<?php

/**
 * CardLess pre-authorisation functions
 *
 * @package CardLess\PreAuthorization
 */

/**
 * CardLess pre-authorization class
 *
 */
class CardLess_PreAuthorization extends CardLess_Resource {

  /**
   * The API endpoint for pre-authorizations
   *
   * @var string $endpoint
   */
  public static $endpoint = '/pre_authorizations';

  /**
   * Instantiate a new instance of the pre-auth object
   *
   * @param object $client The client to use for the pre-auth object
   * @param array $attrs The properties of the pre-auth
   *
   * @return object The pre-auth object
   */
  function __construct($client, array $attrs = null) {

    $this->client = $client;

    if (is_array($attrs)) {
      foreach ($attrs as $key => $value) {
        $this->$key = $value;
      }
    }

  }

  /**
   * Fetch a pre-authorisation object from the API
   *
   * @param string $id The id of the pre-authorisation to fetch
   *
   * @return object The pre-authorisation object
   */
  public static function find($id) {

    $endpoint = self::$endpoint . '/' . $id;

    return new self(CardLess::$client, CardLess::$client->request('get',
      $endpoint));

  }

  /**
   * Fetch a bill item from the API
   *
   * @param object $client The client object to use to make the query
   * @param string $id The id of the bill to fetch
   *
   * @return object The bill object
   */
  public static function find_with_client($client, $id) {

    $endpoint = self::$endpoint . '/' . $id;

    return new self($client, $client->request('get', $endpoint));

  }

  /**
   * Create a bill under an existing pre-authorization
   *
   * @param array $attrs The properties of the bill being created
   *
   * @return object The pre-authorisations object
   */
  public function create_bill($attrs) {

    if ( ! isset($attrs['amount'])) {
      throw new CardLess_ArgumentsException('Amount required');
    }

    $params = array(
      'bill' => array(
        'amount'                => $attrs['amount'],
        'pre_authorization_id'  => $this->id
      )
    );

    if (isset($attrs['name'])) {
      $params['bill']['name'] = $attrs['name'];
    }

    if (isset($attrs['description'])) {
      $params['bill']['description'] = $attrs['description'];
    }

    $endpoint = CardLess_Bill::$endpoint;

    return new CardLess_Bill($this->client, $this->client->request('post',
      $endpoint, $params));

  }

  /**
   * Cancel a pre-authorisation
   *
   * @return object The result of the cancel query
   */
  public function cancel() {

    $endpoint = self::$endpoint . '/' . $this->id . '/cancel';

    return new self($this->client, $this->client->request('put', $endpoint));

  }

}
