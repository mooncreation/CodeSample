<?php

/*
 * Adding Venue function
 */

remove_Action('after_theme_setup', 'tribe_get_venue_website_link');

function tribe_get_venue_website_link($post_id = null, $label = null) {
    $post_id = tribe_get_venue_id($post_id);
    $url = tribe_get_event_meta($post_id, '_VenueURL', true);
    if (!empty($url)) {
        $label = is_null($label) ? $url : $label;
        if (!empty($url)) {
            $parseUrl = parse_url($url);
            if (empty($parseUrl['scheme'])) {
                $url = "http://$url";
            }
        }
        $html = sprintf(
                '<a href="%s" target="%s" class="tribe-events-gcal tribe-events-button">Event Website</a>', esc_url($url), apply_filters('tribe_get_venue_website_link_target', 'self'), apply_filters('tribe_get_venue_website_link_label', $label)
        );
    } else {
        $html = '';
    }

    return apply_filters('tribe_get_venue_website_link', $html);
}
