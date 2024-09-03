DELETE FROM order_item;
DELETE FROM options;
DELETE FROM cart_item;
DELETE FROM review;
DELETE FROM product;
DELETE FROM cart;
DELETE FROM user_coupon;
DELETE FROM coupon;
DELETE FROM shipping_status;
DELETE FROM shipping;
DELETE FROM orders;
DELETE FROM payment;
DELETE FROM point_transaction;
DELETE FROM point;
DELETE FROM notification;
DELETE FROM users;

-- User data
INSERT INTO users (id, email, username, password, role, address, address_details, shipping_details) VALUES
(10000, 'user1@example.com', 'user1', 'password1', 'ROLE_USER', '123 Main St', 'Apt 4B', 'Fast Shipping'),
(20000, 'user2@example.com', 'user2', 'password2', 'ROLE_USER', '456 Park Ave', 'Apt 12C', 'Standard Shipping'),
(30000, 'admin@example.com', 'admin', 'adminpassword', 'ROLE_ADMIN', '789 Elm St', '', '');

-- Product data
INSERT INTO product (id, name, description, original_price, discounted_price, stock_quantity,
low_stock_threshold, image_urls, category, badge_texts, review_rating, review_count)
VALUES
(1, 'Product A', 'Description A', 1000, 100, 900, 10, ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif'], 'Category A', ARRAY['신상품', '첫판매할인'], 5985, 1247),
(2, 'Product B', 'Description B', 2000, 1800, 50, 5, ARRAY['/sample2.jfif', '/sample2.jfif'], 'Category B', ARRAY['무료배송'], 67513, 15003),
(3, 'Product C', 'Description C', 2000, 1800, 50, 5, ARRAY['/sample3.jfif', '/sample2.jfif'], 'Category B', ARRAY['무료배송'], 15, 3);

INSERT INTO options (id, quantity, price, description, product_id)
VALUES (1, 10, 1000, 'Option 1 Description', 1),
       (2, 20, 2000, 'Option 2 Description', 1),
       (3, 15, 1500, 'Option 3 Description', 1);

INSERT INTO options (id, quantity, price, description, product_id)
VALUES (4, 10, 1000, 'Option 1 Description', 2),
       (5, 20, 2000, 'Option 2 Description', 2),
       (6, 15, 1500, 'Option 3 Description', 2);

INSERT INTO options (id, quantity, price, description, product_id)
VALUES (7, 10, 1000, 'Option 1 Description', 3),
       (8, 20, 2000, 'Option 2 Description', 3),
       (9, 15, 1500, 'Option 3 Description', 3);

-- Cart and CartItem data
INSERT INTO cart (id, user_id) VALUES
(1000, 10000),
(2000, 20000);

INSERT INTO cart_item (id, cart_id, product_id, quantity) VALUES
(1, 1000, 1, 2),
(2, 1000, 2, 1),
(3, 2000, 3, 5);

-- Coupon data
INSERT INTO coupon (id, code, description, discount_type, discount_value, valid_from, valid_until, is_active, minimum_order_amount, max_discount_amount, remaining_quantity) VALUES
(1, 'DISCOUNT10', '10% off on orders above $50', 'PERCENTAGE', 10, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true, 5000, 1000, 100),
(2, 'SAVE20', '$20 off on orders above $100', 'AMOUNT', 20, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true, 10000, 2000, 50);

-- UserCoupon data
INSERT INTO user_coupon (id, user_id, coupon_id, is_used, assigned_at, used_at) VALUES
(1, 10000, 1, false, '2024-06-01 12:00:00', NULL),
(2, 20000, 2, false, '2024-06-01 12:00:00', NULL);

INSERT INTO payment (id, order_payment_id, total, subtotal, discount, shipping_fee, payment_method, payment_status, payment_date, user_id, used_points, coupon_id) VALUES
(1000, 'payment-dasdas',10000, 11000, 1000, 3000, 'CREDITCARD', 'COMPLETE', '2024-06-01 12:00:00', 10000, 10000, 1),
(2000, 'payment-dasdas',10000, 11000, 1000, 3000, 'CREDITCARD', 'COMPLETE', '2024-06-01 12:00:00', 20000, 10000, 2);

-- Order, OrderItem, and Shipping data
INSERT INTO orders (id, user_id, total_amount, order_date, order_payment_id, payment_id) VALUES
(1, 10000, 5000, '2024-06-01 12:30:00', '12213421', 1000),
(2, 20000, 10000, '2024-06-01 13:00:00', '12312423', 2000);

INSERT INTO order_item (id, order_id, product_id, price, quantity, total_amount) VALUES
(1000, 1, 1, 1000, 2, 2000),
(2000, 1, 2, 1500, 2, 3000),
(3000, 2, 3, 3000, 3, 9000);

INSERT INTO shipping (id, order_id, recipient_name, address, address_detail, recipient_phone, orderer_name, orderer_phone) VALUES
(10000, 1, 'John Doe', '123 Main St', 'key', '010-1234-5678', 'lee', '010-1234-5678'),
(20000, 2, 'Jane Smith', '456 Park Ave', 'key', '010-8765-4321', 'joe', '010-1234-5678');

INSERT INTO shipping_status (id, shipping_id, status, status_time) VALUES
(1, 10000, '출고완료','2024-06-01 12:00:00'),
(2, 10000, '배송완료','2024-06-03 12:00:00'),
(3, 20000, '출고완료','2024-06-01 12:00:00'),
(4, 20000, '배송완료','2024-06-03 12:00:00');

-- Point and PointTransaction data
INSERT INTO point (id, user_id, points) VALUES
(100, 10000, 500),
(200, 20000, 1000);

INSERT INTO point_transaction (id, point_id, transaction_type, points, description, created_at) VALUES
(1, 100, 'REWARD', 500, 'Signup bonus', '2024-06-01 12:00:00'),
(2, 200, 'REWARD', 1000, 'Signup bonus', '2024-06-01 12:00:00');

-- Review data
INSERT INTO review (id, user_id, product_id, rating, comment, created_at) VALUES
(1, 10000, 1, 5, 'Great product!', '2024-06-01 12:50:00'),
(2, 20000, 2, 4, 'Very good, but could be cheaper.', '2024-06-01 13:20:00');

-- Notification data
INSERT INTO notification (id, user_id, message, is_read, created_at) VALUES
(1, 10000, 'Your order has been shipped.', false, '2024-06-01 13:00:00'),
(2, 20000, 'Your order has been delivered.', false, '2024-06-01 14:00:00');
