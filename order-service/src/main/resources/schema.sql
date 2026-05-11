CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    payment_status VARCHAR(120) NOT NULL,
    notification_status VARCHAR(120) NOT NULL,
    analytics_status VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
