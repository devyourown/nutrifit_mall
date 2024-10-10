DELETE FROM shipping_status;
DELETE FROM order_item;
DELETE FROM options;
DELETE FROM cart_item;
DELETE FROM review;
DELETE FROM product_qna;
DELETE FROM product_detail;
DELETE FROM product;
DELETE FROM user_coupon;
DELETE FROM coupon;
DELETE FROM shipping;
DELETE FROM orders;
DELETE FROM payment;
DELETE FROM point_transaction;
DELETE FROM notification;
DELETE FROM point;
DELETE FROM users;

-- User data
INSERT INTO users (id, email, username, password, role, o_auth, created_at) VALUES
(10000, 'user1@example.com', 'user1', 'password1', 'ROLE_USER', false, '2024-06-01 12:00:00'),
(20000, 'user2@example.com', 'user2', 'password2', 'ROLE_USER', false, '2024-06-01 12:00:00'),
(30000, 'admin@example.com', 'admin', 'adminpassword', 'ROLE_ADMIN', false, '2024-06-01 12:00:00'),
(40000, 'lhj6947@naver.com', 'buru', 'adminpassword', 'ROLE_USER', true, '2024-06-01 12:00:00');

-- Point and PointTransaction data
INSERT INTO point (id, points, user_id) VALUES
(100, 500, 10000),
(200, 1000, 20000),
(300, 1000, 30000),
(400, 1000, 40000);

INSERT INTO point_transaction (id, user_id, transaction_type, points, description, created_at) VALUES
(1, 10000, 'REWARD', 500, 'Signup bonus', '2024-06-01 12:00:00'),
(2, 20000, 'REWARD', 1000, 'Signup bonus', '2024-06-01 12:00:00'),
(3, 30000, 'REWARD', 1000, 'Signup bonus', '2024-06-01 12:00:00'),
(4, 40000, 'REWARD', 1000, 'Signup bonus', '2024-06-01 12:00:00');

-- Product data
INSERT INTO product (id, name, description, original_price, discounted_price, stock_quantity,
low_stock_threshold, image_urls, category, badge_texts, review_rating, review_count, is_released)
VALUES
(1000, '허브 마리네이드 닭가슴살', '천연 허브로 마리네이드한 촉촉한 닭가슴살. 건강과 맛을 동시에 잡았습니다.', 200, 100, 900, 10, ARRAY['/herb_chicken1.jpg', '/herb_chicken2.jpg', '/herb_chicken3.jpg'], '건강식', ARRAY['신상품', '첫판매할인'], 5985, 1247, true),
(2000, '훈제 닭가슴살', '훈제 향이 은은하게 배인 촉촉하고 부드러운 닭가슴살. 간편한 식사로 제격입니다.', 1200, 1000, 50, 5, ARRAY['/smoked_chicken1.webp', '/smoked_chicken2.jpg', '/smoked_chicken3.webp'], '건강식', ARRAY['무료배송'], 67513, 15003, true),
(3000, '칠리 닭가슴살', '매콤달콤한 칠리 소스로 양념된 닭가슴살로 색다른 맛을 즐겨보세요.', 1200, 1000, 50, 5, ARRAY['/chili_chicken1.jpg', '/chili_chicken2.webp', '/chili_chicken3.jpg'], '건강식', ARRAY['무료배송'], 15, 3, true);


INSERT INTO cart_item (id, user_id, product_id, quantity, image_url) VALUES
(1, 10000, 1000, 2, '/herb_chicken1.jpg'),
(2, 10000, 2000, 1, '/herb_chicken1.jpg'),
(3, 20000, 3000, 5, '/herb_chicken1.jpg');

-- ProductDetail 더미 데이터 1
INSERT INTO product_detail (id, product_id, detail_image_urls, shipping_details, exchange_and_returns)
VALUES (1200, 1000, ARRAY['/breast.jpg', '/breast2.jpg', '/breast3.jpg'],
ARRAY['Delivery 브랜드 업체발송은 상품설명에 별도로 기입된 브랜드 알림 배송공지 기준으로 출고되고 브랜드마다 개별 배송비가 부여됩니다.',
'SPECIAL ORDER, PT 등 예약주문은 상세설명의 출고일정을 확인하시기 바랍니다.'],
ARRAY['상품 수령일로부터 7일 이내 반품 / 환불 가능합니다.',
'변심 반품의 경우 왕복배송비를 차감한 금액이 환불되며, 제품 및 포장 상태가 재판매 가능하여야 합니다.']);

-- ProductDetail 더미 데이터 2
INSERT INTO product_detail (id, product_id, detail_image_urls, shipping_details, exchange_and_returns)
VALUES (2200, 2000, ARRAY['/breast.jpg', '/breast2.jpg', '/breast3.jpg'],
ARRAY['Delivery 브랜드 업체발송은 상품설명에 별도로 기입된 브랜드 알림 배송공지 기준으로 출고되고 브랜드마다 개별 배송비가 부여됩니다.',
'SPECIAL ORDER, PT 등 예약주문은 상세설명의 출고일정을 확인하시기 바랍니다.'],
ARRAY['상품 수령일로부터 7일 이내 반품 / 환불 가능합니다.',
'변심 반품의 경우 왕복배송비를 차감한 금액이 환불되며, 제품 및 포장 상태가 재판매 가능하여야 합니다.']);

-- ProductDetail 더미 데이터 3
INSERT INTO product_detail (id, product_id, detail_image_urls, shipping_details, exchange_and_returns)
VALUES (3200, 3000, ARRAY['/breast.jpg', '/breast2.jpg', '/breast3.jpg'],
ARRAY['Delivery 브랜드 업체발송은 상품설명에 별도로 기입된 브랜드 알림 배송공지 기준으로 출고되고 브랜드마다 개별 배송비가 부여됩니다.',
'SPECIAL ORDER, PT 등 예약주문은 상세설명의 출고일정을 확인하시기 바랍니다.'],
ARRAY['상품 수령일로부터 7일 이내 반품 / 환불 가능합니다.',
'변심 반품의 경우 왕복배송비를 차감한 금액이 환불되며, 제품 및 포장 상태가 재판매 가능하여야 합니다.']);

-- 샘플 ProductQnA 데이터
INSERT INTO product_qna (id, user_id, product_detail_id, question, answer, question_date, answer_date) VALUES
(1, 10000, 1200, 'Is this product available in blue color?', 'Yes, it is available in blue.', '2023-09-01 12:34:56', '2023-09-02 10:00:00'),
(2, 10000, 1200, 'What is the warranty period for this product?', 'The warranty period is 2 years.', '2023-09-05 15:20:10', '2023-09-06 09:30:00'),
(3, 20000, 2200, 'Does the product include batteries?', 'No, batteries are not included.', '2023-09-07 09:00:00', '2023-09-07 14:00:00'),
(4, 20000, 2200, 'Is there a discount for bulk purchases?', NULL, '2023-09-08 11:30:00', NULL),
(5, 30000, 3200, 'What is the delivery time to California?', 'Delivery takes approximately 5-7 business days.', '2023-09-09 13:15:30', '2023-09-10 08:45:00'),
(6, 40000, 1200, '이 제품의 크기가 어느 정도인가요?', '이 제품의 크기는 30cm x 20cm입니다.', '2024-09-01 11:22:33', '2024-09-02 14:30:00'),
(7, 40000, 2200, '세탁이 가능한가요?', '네, 세탁이 가능합니다.', '2024-09-05 09:12:45', '2024-09-06 10:10:00'),
(8, 40000, 3200, '배송은 얼마나 걸리나요?', '평균적으로 3일 이내에 배송됩니다.', '2024-09-08 08:40:00', '2024-09-08 16:50:00');


INSERT INTO options (id, quantity, price, description, product_id)
VALUES
(10, 1, 100, '1팩 구매 시 정가', 1000),
(1000, 10, 1000, '10팩 구매 시 천원', 1000),
(2000, 15, 1350, '15팩 구매 시 10% 할인 (개당 90원)', 1000),
(3000, 20, 1700, '20팩 구매 시 15% 할인 (개당 85원)', 1000);

INSERT INTO options (id, quantity, price, description, product_id)
VALUES
(4000, 10, 10000, '10팩 구매 시 만원', 2000),
(5000, 15, 13500, '15팩 구매 시 10% 할인 (개당 1350원)', 2000),
(6000, 20, 18000, '20팩 구매 시 15% 할인 (개당 900원)', 2000);

INSERT INTO options (id, quantity, price, description, product_id)
VALUES
(7000, 10, 10000, '10팩 구매 시 만원', 3000),
(8000, 15, 13500, '15팩 구매 시 10% 할인 (개당 1350원)', 3000),
(9000, 20, 18000, '20팩 구매 시 15% 할인 (개당 900원)' , 3000);

-- Coupon data
INSERT INTO coupon (id, code, description, discount_type, discount_value, valid_from, valid_until, is_active, minimum_order_amount, max_discount_amount, remaining_quantity) VALUES
(1000, 'DISCOUNT10', '5000원 이상 구매시, 10퍼센트 할인', 'PERCENTAGE', 10, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true, 5000, 1000, 100),
(2000, 'SAVE20', '10000원이상 구매시 2000원 할인', 'AMOUNT', 2000, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true, 10000, 2000, 50);

-- UserCoupon data
INSERT INTO user_coupon (id, user_id, coupon_id, is_used, assigned_at, used_at) VALUES
(1000, 10000, 1000, false, '2024-06-01 12:00:00', NULL),
(2000, 20000, 2000, false, '2024-06-01 12:00:00', NULL);

-- Payment 더미 데이터
INSERT INTO payment (id, order_payment_id, total, subtotal, discount, shipping_fee, payment_method, payment_status, payment_date, user_id, coupon_code, used_points, earn_points)
VALUES
(10, 'PAY-20240001', 100000, 95000, 5000, 3000, 'CREDIT_CARD', 'COMPLETED', '2024-09-01 12:30:00', 10000, 'DISCOUNT10', 500, 1000),
(20, 'PAY-20240002', 200000, 190000, 10000, 4000, 'PAYPAL', 'PENDING', '2024-09-02 14:45:00', 20000, 'DISCOUNT20', 1000, 2000),
(30, 'PAY-20240003', 50000, 48000, 2000, 2000, 'KAKAO_PAY', 'COMPLETED', '2024-09-03 09:20:00', 40000, NULL, 0, 500),
(40, 'PAY-20240004', 150000, 140000, 10000, 3000, 'BANK_TRANSFER', 'FAILED', '2024-09-04 16:00:00', 40000, 'DISCOUNT15', 700, 1500),
(50, 'PAY-20240005', 300000, 285000, 15000, 5000, 'CREDIT_CARD', 'COMPLETED', '2024-09-05 11:15:00', 40000, 'DISCOUNT25', 1200, 3000);


-- Order, OrderItem, and Shipping data
INSERT INTO orders (id, user_id, total_amount, order_date, order_payment_id, payment_id) VALUES
(10000000, 10000, 5000, '2024-06-01 12:30:00', '12213421', 10),
(20000000, 20000, 10000, '2024-06-01 13:00:00', '12312423', 20),
(30000000, 40000, 10000, '2024-06-01 13:00:00', '11345678', 30);

INSERT INTO order_item (id, order_id, product_id, price, quantity, total_amount, image_url) VALUES
(1000, 10000000, 1000, 1000, 2, 2000, '/herb_chicken1.jpg'),
(2000, 10000000, 2000, 1500, 2, 3000, '/herb_chicken1.jpg'),
(3000, 20000000, 3000, 3000, 3, 9000, '/herb_chicken1.jpg'),
(4000, 30000000, 3000, 3000, 3, 9000, '/herb_chicken1.jpg'),
(5000, 30000000, 3000, 3000, 3, 9000, '/herb_chicken1.jpg');

INSERT INTO shipping (id, order_id, recipient_name, address, address_detail, recipient_phone, orderer_name, orderer_phone) VALUES
(10000,10000000, 'John Doe', '123 Main St', 'key', '010-1234-5678', 'lee', '010-1234-5678'),
(20000, 20000000, 'Jane Smith', '456 Park Ave', 'key', '010-8765-4321', 'joe', '010-1234-5678');

INSERT INTO shipping_status (id, order_item_id, status, status_time) VALUES
(12122, 1000, '출고완료','2024-06-01 12:00:00'),
(22121, 1000, '배송완료','2024-06-03 12:00:00'),
(31212, 2000, '출고완료','2024-06-01 12:00:00'),
(41212, 2000, '배송완료','2024-06-03 12:00:00'),
(3121232, 4000, '출고완료','2024-06-01 12:00:00'),
(4121232, 5000, '배송완료','2024-06-03 12:00:00');

-- Review data
INSERT INTO review (id, user_id, product_id, rating, comment, created_at, image_urls) VALUES
(1465456, 10000, 1000, 5, 'Great product!', '2024-06-01 12:50:00', ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif']),
(1365465, 10000, 1000, 5, 'Great product!', '2024-06-01 12:50:00', ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif']),
(146548465, 10000, 1000, 5, 'Great product!', '2024-06-01 12:50:00', ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif']),
(123412341, 10000, 1000, 5, 'Great product!', '2024-06-01 12:50:00', ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif']),
(5468465484, 10000, 1000, 5, 'Great product!', '2024-06-01 12:50:00', ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif']),
(45685465, 10000, 1000, 5, 'Great product!', '2024-06-01 12:50:00', ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif']),
(12346845, 10000, 1000, 5, 'Great product!', '2024-06-01 12:50:00', ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif']),
(49636545465, 10000, 1000, 5, 'Great product!', '2024-06-01 12:50:00', ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif']),
(1324454845, 10000, 1000, 5, 'Great product!', '2024-06-01 12:50:00', ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif']),
(246848476, 20000, 2000, 4, 'Very good, but could be cheaper.', '2024-06-01 13:20:00', ARRAY['/sample1.jfif', '/sample2.jfif', '/sample3.jfif']),
(154, 40000, 1000, 4.5, '이 제품은 정말 마음에 듭니다!', '2024-09-01 12:34:56', ARRAY['herb_chicken1.jpg', 'herb_chicken2.jpg']),
(28487, 40000, 2000, 3.0, '생각보다는 괜찮지만, 개선의 여지가 있습니다.', '2024-09-05 15:20:00', ARRAY['herb_chicken3.jpg']),
(38987, 40000, 3000, 5.0, '완벽한 제품입니다! 추천합니다.', '2024-09-10 09:45:30', ARRAY['herb_chicken1.jpg', 'herb_chicken2.jpg', 'herb_chicken3.jpg']);


-- Notification data
INSERT INTO notification (id, user_id, message, is_read, created_at) VALUES
(1, 10000, 'Your order has been shipped.', false, '2024-06-01 13:00:00'),
(2, 20000, 'Your order has been delivered.', false, '2024-06-01 14:00:00');
