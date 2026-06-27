ALTER TABLE tb_orders
ADD COLUMN client_id BIGINT;

ALTER TABLE tb_orders
ADD CONSTRAINT fk_order_client
FOREIGN KEY (client_id)
REFERENCES tb_users (id);