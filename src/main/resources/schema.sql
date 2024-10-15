-- PostgreSQL 스키마 생성 스크립트

BEGIN;

DROP TABLE IF EXISTS user_coupon CASCADE;
DROP TABLE IF EXISTS shipping_status CASCADE;
DROP TABLE IF EXISTS review CASCADE;
DROP TABLE IF EXISTS product_qna CASCADE;
DROP TABLE IF EXISTS product_detail CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS point_transaction CASCADE;
DROP TABLE IF EXISTS point CASCADE;
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS options CASCADE;
DROP TABLE IF EXISTS coupon CASCADE;
DROP TABLE IF EXISTS cart_item CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Table: User
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    o_auth BOOLEAN,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    profile_image VARCHAR(255),
    recipient_name VARCHAR(255),
    recipient_phone VARCHAR(255),
    orderer_name VARCHAR(255),
    orderer_phone VARCHAR(255),
    address VARCHAR(255),
    address_detail VARCHAR(255),
    cautions VARCHAR(255)
);

-- Table: Product
CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    original_price BIGINT NOT NULL,
    discounted_price BIGINT,
    is_released BOOLEAN NOT NULL,
    stock_quantity INT NOT NULL,
    low_stock_threshold INT,
    image_urls TEXT[],
    category VARCHAR(255),
    badge_texts TEXT[],
    review_rating BIGINT,
    review_count BIGINT
);

-- Table: CartItem
CREATE TABLE cart_item (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Table: Coupon
CREATE TABLE coupon (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255),
    discount_type VARCHAR(50),
    discount_value INT,
    valid_from TIMESTAMP,
    valid_until TIMESTAMP,
    is_active BOOLEAN,
    minimum_order_amount INT,
    max_discount_amount INT,
    remaining_quantity INT
);

-- Table: Options
CREATE TABLE options (
    id BIGSERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    price BIGINT NOT NULL,
    description VARCHAR(255) NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Table: Payment
CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    order_payment_id VARCHAR(255) NOT NULL,
    total BIGINT NOT NULL,
    subtotal BIGINT NOT NULL,
    discount BIGINT NOT NULL,
    shipping_fee BIGINT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    user_id BIGINT,
    coupon_code VARCHAR(255),
    used_points INT,
    earn_points BIGINT,
    recipient_name VARCHAR(255) NOT NULL,
    recipient_phone VARCHAR(255) NOT NULL,
    orderer_name VARCHAR(255) NOT NULL,
    orderer_phone VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    address_detail VARCHAR(255) NOT NULL,
    cautions VARCHAR(255),
    phone_number VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Table: Orders
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_payment_id VARCHAR(255) NOT NULL,
    user_id BIGINT,
    user_phone VARCHAR(255),
    total_amount BIGINT NOT NULL,
    order_date TIMESTAMP NOT NULL,
    payment_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (payment_id) REFERENCES payment(id)
);

-- Table: OrderItem (with monthly partitioning)
CREATE TABLE order_item (
    id BIGSERIAL,
    order_id BIGINT NOT NULL,
    order_payment_id VARCHAR(255) NOT NULL,
    user_id BIGINT,
    username VARCHAR(255) NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price BIGINT NOT NULL,
    quantity INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    total_amount BIGINT NOT NULL,
    tracking_number VARCHAR(255),
    order_date TIMESTAMP NOT NULL,
    current_status_time TIMESTAMP NOT NULL,
    current_status VARCHAR(255) NOT NULL,
    recipient_name VARCHAR(255) NOT NULL,
    recipient_phone VARCHAR(255) NOT NULL,
    orderer_name VARCHAR(255) NOT NULL,
    orderer_phone VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    address_detail VARCHAR(255) NOT NULL,
    cautions VARCHAR(255),
    PRIMARY KEY (id, order_date),
    FOREIGN KEY (order_id) REFERENCES orders(id)
) PARTITION BY RANGE (order_date);

-- Create monthly partitions for order_item (example: from 2024-01 to 2024-12)
CREATE TABLE order_item_2024_01 PARTITION OF order_item
FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE order_item_2024_02 PARTITION OF order_item
FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

CREATE TABLE order_item_2024_03 PARTITION OF order_item
FOR VALUES FROM ('2024-03-01') TO ('2024-04-01');

CREATE TABLE order_item_2024_04 PARTITION OF order_item
FOR VALUES FROM ('2024-04-01') TO ('2024-05-01');

CREATE TABLE order_item_2024_05 PARTITION OF order_item
FOR VALUES FROM ('2024-05-01') TO ('2024-06-01');

CREATE TABLE order_item_2024_06 PARTITION OF order_item
FOR VALUES FROM ('2024-06-01') TO ('2024-07-01');

CREATE TABLE order_item_2024_07 PARTITION OF order_item
FOR VALUES FROM ('2024-07-01') TO ('2024-08-01');

CREATE TABLE order_item_2024_08 PARTITION OF order_item
FOR VALUES FROM ('2024-08-01') TO ('2024-09-01');

CREATE TABLE order_item_2024_09 PARTITION OF order_item
FOR VALUES FROM ('2024-09-01') TO ('2024-10-01');

CREATE TABLE order_item_2024_10 PARTITION OF order_item
FOR VALUES FROM ('2024-10-01') TO ('2024-11-01');

CREATE TABLE order_item_2024_11 PARTITION OF order_item
FOR VALUES FROM ('2024-11-01') TO ('2024-12-01');

CREATE TABLE order_item_2024_12 PARTITION OF order_item
FOR VALUES FROM ('2024-12-01') TO ('2025-01-01');

-- Indexes for OrderItem
CREATE INDEX idx_order_payment_id ON order_item(order_payment_id);
CREATE INDEX idx_order_id_product_name ON order_item(order_payment_id, product_name);
CREATE INDEX idx_username ON order_item(username);
CREATE INDEX idx_order_date ON order_item(order_date);
CREATE INDEX idx_current_status ON order_item(current_status);
CREATE INDEX idx_tracking_number ON order_item(tracking_number);

-- Table: Point
CREATE TABLE point (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    points BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Table: PointTransaction
CREATE TABLE point_transaction (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    points BIGINT NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Table: ProductDetail
CREATE TABLE product_detail (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    detail_image_urls TEXT[],
    shipping_details TEXT[],
    exchange_and_returns TEXT[],
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Table: ProductQnA
CREATE TABLE product_qna (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_detail_id BIGINT NOT NULL,
    question VARCHAR(255) NOT NULL,
    answer VARCHAR(255),
    question_date TIMESTAMP NOT NULL,
    answer_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_detail_id) REFERENCES product_detail(id)
);

-- Table: Review
CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    rating DOUBLE PRECISION NOT NULL,
    comment VARCHAR(255),
    image_urls TEXT[],
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Table: ShippingStatus
CREATE TABLE shipping_status (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    status_time TIMESTAMP NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE user_coupon (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    is_used BOOLEAN,
    assigned_at TIMESTAMP,
    used_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (coupon_id) REFERENCES coupon(id)
);