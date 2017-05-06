<?php

/**
 * CardLess exceptions
 *
 * @package CardLess\Exceptions
 */

/**
 * Exceptions pertaining to the client object
 *
 * @return exception
 */
class CardLess_ClientException extends Exception {

  /**
   * Throw a default exception
   *
   * @param string $description Description of the error
   */
  public function __construct($description = 'Unknown client error') {
    parent::__construct($description);
  }

}

/**
 * Exceptions pertaining to the arguments used in a function
 *
 * @return exception
 */
class CardLess_ArgumentsException extends Exception {

  /**
   * Throw a default exception
   *
   * @param string $description Description of the error
   */
  public function __construct($description = 'Unknown argument error') {
    parent::__construct($description);
  }

}

/**
 * Exceptions pertaining to the CardLess API
 *
 * @return exception
 */
class CardLess_ApiException extends Exception {

  /**
   * Throw a default exception
   *
   * @param string $description Description of the error
   * @param integer $code The returned error code
   */
  public function __construct($description = 'Unknown error', $code = 0) {
    if (empty($description)) {
      $description = 'Unknown error';
    }
    parent::__construct($description, $code);
  }

}

/**
 * Exceptions pertaining to the signature
 *
 * @return exception
 */
class CardLess_SignatureException extends Exception {

  /**
   * Throw a default exception
   *
   * @param string $description Description of the error
   */
  public function __construct($description = 'Signature error') {
    parent::__construct($description);
  }

}
