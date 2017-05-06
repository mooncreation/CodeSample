<?php
//Display Fields
add_action('woocommerce_product_after_variable_attributes', 'variable_fields', 10, 2);
//JS to add fields for new variations
add_action('woocommerce_product_after_variable_attributes_js', 'variable_fields_js');
//Save variation fields
add_action('woocommerce_process_product_meta_variable', 'save_variable_fields', 10, 1);

/**
 * Create new fields for variations
 *
 */
function variable_fields($loop, $variation_data) {
    ?>

    <tr>
        <td>
    <?php
// Textarea
    woocommerce_wp_textarea_input(
            array(
                'id' => '_textarea[' . $loop . ']',
                'label' => __('Extra Text For Display', 'woocommerce'),
                'placeholder' => '',
                'description' => __('Enter the custom value here.', 'woocommerce'),
                'value' => $variation_data['_textarea'][0],
            )
    );
    ?>
        </td>
    </tr>
    <tr>
<td>
<?php
// Textarea
woocommerce_wp_text_input(
array(
'id' => '_text['.$loop.']',
'label' => __( 'Enter View Point for Display', 'woocommerce' ),
'placeholder' => '',
'description' => __( 'Enter View Point value here.', 'woocommerce' ),
'value' => $variation_data['_text'][0],
)
);
?>
</td>
</tr>
    <tr>
        <td>
    <?php
// Hidden field
    woocommerce_wp_hidden_input(
            array(
                'id' => '_hidden_field[' . $loop . ']',
                'value' => 'hidden_value'
            )
    );
    ?>
        </td>
    </tr>
    <?php
}

/**
 * Create new fields for new variations
 *
 */
function variable_fields_js() {
    ?>


    <tr>
        <td>
    <?php
// Textarea
    woocommerce_wp_textarea_input(
            array(
                'id' => '_textarea[ + loop + ]',
                'label' => __('Extra Text For Display', 'woocommerce'),
                'placeholder' => '',
                'description' => __('Enter the custom value here.', 'woocommerce'),
                'value' => $variation_data['_textarea'][0],
            )
    );
    ?>
        </td>
    </tr>


    <tr>
        <td>
    <?php
// Hidden field
    woocommerce_wp_hidden_input(
            array(
                'id' => '_hidden_field[ + loop + ]',
                'value' => 'hidden_value'
            )
    );
    ?>
        </td>
    </tr>
    <?php
}

/**
 * Save new fields for variations
 *
 */
function save_variable_fields( $post_id ) {
if (isset( $_POST['variable_sku'] ) ) :
$variable_sku = $_POST['variable_sku'];
$variable_post_id = $_POST['variable_post_id'];

// Textarea
$_textarea = $_POST['_textarea'];
for ( $i = 0; $i < sizeof( $variable_sku ); $i++ ) :
$variation_id = (int) $variable_post_id[$i];
if ( isset( $_textarea[$i] ) ) {
update_post_meta( $variation_id, '_textarea', stripslashes( $_textarea[$i] ) );
}
endfor;

// TextField
$_text = $_POST['_text'];
for ( $i = 0; $i < sizeof( $variable_sku ); $i++ ) :
$variation_id = (int) $variable_post_id[$i];
if ( isset( $_text[$i] ) ) {
update_post_meta( $variation_id, '_text', stripslashes( $_text[$i] ) );
}
endfor;

// Hidden field
$_hidden_field = $_POST['_hidden_field'];
for ( $i = 0; $i < sizeof( $variable_sku ); $i++ ) :
$variation_id = (int) $variable_post_id[$i];
if ( isset( $_hidden_field[$i] ) ) {
update_post_meta( $variation_id, '_hidden_field', stripslashes( $_hidden_field[$i] ) );
}
endfor;
endif;
}
?>