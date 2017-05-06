<?php

/*
 *  Overriding the subscribe function
 */

remove_action('after_theme_setup', 'subscribe');

function subscribe($order_id, $first_name, $last_name, $email, $listid = 'false') {
    global $woocommerce;
    global $product;
    $items = $woocommerce->cart->get_cart();

    foreach ($items as $item => $values) {
        $p_title = get_the_title($values['product_id']);
    }
    if (!$email)
        return; // Email is required


    if ($p_title == 'test1') {
        $listid = '9ef7e0936f';
    } else if ($p_title == 'test2') {
        $listid = '068b3d08bd';
    } else if ($p_title == 'test3') {
        $listid = '506a68d7a6';
    } else if ($p_title == 'test4') {
        $listid = '6bc66ed52f';
    } else if ($p_title == 'test5') {
        $listid = '34d6a2e18d';
    } else {
        $listid = '3d1e44aa76';
    }

    //$listid = $this->list;

    $api = new MCAPI($this->api_key);

    $merge_vars = array('FNAME' => $first_name, 'LNAME' => $last_name);

    if (!empty($this->interest_groupings) && !empty($this->groups)) {
        $merge_vars['GROUPINGS'] = array(
            array('name' => $this->interest_groupings, 'groups' => $this->groups),
        );
    }

    $vars = apply_filters('ss_wc_mailchimp_subscribe_merge_vars', $merge_vars, $order_id);

    $email_type = 'html';
    $double_optin = ( $this->double_optin == 'no' ? false : true );
    $update_existing = true;
    $replace_interests = false;
    $send_welcome = false;

    self::log('Calling MailChimp API listSubscribe method with the following: ' .
            'listid=' . $listid .
            ', email=' . $email .
            ', vars=' . print_r($vars, true) .
            ', email_type=' . $email_type .
            ', double_optin=' . $double_optin .
            ', update_existing=' . $update_existing .
            ', replace_interests=' . $replace_interests .
            ', send_welcome=' . $send_welcome
    );
    $retval = $api->listSubscribe($listid, $email, $vars, $email_type, $double_optin, $update_existing, $replace_interests, $send_welcome);

    $retvalchimp = $api->listSubscribe('2f381dd0c7', $email, $vars, $email_type, $double_optin, $update_existing, $replace_interests, $send_welcome);

    self::log('MailChimp return value:' . $retval);

    if ($api->errorCode && $api->errorCode != 214) {
        self::log('WooCommerce MailChimp subscription failed: (' . $api->errorCode . ') ' . $api->errorMessage);

        do_action('ss_wc_mailchimp_subscribed', $email);

        // Email admin
        wp_mail(get_option('admin_email'), __('WooCommerce MailChimp subscription failed', 'ss_wc_mailchimp'), '(' . $api->errorCode . ') ' . $api->errorMessage);
    }
}
