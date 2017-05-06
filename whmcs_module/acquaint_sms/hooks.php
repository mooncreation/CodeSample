<?php
/* WHMCS SMS Addon with GNU/GPL Licence
 * */
if (!defined("WHMCS"))
	die("This file cannot be accessed directly");

require_once("smsclass.php");
$class = new AcquaintSms();
$hooks = $class->getHooks();

foreach($hooks as $hook){
    add_hook($hook['hook'], 1, $hook['function'], "");
}