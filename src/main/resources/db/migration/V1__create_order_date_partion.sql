-- Step 1: 기존 테이블 이름 변경
ALTER TABLE order_item RENAME TO order_item_backup;

-- Step 2: 파티션 마스터 테이블 생성
CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
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
    cautions VARCHAR(255)
) PARTITION BY RANGE (order_date);

-- Step 3: 파티션 생성 (예시: 2024년 1월 ~ 3월)
CREATE TABLE order_item_2024_01 PARTITION OF order_item
FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE order_item_2024_02 PARTITION OF order_item
FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

CREATE TABLE order_item_2024_03 PARTITION OF order_item
FOR VALUES FROM ('2024-03-01') TO ('2024-04-01');

-- Step 4: 기존 데이터 파티션 테이블로 이동
INSERT INTO order_item
SELECT * FROM order_item_backup;

-- Step 5: 기존 백업 테이블 삭제
DROP TABLE order_item_backup;