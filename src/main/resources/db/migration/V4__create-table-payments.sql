CREATE TABLE tb_payments (
    order_id BIGINT PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_payment_order
    FOREIGN KEY (order_id)
    REFERENCES tb_orders (id)
)