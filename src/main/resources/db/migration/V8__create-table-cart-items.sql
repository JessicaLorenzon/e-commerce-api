CREATE TABLE tb_cart_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    quantity INTEGER NOT NULL,

    product_id BIGINT NOT NULL,
    cart_id BIGINT NOT NULL,

    CONSTRAINT fk_cart_item_product
    FOREIGN KEY (product_id)
    REFERENCES tb_products(id),

    CONSTRAINT fk_cart_item_cart
    FOREIGN KEY (cart_id)
    REFERENCES tb_carts(id)
)