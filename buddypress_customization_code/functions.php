<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/*
 * Add theme support
 */

add_theme_support( 'buddypress' );

/*
 * Defining custom slugs
 */

// change 'discuss' to whatever you want
define( 'BP_FORUMS_SLUG', 'discuss' );

/*
 * Removing the links automatically created in a member’s profile
 */

function remove_xprofile_links() {
    remove_filter( 'bp_get_the_profile_field_value', 'xprofile_filter_link_profile_data', 50, 2 );
}
add_action( 'plugins_loaded', 'remove_xprofile_links' );

/*
 * Append Excerpt text
 */

function cc_excerpt_append_text() {
return 'Stop Reading';
}
add_filter( 'bp_excerpt_append_text', 'cc_excerpt_append_text' );


/* Blog registration template tags */
 
function bp_blog_signup_enabled() {
    global $bp;
 
    $active_signup = isset( $bp->site_options['registration'] ) ? $bp->site_options['registration'] : 'all';
 
    $active_signup = apply_filters( 'wpmu_active_signup', $active_signup ); // return "all", "none", "blog" or "user"
 
    if ( 'none' == $active_signup || 'user' == $active_signup )
        return false;
 
    return true;
}

/*
 * Remove gravator
 */

function remove_gravatar ($avatar, $id_or_email, $size, $default, $alt) {
 
    $default = get_stylesheet_directory_uri() .'/_inc/images/bp_default_avatar.jpg';
    return &quot;&lt;img alt='{$alt}' src='{$default}' class='avatar avatar-{$size} photo avatar-default' height='{$size}' width='{$size}' /&gt;&quot;;
}
 
add_filter('get_avatar', 'remove_gravatar', 1, 5);
 
function bp_remove_signup_gravatar ($image) {
 
    $default = get_stylesheet_directory_uri() .'/_inc/images/bp_default_avatar.jpg';
 
    if( $image &amp;&amp; strpos( $image, &quot;gravatar.com&quot; ) ){ 
 
        return '&lt;img src=&quot;' . $default . '&quot; alt=&quot;avatar&quot; class=&quot;avatar&quot; width=&quot;150&quot; height=&quot;150&quot; /&gt;';
    } else {
        return $image;
    }
 
}
add_filter('bp_get_signup_avatar', 'bp_remove_signup_gravatar', 1, 1 );