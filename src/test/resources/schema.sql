-- Drop tables if they exist
DROP TABLE IF EXISTS alerts;
DROP TABLE IF EXISTS measurements;
DROP TABLE IF EXISTS sensors;

-- Create sensors table
CREATE TABLE sensors (
    id UUID PRIMARY KEY,
    status VARCHAR(10) NOT NULL,
    consecutive_high_readings INTEGER NOT NULL,
    consecutive_low_readings INTEGER NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Create measurements table
CREATE TABLE measurements (
    id UUID PRIMARY KEY,
    sensor_uuid UUID NOT NULL,
    co2_level INTEGER NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_measurements_sensor_timestamp ON measurements (sensor_uuid, timestamp);

-- Create alerts table
CREATE TABLE alerts (
    id UUID PRIMARY KEY,
    sensor_uuid UUID NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_alerts_sensor_uuid ON alerts (sensor_uuid);
