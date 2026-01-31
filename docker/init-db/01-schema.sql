-- CO2 Sensor Monitoring Service Database Schema

-- Create sensors table
CREATE TABLE IF NOT EXISTS sensors (
    id UUID PRIMARY KEY,
    status VARCHAR(10) NOT NULL CHECK (status IN ('OK', 'WARN', 'ALERT')),
    consecutive_high_readings INT NOT NULL DEFAULT 0,
    consecutive_low_readings INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create measurements table
CREATE TABLE IF NOT EXISTS measurements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_uuid UUID NOT NULL,
    co2_level INT NOT NULL CHECK (co2_level > 0),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_sensor
        FOREIGN KEY (sensor_uuid)
        REFERENCES sensors(id)
        ON DELETE CASCADE
);

-- Create alerts table
CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_uuid UUID NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_alert_sensor
        FOREIGN KEY (sensor_uuid)
        REFERENCES sensors(id)
        ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_measurements_sensor_timestamp 
    ON measurements(sensor_uuid, timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_alerts_sensor_uuid 
    ON alerts(sensor_uuid);

CREATE INDEX IF NOT EXISTS idx_sensors_status 
    ON sensors(status);

-- Add comments for documentation
COMMENT ON TABLE sensors IS 'Stores sensor information and current status';
COMMENT ON TABLE measurements IS 'Stores CO2 measurements from sensors';
COMMENT ON TABLE alerts IS 'Stores alert history for sensors';

COMMENT ON COLUMN sensors.consecutive_high_readings IS 'Count of consecutive readings > 2000 ppm';
COMMENT ON COLUMN sensors.consecutive_low_readings IS 'Count of consecutive readings < 2000 ppm (used in ALERT state)';
COMMENT ON COLUMN measurements.co2_level IS 'CO2 concentration in parts per million (ppm)';
COMMENT ON COLUMN alerts.end_time IS 'NULL if alert is active, timestamp if resolved';
