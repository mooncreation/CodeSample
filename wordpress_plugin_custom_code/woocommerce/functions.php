<?php

/*
 * Woocommere Admin new order class override
 */

class WC_Email_New_Order_child extends WC_Email_New_Order_child {
    
    function __construct() {
        add_action( 'woocommerce_order_status_pending_to_processing_notification', array( $this, 'trigger' ) );
        add_action( 'woocommerce_order_status_pending_to_completed_notification', array( $this, 'trigger' ) );
        add_action( 'woocommerce_order_status_pending_to_on-hold_notification', array( $this, 'trigger' ) );
        add_action( 'woocommerce_order_status_failed_to_processing_notification', array( $this, 'trigger' ) );
        add_action( 'woocommerce_order_status_failed_to_completed_notification', array( $this, 'trigger' ) );
        add_action( 'woocommerce_order_status_failed_to_on-hold_notification', array( $this, 'trigger' ) );
    }
    function trigger( $order_id ) {
        if ( $order_id ) {
			$this->object 		= wc_get_order( $order_id );

			$this->find['order-date']      = '{order_date}';
			$this->find['order-number']    = '{order_number}';
			
			$this->replace['order-date']   = date_i18n( wc_date_format(), strtotime( $this->object->order_date ) );
			$this->replace['order-number'] = $this->object->get_order_number();
		}

		if ( ! $this->is_enabled() || ! $this->get_recipient() ) {
			return;
		}		
	
		add_filter( 'wp_mail_from', function( $email ) { return 'test_test@test1.com'; } );

		$recepient = 'test_test@test.com';
					
		//admin different emails for different categories
		$order = new WC_Order( $order_id );
		$items = $order->get_items();
		foreach($items as $pids)
		{
			$product_cats = wp_get_post_terms( $pids['product_id'], 'product_cat' );
			foreach ( $product_cats as $category ) {
				if($category->name == 'test_test')
				{
					$recepient = 'test_test@test1.com';

					add_filter( 'wp_mail_from', function( $email ) { return 'test_test@test1.com'; } );
					break;
				}
			}		
		}
		
		// Filters for the email
		//add_filter( 'wp_mail_from', array( $this, 'get_from_address' ) );
		add_filter( 'wp_mail_from_name', array( $this, 'get_from_name' ) );
		add_filter( 'wp_mail_content_type', array( $this, 'get_content_type' ) );
		
		// Send
		wp_mail( $recepient, $this->get_subject(), $this->get_content(), $this->get_headers(), $this->get_attachments() );
		
    }
}

/*
 * Woocommere Customer Processing order class override
 */

class WC_Email_Customer_Processing_Order_child extends WC_Email_Customer_Processing_Order {
    
    function __construct() {
		// Triggers for this email
		add_action( 'woocommerce_order_status_pending_to_processing_notification', array( $this, 'trigger' ) );
		add_action( 'woocommerce_order_status_pending_to_on-hold_notification', array( $this, 'trigger' ) );

		// Call parent constructor
		parent::__construct();
	}
        
    function trigger( $order_id ) {

		if ( $order_id ) {
			$this->object 		= wc_get_order( $order_id );
			//$this->recipient	= $this->object->billing_email;
			$this->recipient	= $this->object->get_user()->user_email;

			$this->find['order-date']      = '{order_date}';
			$this->find['order-number']    = '{order_number}';
			
			$this->replace['order-date']   = date_i18n( wc_date_format(), strtotime( $this->object->order_date ) );
			$this->replace['order-number'] = $this->object->get_order_number();
		}

		if ( ! $this->is_enabled() || ! $this->get_recipient() ) {
			return;
		}
		
		add_filter( 'wp_mail_from', function( $email ) { return 'test@test.com'; } );
		
		add_filter( 'wp_mail_from_name', array( $this, 'get_from_name' ) );
		add_filter( 'wp_mail_content_type', array( $this, 'get_content_type' ) );
		
		wp_mail( $this->get_recipient(), $this->get_subject(), $this->get_content(), $this->get_headers(), $this->get_attachments() );
	}
}