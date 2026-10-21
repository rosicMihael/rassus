package hr.fer.tel.rassus.server.controllers;

import hr.fer.tel.rassus.server.beans.Reading;
import hr.fer.tel.rassus.server.beans.Sensor;
import hr.fer.tel.rassus.server.services.ReadingRepository;
import hr.fer.tel.rassus.server.services.SensorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/sensors/{sensorId}/readings")
public class ReadingController {

    private final ReadingRepository readingRepository;
    private final SensorRepository sensorRepository;

    public ReadingController(
            ReadingRepository readingRepository,
            SensorRepository sensorRepository) {
        this.readingRepository = readingRepository;
        this.sensorRepository = sensorRepository;
    }

    @PostMapping
    public ResponseEntity<Void> saveReading(
            @PathVariable Long sensorId,
            @RequestBody Reading reading) {

        Sensor sensor = sensorRepository.findById(sensorId).orElse(null);

        if (sensor == null) {
            return ResponseEntity.noContent().build();
        }

        reading.setSensor(sensor);
        Reading saved = readingRepository.save(reading);

        URI location = URI.create("/sensors/" + sensorId + "/readings/" + saved.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<?> getReadings(@PathVariable Long sensorId) {
        if (!sensorRepository.existsById(sensorId)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(readingRepository.findBySensorId(sensorId));
    }
}