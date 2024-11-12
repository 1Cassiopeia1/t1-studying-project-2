CREATE SEQUENCE IF NOT EXISTS accept_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS accept
(
    id BIGINT PRIMARY KEY,
    client_id BIGINT NOT NULL;
    account_id BIGINT NOT NULL;
    transaction_id BIGINT NOT NULL;
    timestamp TIMESTAMP NOT NULL;
    transaction_amount TEXT;
    account_balance TEXT;
);

