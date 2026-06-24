CREATE TABLE tb_order_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,

    product_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,

    CONSTRAINT fk_order_item_product
    FOREIGN KEY (product_id)
    REFERENCES tb_products(id),

    CONSTRAINT fk_order_item_order
    FOREIGN KEY (order_id)
    REFERENCES tb_orders(id)
)