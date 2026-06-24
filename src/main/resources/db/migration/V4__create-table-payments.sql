CREATE TABLE tb_payments (
    order_id BIGINT PRIMARY KEY,
    moment TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_payment_order
    FOREIGN KEY (order_id)
    REFERENCES tb_orders (id)
)