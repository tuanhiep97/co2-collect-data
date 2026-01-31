-- Sample data for testing (optional - can be removed in production)

-- Insert sample sensors
INSERT INTO sensors (id, status, consecutive_high_readings, consecutive_low_readings, last_updated)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440000'::UUID, 'OK', 0, 0, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440001'::UUID, 'WARN', 1, 0, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert sample measurements for the first sensor (last 30 days)
INSERT INTO measurements (sensor_uuid, co2_level, timestamp)
SELECT 
    '550e8400-e29b-41d4-a716-446655440000'::UUID,
    800 + (random() * 400)::int,  -- Random CO2 levels between 800-1200
    CURRENT_TIMESTAMP - (interval '1 day' * generate_series(0, 29))
ON CONFLICT DO NOTHING;

-- Insert a few high readings for the second sensor
INSERT INTO measurements (sensor_uuid, co2_level, timestamp)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440001'::UUID, 2100, CURRENT_TIMESTAMP - interval '3 minutes'),
    ('550e8400-e29b-41d4-a716-446655440001'::UUID, 1800, CURRENT_TIMESTAMP - interval '2 minutes'),
    ('550e8400-e29b-41d4-a716-446655440001'::UUID, 2050, CURRENT_TIMESTAMP - interval '1 minute')
ON CONFLICT DO NOTHING;
