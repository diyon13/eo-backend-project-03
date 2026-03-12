INSERT INTO plans (plan_name, token_limit, price, created_at, updated_at) VALUES ('NORMAL', 10000, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO plans (plan_name, token_limit, price, created_at, updated_at) VALUES ('PRO', 100000, 9900, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO plans (plan_name, token_limit, price, created_at, updated_at) VALUES ('MAX', 1000000, 29900, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO plan_models (plan_id, model_name) VALUES (1, 'alan-4.0');
INSERT INTO plan_models (plan_id, model_name) VALUES (2, 'alan-4.0');
INSERT INTO plan_models (plan_id, model_name) VALUES (2, 'alan-4.1');
INSERT INTO plan_models (plan_id, model_name) VALUES (3, 'alan-4.0');
INSERT INTO plan_models (plan_id, model_name) VALUES (3, 'alan-4.1');
INSERT INTO plan_models (plan_id, model_name) VALUES (3, 'alan-4-turbo');

INSERT INTO users (plan_id, userid, username, password, email, used_token, active, locked, created_at, updated_at) VALUES (1, 'testuser', '테스트유저', '$2b$10$HXPeKfi7mPp2c06omWGQXuXjD1wI9GSbaT9H//bV2mQ2AtL48cbFC', 'test@test.com', 0, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO admin (admin_id, admin_name, password, created_at) VALUES ('admin', '관리자', 'admin1234', CURRENT_TIMESTAMP);