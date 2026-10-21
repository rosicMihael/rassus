package hr.fer.tel.rassus.examples;

public record SensorInfo(
        Long id,
        double latitude,
        double longitude,
        String ip,
        int port
) {}