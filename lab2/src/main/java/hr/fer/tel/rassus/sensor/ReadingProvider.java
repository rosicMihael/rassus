package hr.fer.tel.rassus.sensor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.List;

public class ReadingProvider {

    private final List<CSVRecord> readings;
    private int activeSeconds = 0;

    public ReadingProvider(String file_name) throws IOException {
        try (var inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(file_name)) {

            if (inputStream == null) {
                throw new IOException("Resource not found: " + file_name);
            }

            try (var reader = new java.io.InputStreamReader(inputStream);
                 CSVParser parser = CSVFormat.DEFAULT
                         .builder()
                         .setHeader()
                         .setSkipHeaderRecord(true)
                         .setAllowMissingColumnNames(true)
                         .build()
                         .parse(reader)) {

                readings = parser.getRecords();
            }
        }
    }

    public Double get_next_reading() {
        int row = activeSeconds % 100;

        CSVRecord record = readings.get(row);
        activeSeconds++;

        String no2 = record.get("NO2");

        if (no2 == null || no2.isBlank()) {
            return null;
        }

        return Double.parseDouble(no2);
    }

    public void reset() {
        activeSeconds = 0;
    }
}