INSERT INTO products (id, name, description, price)
VALUES
    (101, 'Product-101', 'Sample product for order placement', 499.99),
    (102, 'Product-102', 'Backup product for demo scenarios', 799.00)
ON CONFLICT (id) DO NOTHING;
