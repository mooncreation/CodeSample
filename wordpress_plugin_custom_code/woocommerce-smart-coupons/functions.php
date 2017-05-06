<?php
/*
 *  Updating the coupon display on my account page.
 */

class WC_Smart_Coupons_child extends WC_Smart_Coupons_child {

    function get_generated_coupon_data_box($generated_coupon_data = array()) {
        if (empty($generated_coupon_data))
            return;
        global $woocommerce, $post, $product;
        $js = "
                        var switchMoreLess = function() {
                            var total = jQuery('details').length;
                            var open = jQuery('details[open]').length;
                            if ( open == total ) {
                                jQuery('a#more_less').text('" . __('Less details', 'wc_smart_coupons') . "');
                            } else {
                                jQuery('a#more_less').text('" . __('More details', 'wc_smart_coupons') . "');
                            }
                        };
                        switchMoreLess();
                        jQuery('a#more_less').on('click', function(){
                            var current = jQuery('details').attr('open');
                            if ( current == '' || current == undefined ) {
                                jQuery('details').attr('open', 'open');
                                jQuery('a#more_less').text('" . __('Less details', 'wc_smart_coupons') . "');
                            } else {
                                jQuery('details').removeAttr('open');
                                jQuery('a#more_less').text('" . __('More details', 'wc_smart_coupons') . "');
                            }
                        });
                        jQuery('summary.generated_coupon_summary').on('mouseup', function(){
                            setTimeout( switchMoreLess, 10 );
                        });
                        jQuery('span.expand_collapse').show();
                    ";
        if (Smart_Coupons_WC_Compatibility::is_wc_21()) {
            wc_enqueue_js($js);
        } else {
            $woocommerce->add_inline_js($js);
        }
        ?>
        <style type="text/css">
            .coupon-container {
                margin: .2em;
                box-shadow: 0 0 5px #e0e0e0;
                display: inline-table;
                text-align: center;
                cursor: pointer;
            }
            .coupon-container.previews { cursor: inherit }
            .coupon-container.blue { background-color: #e0f7ff }
            .coupon-container.red { background-color: #ffe0e0 }
            .coupon-container.green { background-color: #e0ffe0 }
            .coupon-container.yellow { background-color: #f7f7e0 }
            .coupon-container.small {
                padding: .3em;
                line-height: 1.2em;
            }
            .coupon-container.medium {
                padding: .4em;
                line-height: 1.4em;
            }
            .coupon-container.large {
                padding: .5em;
                line-height: 1.6em;
            }
            .coupon-content.small { padding: .2em 1.2em }
            .coupon-content.medium { padding: .4em 1.4em }
            .coupon-content.large { padding: .6em 1.6em }
            .coupon-content.dashed { border: 2.3px dashed }
            .coupon-content.dotted { border: 2.3px dotted }
            .coupon-content.groove { border: 2.3px groove }
            .coupon-content.solid { border: 2.3px solid }
            .coupon-content.none { border: 2.3px none }
            .coupon-content.blue { border-color: #c0d7ee }
            .coupon-content.red { border-color: #eec0c0 }
            .coupon-content.green { border-color: #c0eec0 }
            .coupon-content.yellow { border-color: #e0e0c0 }
            .coupon-content .code {
                font-family: monospace;
                font-size: 1.2em;
                font-weight:700;
            }
            .coupon-content .coupon-expire,
            .coupon-content .discount-info {
                font-family: Helvetica, Arial, sans-serif;
                font-size: 1em;
            }
            .generated_coupon_summary { margin: 0.8em 0.8em; }
            .generated_coupon_details { margin-left: 2em; margin-bottom: 1em; margin-right: 2em; text-align: left; }
            .generated_coupon_data { border: solid 1px lightgrey; margin-bottom: 5px; margin-right: 5px; width: 50%; }
            .generated_coupon_details p { margin: 0; }
            span.expand_collapse { text-align: right; display: block; margin-bottom: 1em; cursor: pointer; }
            .float_right_block { float: right; }
            summary::-webkit-details-marker { display: none; }
            details[open] summary::-webkit-details-marker { display: none; }
        </style>
        <div class="generated_coupon_data_wrapper">
            <span class="expand_collapse" style="display: none;">
                <a id="more_less"><?php _e('More details', 'wc_smart_coupons'); ?></a>
            </span>
            <?php
            foreach ($generated_coupon_data as $from => $data) {
                foreach ($data as $coupon_data) {
                    $coupon = new WC_Coupon($coupon_data['code']);

                    if (empty($coupon->id) || intval($coupon->usage_count) >= intval($coupon->usage_limit))
                        continue; // skip display empty or used coupons

                    $coupon_meta = $this->get_coupon_meta_data($coupon);
                    ?>
                    <div class="coupon-container blue medium" style="float:left; text-align: center;">
                        <details>
                            <summary class="generated_coupon_summary">
                                <?php
                                echo '<div class="coupon-content blue dashed small">';
                                //echo '<div class="discount-info">';
                                //echo( ( !empty( $coupon_meta['coupon_amount'] ) ) ? $coupon_meta['coupon_amount'] : '' )." ". ( ( !empty( $coupon_meta['coupon_type'] ) ) ? $coupon_meta['coupon_type'] : '' );
                                //echo '</div>';
                                echo '<div class="code">' . $coupon->code . '</div>';
                                if (!empty($coupon->expiry_date)) {
                                    $expiry_date = $this->get_expiration_format($coupon->expiry_date);
                                    echo '<div class="coupon-expire">' . $expiry_date . '</div>';
                                } else {
                                    echo '<div class="coupon-expire">' . __('Never Expires ', 'wc_smart_coupons') . '</div>';
                                }

                                echo '</div>';
                                ?>
                            </summary>
                            <div class="generated_coupon_details">

                                <p><strong><?php _e('Recipient', 'wc_smart_coupons'); ?>:</strong> <?php echo $coupon_data['email']; ?></p>
                                <?php if (!empty($coupon_data['message'])) { ?>                                        
                                    <p><strong><?php _e('Message', 'wc_smart_coupons'); ?>:</strong> <?php echo $coupon_data['message']; ?></p>
                <?php } ?>
                            </div>
                        </details>
                    </div>
                    <div style="float:left;margin-left: 5px;" class="coupon-mobile-text">
                        <?php
                        echo '<b>This coupon entitles you to the following features.</b><br>';

                        $parent_coupon_id = $coupon->coupon_custom_fields['parent_coupon_id'][0];
                        $parent_coupon_code = $coupon->coupon_custom_fields['parent_coupon_code'][0];

                        if ($coupon->code == '54db2ff70c782') {
                            ?>
                            <!-- Custom code -->
                            <?php
                        } else if ($parent_coupon_id == '5684') {
                            ?>
                            <!-- Custom code -->
                            <?php
                        } else {
                            ?>
                            <!-- Custom code -->
                            <?php
                        }
                        ?>
                    </div>
                    <div style="clear:both;"></div>
                        <?php
                    }
                }
                ?>
        </div>
            <?php
        }

    }
    