package hr.fer.tel.rassus.examples;

import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;

public class SensorService extends SensorGrpc.SensorImplBase {
  private static final Logger logger = Logger.getLogger(SensorService.class.getName());
  private final ReadingsProvider readingsProvider;

  public SensorService(ReadingsProvider readingsProvider) {
    this.readingsProvider = readingsProvider;
  }

  @Override
  public void getReadings(
          ReadingsRequest request,
          StreamObserver<ReadingsResponse> responseObserver) {

    logger.info("Dobio sam novi zahtjev za ocitanja od susjeda");

    try {
      SensorData readings = readingsProvider.getCurrentReadings();

      ReadingsResponse.Builder builder = ReadingsResponse.newBuilder();

      if (readings.getTemperature() != null) {
        builder.setTemperature(readings.getTemperature());
      }

      if (readings.getPressure() != null) {
        builder.setPressure(readings.getPressure());
      }

      if (readings.getHumidity() != null) {
        builder.setHumidity(readings.getHumidity());
      }

      if (readings.getCo() != null) {
        builder.setCo(readings.getCo());
      }

      if (readings.getNo2() != null) {
        builder.setNo2(readings.getNo2());
      }

      if (readings.getSo2() != null) {
        builder.setSo2(readings.getSo2());
      }

      ReadingsResponse response = builder.build();

      logger.info("Saljem ocitanja susjedu.");
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      logger.severe("Greska pri obradi gRPC zahtjeva: " + e.getMessage());
      responseObserver.onError(
              io.grpc.Status.INTERNAL
                      .withDescription("Greska pri dohvaćanju očitanja")
                      .withCause(e)
                      .asRuntimeException()
      );
    }
  }
}
