<?php

use Carbon\Carbon;
use HomeHello\HHCarbon;
use HomeHello\HHStripe;
use HomeHello\Responses\BookingResponse;
use HomeHello\Responses\HHResponse;

class BookingController extends BaseController
{
    /**
     * PUT /api/v2/bookings/{bookingId}/cancel/{cancelId?}
     *
     * @param $bookingId
     * @return mixed
     */
    public function cancelBooking($bookingId)
    {
        $booking = Booking::find($bookingId);
        if ( ! $booking) {
            return BookingResponse::noContent('listing_single_booking_not_found', 'Booking not found');
        }

        if ($booking->job_status === Booking::STATUS_PAID) { // Paid
            return BookingResponse::noContent('booking_cancellation_already_paid', 'Booking already paid.');
        } elseif ($booking->job_status == Booking::STATUS_CANCELLED) { // Already cancelled
            return BookingResponse::noContent('booking_cancellation_already_cancelled', 'Booking already cancelled.');
        } elseif ( ! $this->user->isAdmin() && Carbon::now()->gte($booking->start_time)) {
            return BookingResponse::error(null, 'It is now past the booking start time and cannot be cancelled.');
        }

        if ($this->user->isCleaner()) {
            return $this->cancelBookingByCleaner($booking);
        } else {
            return $this->cancelBookingByClientOrAdmin($booking);
        }
    }

    /**
     * POST /api/v2/bookings/cleaner/check
     *
     * @return mixed
     */
    public function checkCleanerSteps()
    {
        if ( ! ($cleaner = User::find(Input::get('cleaner_id')))) {
            return BookingResponse::error(null, 'Invalid cleaner ID.');
        }
        $startTime = HHCarbon::parse(Input::get('start_time'));
        $duration = Input::get('duration_selected');

        if ( ! $cleaner->isAvailableAtCarbon($startTime, $duration)) {
            return BookingResponse::gone('check_cleaner_error', 'Cleaner not available anymore.');
        }

        return BookingResponse::ok('check_cleaner_success', 'Cleaner available.');
    }

    /**
     * GET /api/v2/bookings/checkout-update
     *
     * Client updates the booking with additional info (pets, garbage, parking, etc.)
     *
     * @return \Illuminate\Http\JsonResponse
     */
    public function checkoutUpdate()
    {
        if ( ! ($bookingId = Input::get('booking_id'))) {
            return HHResponse::error(null, 'No booking ID was given.');
        }

        $bookingIds = is_array($bookingId) ? $bookingId : [$bookingId];

        $numBookings = Booking::whereIn('id', $bookingIds)->inTheFuture()->jobStatusNew()->count();

        if ($numBookings === 0) {
            return HHResponse::error(null, 'Invalid booking IDs were given.');
        }

        $fillable = ['additional_info', 'garbage', 'parking', 'parking_info', 'pets', 'pets_info', 'restrictions', 'special_attention', 'specifics'];

        $bookings = Booking::whereIn('id', $bookingIds)->inTheFuture()->jobStatusNew()->get();

        foreach ($bookings as $booking) {
            $booking->update(Input::only($fillable));
        }

        return HHResponse::ok();
    }

    /**
     * GET /api/v2/bookings/all/availableTimes
     *
     * Check if cleaner is available in postcode and at time
     *
     * @return Response
     */
    public function findCleaner()
    {
        $blockedCleanerIds = $this->user ? $this->user->blockedCleaners()->lists('cleaner_id') : [];

        $dates = Input::get('dates');
        array_walk($dates, function (&$date) {
            $date = Carbon::parse($date)->format('Y-m-d');
        });

        $timings = Booking::findAvailableCleaners(Input::get('postcode'), $dates, Input::get('duration'), 1, $blockedCleanerIds);

        return HHResponse::ok('find_cleaner_success', 'List available cleaners.', $timings);
    }

    /**
     * GET /api/v2/bookings/{bookingId}/invoice/{invoiceId?}
     *
     * @param int $id Booking ID to generate invoice for
     *
     * @todo - to be fixed
     */
    public function generateInvoice($bookingId, $invoiceId = null)
    {
        $booking = Booking::find($bookingId);
        if ( ! $booking) {
            return BookingResponse::noContent('listing_single_booking_not_found', 'Booking not found');
        }

        $invoiceData = [
            'bookingDate' => $booking->start_time->format('Y-m-d H:i'),
            'invoiceNumber' => $booking->start_time->format('y').sprintf('%05d', $booking->id).$booking->start_time->format('md'),
            'clientName' => $booking->client->u_names,
            'clientStreet' => $booking->address->street,
            'cleanerName' => $booking->cleaners->first()->u_names,
            'cleanerAbn' => $booking->cleaners->first()->userDataCleaners->abn,
        ];

        $invoiceData['items'][] = [
            'name' => 'Standard Cleaning',
            'rates' => '$'.$booking->pricing->client_price,
            'hours' => $booking->duration_selected.' hours x 1 cleaner',
            'amount' => '$'.$booking->duration_selected * $booking->pricing->client_price,
        ];

        if ($booking->cleaning_products) {
            $invoiceData['items'][] = [
                'name' => 'Cleaning Products',
                'rates' => '',
                'hours' => '',
                'amount' => '$5.00',
            ];
        }

        $invoiceData['discount'] = $booking->discount_cost;
        $invoiceData['grandTotal'] = $booking->job_total_cost;

        $html = View::make('invoice.client-invoice')->with($invoiceData);

        echo PDF::load($html, 'A4', 'portrait')->output();

        return '';
    }

    /**
     * PUT /api/v2/bookings/{bookingId}/quickupdate/{quickUpdateId?}
     *
     * @param int $bookingId
     * @param int $quickUpdateId
     * @return mixed
     */
    public function quickUpdate($bookingId)
    {
        $booking = Booking::find($bookingId);
        if ( ! $booking) {
            return BookingResponse::noContent('listing_single_booking_not_found', 'Booking not found');
        }

        try {
            if ($this->user->isAdmin()) {
                Booking::unguard();
            }

            $booking->update(Input::get());
        } catch (Exception $exception) {
            return BookingResponse::badRequest('booking_update_quick_exception_error', $exception);
        }

        return BookingResponse::ok('booking_update_quick_success', 'Booking updated successfully');
    }

    /**
     * POST /api/v2/bookings/{id}/reschedule
     *
     * @param  int $bookingId
     * @return \Illuminate\Http\JsonResponse
     */
    public function reschedule($bookingId)
    {
        try {
            DB::transaction(function () use ($bookingId, &$booking, &$oldCleaner, &$oldBookingStartTime) {
                $booking = Booking::jobStatusNotCancelled()->inTheFuture()->findOrFail($bookingId);
                $oldBookingStartTime = $booking->start_time;

                $booking->mail_reminder = 0;
                $booking->cleaner_confirmed = null;
                $booking->cleaner_sms_response = null;
                $booking->fill(Input::except('frequency'));
                $booking->unapplyCouponIfOutsideMembership();

                if ($booking->start_time->isTomorrow() && Carbon::now()->hour >= 15) {
                    throw new Exception('Since it is now past 3PM you cannot reschedule a booking for tomorrow.');
                } elseif ($booking->start_time->lt(Carbon::tomorrow())) {
                    throw new Exception('You cannot reschedule your booking to such an early date.');
                }

                $oldCleaner = $booking->cleaners->first();

                if ($oldCleaner) {
                    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    | Temporarily unassign cleaner to free up timeslot. |
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
                    $oldBookingAssignmentIds = $booking->bookingAssignedCleaners()->lists('id');
                    BookingAssignedCleaners::whereIn('id', $oldBookingAssignmentIds)->delete();

                    if ( ! $oldCleaner->isAvailableForBooking($booking)) {
                        $booking->assignCleaner(null, null, [$oldCleaner->id]);
                    } else {
                        BookingAssignedCleaners::whereIn('id', $oldBookingAssignmentIds)->restore();
                    }
                } else {
                    $booking->assignCleaner();
                }

                $booking->save();
            });
        } catch (Exception $exception) {
            return HHResponse::error(null, $exception);
        }

        if ($oldCleaner) {
            (new CleanerMailer($oldCleaner))->bookingCancellationNotification($oldBookingStartTime, false);
        }

        (new CleanerMailer($booking->cleaners()->first()))->newBookingConfirmation(new \Illuminate\Support\Collection([$booking]));

        return HHResponse::ok(null, null, $booking);
    }

    /**
     * GET /api/v2/bookings/{bookingId}
     *
     * Display the specified resource.
     *
     * @param int $bookingId
     *
     * @return Response
     */
    public function show($bookingId)
    {
        $booking = Booking::find($bookingId);
        if ( ! $booking) {
            return BookingResponse::noContent('listing_single_booking_not_found', 'Booking not found');
        }

        $load = ['address', 'client.card', 'extras', 'cleaners', 'coupon'];

        if ($booking->client->hasActiveMembership()) {
            $load = array_merge($load, ['client.membership.recurringCoupon']);
        }

        if ($this->user->isAdmin()) {
            $load = array_merge($load, ['kpis', 'bookingNotes']);
        }

        $booking->load($load);
        $booking->endTime = $booking->getEndTime();

        if ( ! $booking->cleaners->isEmpty()) {
            $booking->cleaner_job_payment = $booking->cleaner_payment_final;

            if ($this->user->isClient()) {
                $booking->cleanerRating = $booking->cleaners->first()->averageRating();
                $booking->cleanerAvatar = $booking->cleaners->first()->user_data_cleaners->avatar;
            }
        }

        return HHResponse::ok('booking_details_success', 'Listing booking details', $booking);
    }

    /**
     * POST /api/v2/bookings
     *
     * @return \Illuminate\Http\JsonResponse
     */
    public function store()
    {
        try {
            DB::transaction(function () use (&$client, &$isNewUser, &$temporaryPassword) {
                if ($this->user->isApi()) {
                    if ( ! Input::get('u_email') || ! Input::get('u_phone_number')) {
                        throw new Exception('Please enter your email address and phone number.');
                    }

                    $client = User::whereUEmail(Input::get('u_email'))->first();
                } elseif ($this->user->isClient()) {
                    $client = $this->user;
                } else {
                    throw new Exception('The account you are trying to book with is not registered as a client.');
                }

                $isNewUser = $client ? false : true;

                if ($isNewUser) {
                    $client = $this->createFirstTimeClient($temporaryPassword);
                } else {
                    if (Input::has('payment_cc') || Input::get('client.card.cc_number')) {
                        $this->replaceCustomersOldCreditCard($client);
                    }
                }
            });

            DB::transaction(function () use (&$bookings, &$client) {
                $bookings = $this->createOneOrMultipleBookings($client);

                CheckoutDropoff::whereEmail($client->u_email)->delete();
            });
        } catch (Stripe_Error $exception) {
            return BookingResponse::unprocessableEntity('booking_error_stripe', $exception);
        } catch (Exception $exception) {
            return BookingResponse::unprocessableEntity('booking_error_exception', $exception);
        }

        if ($isNewUser) {
            (new ClientMailer($client))->firstTimeClientBookingConfirmation($client);
        }
        (new ClientMailer($client))->oldClientBookingConfirmation($bookings);

        (new CleanerMailer($bookings->first()->cleaners->first()))->newBookingConfirmation($bookings);

        return BookingResponse::bookingsAddedSuccessfully($bookings, $temporaryPassword, $isNewUser);
    }

    private function cancelBookingByCleaner(Booking $booking)
    {
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        | First unassign the cleaner from the booking. |
        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        DB::transaction(function () use (&$booking, &$oldCleaner) {
            $oldCleaner = $booking->cleaners->first();
            $booking->booking_assigned_cleaners->first()->cancelByCleaner()->save();
        });

        try {
            if ( ! (Carbon::now()->hour >= 15 && $booking->start_time->lte(Carbon::tomorrow()->endOfDay()))) {
                DB::transaction(function () use (&$booking) {
                    $booking->assignCleaner();
                });
            }
        } catch (Exception $exception) {
            return BookingResponse::error(null, 'No cleaner available to reassign booking.');
        } finally {
            (new AdminMailer())->cleanerCancellationNoReplacementsAvailableNotification($booking, $oldCleaner);
        }

        (new ClientMailer($booking->client))->reassignmentNotification($booking);
        (new CleanerMailer($booking->cleaners->first()))->reassignmentConfirmation($booking);

        return BookingResponse::ok('cancel_booking_reassigned_success', 'Successfully reassigned.', $booking);
    }

    private function cancelBookingByClientOrAdmin(Booking $booking)
    {
        try {
            DB::transaction(function () use (&$booking) {
                $booking->job_status = Booking::STATUS_CANCELLED; // Cancelled
                $booking->canceller()->associate($this->user);
                $booking->save();

                if ($this->user->isClient()) {
                    $clientCancellation = ClientCancellation::firstOrNew(['booking_id' => $booking->id]);
                    $clientCancellation->fill(Input::get());
                    $booking->clientCancellation()->save($clientCancellation);
                }
            });
        } catch (Exception $exception) {
            return BookingResponse::error('booking_cancellation_exception', $exception);
        }

        $cancelledByAdmin = $this->user->isAdmin();
        (new ClientMailer($booking->client))->bookingCancellationConfirmation($booking, $cancelledByAdmin);

        if ( ! $booking->cleaners->isEmpty()) {
            (new CleanerMailer($booking->cleaners->first()))->bookingCancellationNotification($booking->start_time, $cancelledByAdmin);
        }
        if ( ! $cancelledByAdmin) {
            (new SupportTeamMailer())->bookingCancellationNotification($booking);
        }

        return BookingResponse::ok('booking_cancelled_success', 'Successfully cancelled.');
    }

    private function createFirstTimeClient(&$temporaryPassword)
    {
        $input = Input::all();
        // get or create address
        $address = Address::firstOrCreate($input['address']);

        $client = new User(Input::get());

        $client->generateApiKey();
        $temporaryPassword = $client->generatePassword();
        $client->role = User::ROLE_CLIENT;
        $client->save();

        // associate address to client.
        $client->addresses()->attach($address);

        // Pre-validate card
        Stripe::setApiKey(Config::get('app.stripe_key'));
        $createCustomer = Stripe_Customer::create([
            'email' => $client->u_email,
            'card' => [
                'name' => $client->u_names,
                'number' => Input::get('payment_cc'),
                'exp_month' => Input::get('exp_month'),
                'exp_year' => Input::get('exp_year'),
                'cvc' => Input::get('payment_cvv'),
            ]
        ]);

        $stripeData = new UserStripe(UserStripe::createFillableArrayUsingStripeCard($createCustomer->cards->data[0]));
        $stripeData->stripe_customer_id = $createCustomer->id;
        $stripeData->client()->associate($client);
        $stripeData->save();

        return $client;
    }

    private function createOneOrMultipleBookings(User $client)
    {
        $startTime = HHCarbon::parse(Input::get('start_time'));
        $frequency = Input::get('frequency.frequency');
        $numberOfBookingsToCreate = $frequency > 0 ? 8 : 1;

        $bookings = new \Illuminate\Support\Collection();
        $couponCode = Input::get('coupon_code', Input::get('coupon.coupon_code'));
        $coupon = $couponCode ? Coupon::whereCouponCode($couponCode)->firstOrFail() : null;

        $address = Address::firstOrCreate(Input::get('address'));

        for ($i = 0; $i < $numberOfBookingsToCreate; ++$i) {
            $booking = new Booking(Input::get());
            $booking->frequency = $frequency;
            $booking->start_time = $i == 0 ? $startTime : $startTime->addDays($frequency);

            $booking->address()->associate($address);
            $booking->client()->associate($client);
            $booking->creator()->associate($client);
            if ($coupon && ($i == 0 || ! $coupon->first_of_recurring)) {
                $booking->applyCoupon($coupon);

                if ($i == 0 && $coupon->triggers_membership) {
                    if ($client->membership) {
                        throw new Exception('Client is already a member.');
                    }

                    $client->membership()->associate($coupon->triggers_membership);
                    $client->membership_created_at = Carbon::now();
                    $client->membership_ends_at = $booking->start_time->addMonths($coupon->triggers_membership->duration_months)->endOfDay();
                    $client->save();

                    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    | After the trigger coupon has been applied, the rest of the bookings |
                    | must get the recurring coupon if within the membership lifetime.    |
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
                    $coupon = $client->membership->recurring_coupon;
                }
            }
            $booking->save();

            // TODO: REMOVE THIS QUICKFIX FOR REBOOKING
            if (Input::has('services.0')) {
                $extraKeys = Input::get('services', []);
            } else {
                $extraKeys = Input::has('services') ? array_keys(array_filter(Input::get('services'))) : [];
            }

            $extraIds = Extra::whereIn('extra_key', $extraKeys)->lists('id');
            $booking->extras()->sync($extraIds);

            $preferredCleaner = User::find(Input::get('cleaner_id'));

            $booking->assignCleaner(null, $preferredCleaner);

            $bookings->push($booking);
        }

        return $bookings;
    }

    private function replaceCustomersOldCreditCard(User $client)
    {
        if (Input::has('client.card.cc_number')) {
            $clientCardData = Input::get('client.card');

            $stripeCardData = array_only($clientCardData, ['exp_month', 'exp_year']) + [
                'number' => array_get($clientCardData, 'cc_number'),
                'cvc' => array_get($clientCardData, 'cvv'),
                'name' => array_get($clientCardData, 'cardholder'),
            ];
        } else {
            $stripeCardData = Input::only('exp_month', 'exp_year') + [
                'number' => Input::get('payment_cc'),
                'cvc' => Input::get('payment_cvv'),
                'name' => Input::get('cardholder'),
            ];
        }

        HHStripe::replaceCustomersOldCreditCard($client, $stripeCardData);
    }
}
