ALTER TABLE tb_orders
ADD COLUMN user_id BIGINT;

ALTER TABLE tb_orders
ADD CONSTRAINT fk_order_client
FOREIGN KEY (user_id)
REFERENCES tb_users (id);