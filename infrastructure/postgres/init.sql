CREATE SCHEMA IF NOT EXISTS tenant_service;
CREATE SCHEMA IF NOT EXISTS order_service;
CREATE SCHEMA IF NOT EXISTS customer_service;
CREATE SCHEMA IF NOT EXISTS notification_service;
CREATE SCHEMA IF NOT EXISTS shared;

CREATE OR REPLACE FUNCTION notify_tenant_change()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM pg_notify('tenant_channel', row_to_json(NEW)::text);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

COMMENT ON SCHEMA tenant_service IS 'Tenant management schema';
COMMENT ON SCHEMA order_service IS 'Order management schema';
COMMENT ON SCHEMA customer_service IS 'Customer management schema';
COMMENT ON SCHEMA notification_service IS 'SMS notification schema';
COMMENT ON SCHEMA shared IS 'Cross-service shared lookups';
