<?php
/*
  Plugin Name: 3-Tier approach Calculator
  Plugin URI: http://acquaintsoft.com/
  Description: Calculator Plugin
  Version: 1.0.1
  Author: Acquaint Softech
  Author URI: RI: http://acquaintsoft.com/
 */
/*  The plugin is useful for calculation of the 3-tier NOx Emission. It calculates the RPM and Tiers with finalized results. 
 */

/**
 * Register and enqueue style sheet.
 */
wp_register_style('helloworldgen', plugins_url('hello-world-generator/css/calc.css'));
wp_enqueue_style('helloworldgen');
global $calc_entry;
$calc_entry = "calc_entry";

function calculator_install() {
    global $wpdb, $calc_entry;
}

//end install

register_activation_hook(__FILE__, 'calculator_install');

function calculator_uninstall() {
    
}

register_deactivation_hook(__FILE__, 'calculator_uninstall');

// RPM Calculation with tier and amount field
function rmpcalculation($selectedTier, $rpm, $selected = '') {
    if ($selectedTier == 'tier1') {
        if ($rpm >= 130 && $rpm <= 2000) {
            $power = pow($rpm, -0.2);
            $finalRmp = intval(45.0) * $power;
        } else if ($rpm < 130) {
            $finalRmp = '17,0';
        } else if ($rpm > 2000) {
            $finalRmp = '9,8';
        }
    } else if ($selectedTier == 'tier2') {
        if ($rpm >= 130 && $rpm <= 2000) {
            $power = pow($rpm, -0.23);
            $finalRmp = intval(44.0) * $power;
        } else if ($rpm < 130) {
            $finalRmp = '14,4';
        } else if ($rpm > 2000) {
            $finalRmp = '7,7';
        }
    } else if ($selectedTier == 'tier3') {
        if ($rpm >= 130 && $rpm <= 2000) {
            $power = pow($rpm, -0.23);
            $finalRmp = intval(9.0) * $power;
        } else if ($rpm < 130) {
            $finalRmp = '3,4';
        } else if ($rpm > 2000) {
            $finalRmp = '2,0';
        }
    }
    return $finalRmp . ' <sup>g</sup>/kWh';
}

// Form Submit with post method
function calculator_get_random_snippet() {
    if (isset($_REQUEST['action']) && $_REQUEST['action'] == 'rpmsubmit') {
        $selectedTier = $_REQUEST['tiers'];
        $rpm = $_REQUEST['rpm'];
        $errorMessage = '';
        if ($selectedTier == '') {
            $errorMessage .= 'Please Select a Tier<br />';
        }
        if ($rpm == '') {
            $errorMessage .= 'Please Enter a Valid RPM Value<br />';
        } else if (!preg_match('/^[0-9]+$/', $rpm)) {
            $errorMessage .= 'Please Enter a Numeric Value for RPM Field<br />';
        }

        // get Tier Selected option

        if ($selectedTier == 'tier1') {
            $selected = 'tier1';
        } else if ($selectedTier == 'tier2') {
            $selected = 'tier2';
        } else if ($selectedTier == 'tier3') {
            $selected = 'tier3';
        }

        if ($errorMessage == '') {
            $result = rmpcalculation($selectedTier, $rpm);
        }
    }

    // Display Error message on form submission
    ?>


    <div class="errormessages">
    <?php
    if (isset($errorMessage) && $errorMessage != '') {
        echo $errorMessage;
    }
    ?>
    </div>
    <form action="" name="rpmform" method="post"> 
        <p>
            <label for="my_meta_box_select">Tiers : </label>
            <select name="tiers" id="tiers" class="selectbox">
                <option value="">Select Tier</option>
                <option value="tier1" <?php selected($selected, 'tier1'); ?>>Tier1</option>
                <option value="tier2" <?php selected($selected, 'tier2'); ?>>Tier2</option>
                <option value="tier3" <?php selected($selected, 'tier3'); ?>>Tier3</option>
            </select>
        </p>
        <p>
            <label for="my_meta_box_select">RPM : </label>
            <input type="text" name="rpm" value="" id="rpm" />
        </p>
        <p>

            <input type="submit" name="submit" value="Submit" />
        </p>
        <input type="hidden" name="action" value="rpmsubmit" />
    </form>

    <div class="resultrpm">
        <h2>Result : <span><?php echo $result == '' ? 'No Result' : $result; ?></span></h2>
    </div>
        <?php
    }

// Calculator shortcode
    add_shortcode('calc', 'calculator_get_random_snippet');
    ?>
