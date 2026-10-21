package hr.fer.tel.rassus.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ReadingsProvider {
    private final List<SensorData> readings;
    private final long startTime;

    public ReadingsProvider() throws IOException {
        readings = loadReadings();
        startTime = System.nanoTime();
    }

    private List<SensorData> loadReadings() throws IOException {
        InputStream input = getClass().getClassLoader()
                .getResourceAsStream("readings");

        if (input == null) {
            throw new IOException("Nije pronadena datoteka readings");
        }

        List<SensorData> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] values = line.split(",", -1);

                if (values.length < 6) {
                    throw new IOException("Neispravan redak CSV-a: " + line);
                }

                result.add(new SensorData(
                        parseValue(values[0]),
                        parseValue(values[1]),
                        parseValue(values[2]),
                        parseValue(values[3]),
                        parseValue(values[4]),
                        parseValue(values[5])
                ));
            }
        }

        if (result.isEmpty()) {
            throw new IOException("CSV ne sadrzi mjerenja.");
        }

        return result;
    }

    private Double parseValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Double.valueOf(value.trim());
    }

    public SensorData getCurrentReadings() {
        long activeSeconds = (System.nanoTime() - startTime)
                / 1_000_000_000L;

        int index = (int) (activeSeconds % 100);

        return readings.get(index);
    }
}
