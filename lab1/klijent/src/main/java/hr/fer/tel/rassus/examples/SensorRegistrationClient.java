
package hr.fer.tel.rassus.examples;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class SensorRegistrationClient {

    private static final Logger logger = Logger.getLogger(SensorRegistrationClient.class.getName());

    private static final String SERVER_URL = "http://localhost:8090/sensors";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SensorRegistrationClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public long registerSensor(String ip, int port)
            throws IOException, InterruptedException {

        double latitude = ThreadLocalRandom.current()
                .nextDouble(45.75, 45.85);

        double longitude = ThreadLocalRandom.current()
                .nextDouble(15.87, 16.00);

        String json = objectMapper.writeValueAsString(
                new SensorRegistration(latitude, longitude, ip, port)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 201) {
            throw new IOException(
                    "Registracija senzora nije uspjela. HTTP status: "
                            + response.statusCode()
                            + ", odgovor: " + response.body()
            );
        }

        String location = response.headers()
                .firstValue("Location")
                .orElseThrow(() ->
                        new IOException("Odgovor ne sadrzi Location zaglavlje."));

        URI locationUri = URI.create(location);
        String path = locationUri.getPath();
        String lastSegment = path.substring(path.lastIndexOf('/') + 1);

        long sensorId;
        try {
            sensorId = Long.parseLong(lastSegment);
        } catch (NumberFormatException e) {
            throw new IOException(
                    "Nije moguce procitati ID senzora iz Location zaglavlja: "
                            + location,
                    e
            );
        }

        logger.info("Senzor je uspjesno registriran.");
        logger.info("ID senzora: " + sensorId);
        logger.info("Lokacija: " + latitude + ", " + longitude);
        logger.info("gRPC adresa: " + ip + ":" + port);

        return sensorId;
    }

    private record SensorRegistration(
            double latitude,
            double longitude,
            String ip,
            int port
    ) {
    }

    public SensorInfo findNearestSensor(long sensorId) throws IOException, InterruptedException {
        String url = SERVER_URL + "/" + sensorId + "/nearest";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 404) {
            throw new IOException("Senzor s ID " + sensorId + " nije pronaden.");
        } else if (response.statusCode() == 204) {
            return null; // Nema dostupnih senzora
        } else if (response.statusCode() != 200) {
            throw new IOException(
                    "Neuspjesno dohvacanje najblizeg senzora. HTTP status: "
                            + response.statusCode()
                            + ", odgovor: " + response.body()
            );
        }

        return objectMapper.readValue(response.body(), SensorInfo.class);
    }

    public void sendReading(long sensorId, SensorData reading)
            throws IOException, InterruptedException {

        String url = "http://localhost:8090/sensors/"
                + sensorId + "/readings";

        String json = objectMapper.writeValueAsString(reading);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 201) {
            throw new IOException(
                    "Slanje ocitanja nije uspjelo. HTTP status: "
                            + response.statusCode()
            );
        }
    }
}
