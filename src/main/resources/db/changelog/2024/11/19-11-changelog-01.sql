ALTER TABLE IF EXISTS event ADD COLUMN IF NOT EXISTS transaction_status varchar(50);

COMMENT ON COLUMN event.transaction_status IS 'Статус транзакции'