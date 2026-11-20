package hr.fer.tel.rassus.sensor;


import java.util.List;
import java.util.Map;

public class SensorReading {
    private final String sensorId;
    private final double reading;
    private final long scalarTimestamp;
    private final Map<String, Integer> vectorTimestamp;

    public SensorReading(
            String sensorId,
            double reading,
            long scalarTimestamp,
            Map<String, Integer> vectorTimestamp) {
        this.sensorId = sensorId;
        this.reading = reading;
        this.scalarTimestamp = scalarTimestamp;
        this.vectorTimestamp = vectorTimestamp;
    }

    public String getSensorId() {
        return sensorId;
    }

    public double getReading() {
        return reading;
    }

    public long getScalarTimestamp() {
        return scalarTimestamp;
    }

    public Map<String, Integer> getVectorTimestamp() {
        return vectorTimestamp;
    }
}
