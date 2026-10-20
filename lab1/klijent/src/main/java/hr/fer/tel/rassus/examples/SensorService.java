package hr.fer.tel.rassus.examples;

import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;

/**
 * The type Uppercase service.
 */
public class SensorService extends SensorGrpc.SensorImplBase {
  private static final Logger logger = Logger.getLogger(SensorService.class.getName());
  private final ReadingsProvider readingsProvider;

  public SensorService(ReadingsProvider readingsProvider) {
    this.readingsProvider = readingsProvider;
  }

  @Override
  public void getReadings(
      ReadingsRequest request, StreamObserver<ReadingsResponse> responseObserver
  ) {
    logger.info("Got a new readings request");

    // Create response
    SensorData readings = readingsProvider.getCurrentReadings();

    logger.info("SO2: " + readings.getSo2());

    ReadingsResponse response = ReadingsResponse.newBuilder()
            .setTemperature(readings.getTemperature())
            .setPressure(readings.getPressure())
            .setHumidity(readings.getHumidity())
            .setCo(readings.getCo())
            .setNo2(readings.getNo2())
            .setSo2(readings.getSo2())
            .build();

    // Send response
    responseObserver.onNext(
        response
    );

    logger.info("Responding with: " + response.getTemperature());
    // Send a notification of successful stream completion.
    responseObserver.onCompleted();
  }
}
