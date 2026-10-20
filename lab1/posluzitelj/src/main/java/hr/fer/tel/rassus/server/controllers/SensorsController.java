
package hr.fer.tel.rassus.server.controllers;

import hr.fer.tel.rassus.server.beans.Sensor;
import hr.fer.tel.rassus.server.services.SensorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Comparator;
import java.util.Map;

@RestController
@RequestMapping("/sensors")
public class SensorsController {

    private final SensorRepository sensorRepository;

    public SensorsController(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody Sensor sensor) {
        sensor.setLatitude(sensor.getLatitude());
        Sensor saved = sensorRepository.save(sensor);

        URI location = URI.create("/sensors/" + saved.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}/nearest")
    public ResponseEntity<?> nearest(@PathVariable Long id) {
        Sensor current = sensorRepository.findById(id).orElse(null);

        if (current == null) {
            return ResponseEntity.notFound().build();
        }

        Sensor nearest = sensorRepository.findAll().stream()
                .filter(sensor -> !sensor.getId().equals(id))
                .min(Comparator.comparingDouble(sensor ->
                        distanceKm(
                                current.getLatitude(),
                                current.getLongitude(),
                                sensor.getLatitude(),
                                sensor.getLongitude()
                        )))
                .orElse(null);

        if (nearest == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(Map.of(
                "id", nearest.getId(),
                "latitude", nearest.getLatitude(),
                "longitude", nearest.getLongitude(),
                "ip", nearest.getIp(),
                "port", nearest.getPort()
        ));
    }

    private double distanceKm(
            double lat1, double lon1, double lat2, double lon2) {

        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double deltaLatitude = Math.toRadians(lat2 - lat1);
        double deltaLongitude = Math.toRadians(lon2 - lon1);

        double a = Math.pow(Math.sin(deltaLatitude / 2), 2)
                + Math.cos(latitude1) * Math.cos(latitude2)
                * Math.pow(Math.sin(deltaLongitude / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c;
    }

    @GetMapping
    public ResponseEntity<?> allSensors() {
        return ResponseEntity.ok(sensorRepository.findAll());
    }
}

