<?php
/*
 * Override wp_pagenavi function
 */
remove_action('after_theme_setup','wp_pagenavi');
function wp_pagenavi( $args = array() ) {
    if ( !is_array( $args ) ) {
		$argv = func_get_args();

		$args = array();
		foreach ( array( 'before', 'after', 'options' ) as $i => $key )
			$args[ $key ] = isset( $argv[ $i ]) ? $argv[ $i ] : "";
	}

	$args = wp_parse_args( $args, array(
		'before' => '',
		'after' => '',
		'options' => array(),
		'query' => $GLOBALS['wp_query'],
		'type' => 'posts',
		'echo' => true
	) );

	extract( $args, EXTR_SKIP );

	$options = wp_parse_args( $options, PageNavi_Core::$options->get() );

	$instance = new PageNavi_Call( $args );

	list( $posts_per_page, $paged, $total_pages ) = $instance->get_pagination_args();

	if ( 1 == $total_pages && !$options['always_show'] )
		return;

	$pages_to_show = absint( $options['num_pages'] );
	$larger_page_to_show = absint( $options['num_larger_page_numbers'] );
	$larger_page_multiple = absint( $options['larger_page_numbers_multiple'] );
	$pages_to_show_minus_1 = $pages_to_show - 1;
	$half_page_start = floor( $pages_to_show_minus_1/2 );
	$half_page_end = ceil( $pages_to_show_minus_1/2 );
	$start_page = $paged - $half_page_start;

	if ( $start_page <= 0 )
		$start_page = 1;

	$end_page = $paged + $half_page_end;

	if ( ( $end_page - $start_page ) != $pages_to_show_minus_1 )
		$end_page = $start_page + $pages_to_show_minus_1;

	if ( $end_page > $total_pages ) {
		$start_page = $total_pages - $pages_to_show_minus_1;
		$end_page = $total_pages;
	}

	if ( $start_page < 1 )
		$start_page = 1;

	$out = '';
	switch ( intval( $options['style'] ) ) {
		// Normal
		case 1:
			// Text
			if ( !empty( $options['pages_text'] ) ) {
				$pages_text = str_replace(
					array( "%CURRENT_PAGE%", "%TOTAL_PAGES%" ),
					array( number_format_i18n( $paged ), number_format_i18n( $total_pages ) ),
				$options['pages_text'] );
				$out .= "<span class='pages'>$pages_text</span>";
			}

			if ( $start_page >= 2 && $pages_to_show < $total_pages ) {
				// First
				$first_text = str_replace( '%TOTAL_PAGES%', number_format_i18n( $total_pages ), $options['first_text'] );
				$out .= $instance->get_single( 1, $first_text, array(
					'class' => 'first'
				), '%TOTAL_PAGES%' );
			}

			// Previous
			if ( $paged > 1 && !empty( $options['prev_text'] ) ) {
				$out .= $instance->get_single( $paged - 1, $options['prev_text'], array(
					'class' => 'previouspostslink',
					'rel'	=> 'prev'
				) );
			}

			if ( $start_page >= 2 && $pages_to_show < $total_pages ) {
				if ( !empty( $options['dotleft_text'] ) )
					$out .= "<span class='extend'>{$options['dotleft_text']}</span>";
			}

			// Smaller pages
			$larger_pages_array = array();
			if ( $larger_page_multiple )
				for ( $i = $larger_page_multiple; $i <= $total_pages; $i+= $larger_page_multiple )
					$larger_pages_array[] = $i;

			$larger_page_start = 0;
			foreach ( $larger_pages_array as $larger_page ) {
				if ( $larger_page < ($start_page - $half_page_start) && $larger_page_start < $larger_page_to_show ) {
					$out .= $instance->get_single( $larger_page, $options['page_text'], array(
						'class' => 'smaller page',
					) );
					$larger_page_start++;
				}
			}

			if ( $larger_page_start )
				$out .= "<span class='extend'>{$options['dotleft_text']}</span>";

			// Page numbers
			$timeline = 'smaller';
			foreach ( range( $start_page, $end_page ) as $i ) {
				if ( $i == $paged && !empty( $options['current_text'] ) ) {
					$current_page_text = str_replace( '%PAGE_NUMBER%', number_format_i18n( $i ), $options['current_text'] );
					$out .= "<span class='current'>$current_page_text</span>";
					$timeline = 'larger';
				} else {
					$out .= $instance->get_single( $i, $options['page_text'], array(
						'class' => "page $timeline",
					) );
				}
			}

			// Large pages
			$larger_page_end = 0;
			$larger_page_out = '';
			foreach ( $larger_pages_array as $larger_page ) {
				if ( $larger_page > ($end_page + $half_page_end) && $larger_page_end < $larger_page_to_show ) {
					$larger_page_out .= $instance->get_single( $larger_page, $options['page_text'], array(
						'class' => 'larger page',
					) );
					$larger_page_end++;
				}
			}

			if ( $larger_page_out ) {
				$out .= "<span class='extend'>{$options['dotright_text']}</span>";
			}
			$out .= $larger_page_out;

			if ( $end_page < $total_pages ) {
				if ( !empty( $options['dotright_text'] ) )
					$out .= "<span class='extend'>{$options['dotright_text']}</span>";
			}

			// Next
			if ( $paged < $total_pages && !empty( $options['next_text'] ) ) {
				$out .= $instance->get_single( $paged + 1, $options['next_text'], array(
					'class' => 'nextpostslink',
					'rel'	=> 'next'
				) );
			}

			if ( $end_page < $total_pages ) {
				// Last
				$out .= $instance->get_single( $total_pages, $options['last_text'], array(
					'class' => 'last',
				), '%TOTAL_PAGES%' );
			}
			break;

		// Dropdown
		case 2:
			$out .= '<form action="" method="get">'."\n";
			$out .= '<select size="1" onchange="document.location.href = this.options[this.selectedIndex].value;">'."\n";

			foreach ( range( 1, $total_pages ) as $i ) {
				$page_num = $i;
				if ( $page_num == 1 )
					$page_num = 0;

				if ( $i == $paged ) {
					$current_page_text = str_replace( '%PAGE_NUMBER%', number_format_i18n( $i ), $options['current_text'] );
					$out .= '<option value="'.esc_url( $instance->get_url( $page_num ) ).'" selected="selected" class="current">'.$current_page_text."</option>\n";
				} else {
					$page_text = str_replace( '%PAGE_NUMBER%', number_format_i18n( $i ), $options['page_text'] );
					$out .= '<option value="'.esc_url( $instance->get_url( $page_num ) ).'">'.$page_text."</option>\n";
				}
			}

			$out .= "</select>\n";
			$out .= "</form>\n";
			break;
	}
	$out = $before . "<div class='wp-pagenavi'>\n$out\n</div>" . $after;

	$out = apply_filters( 'wp_pagenavi', $out );

	if ( !$echo )
		return $out;

	echo $out;
}