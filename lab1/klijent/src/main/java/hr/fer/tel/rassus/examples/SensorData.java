package hr.fer.tel.rassus.examples;

public class SensorData {
    private Double temperature;
    private Double pressure;
    private Double humidity;
    private Double co;
    private Double no2;
    private Double so2;

    public SensorData(Double temperature, Double pressure, Double humidity, Double co, Double no2, Double so2) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.co = co;
        this.no2 = no2;
        this.so2 = so2;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getPressure() {
        return pressure;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getCo() {
        return co;
    }

    public Double getNo2() {
        return no2;
    }

    public Double getSo2() {
        return so2;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public void setCo(Double co) {
        this.co = co;
    }

    public void setNo2(Double no2) {
        this.no2 = no2;
    }

    public void setSo2(Double so2) {
        this.so2 = so2;
    }

    private Double calibrateValue(Double own, Double neighbor) {
        boolean ownValid = own != null && own != 0.0;
        boolean neighborValid = neighbor != null && neighbor != 0.0;

        if (ownValid && neighborValid) {
            return (own + neighbor) / 2.0;
        }

        if (ownValid) {
            return own;
        }

        if (neighborValid) {
            return neighbor;
        }

        return null;
    }

    public SensorData calibrate(SensorData neighbor) {
        return new SensorData(
                calibrateValue(temperature, neighbor.temperature),
                calibrateValue(pressure, neighbor.pressure),
                calibrateValue(humidity, neighbor.humidity),
                calibrateValue(co, neighbor.co),
                calibrateValue(no2, neighbor.no2),
                calibrateValue(so2, neighbor.so2)
        );
    }
}
