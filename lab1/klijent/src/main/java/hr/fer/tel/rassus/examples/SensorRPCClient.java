package hr.fer.tel.rassus.examples;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * The type Simple unary rpc client.
 */
public class SensorRPCClient {

  private static final Logger logger = Logger.getLogger(SensorRPCClient.class.getName());


  private final ManagedChannel channel;
  private final SensorGrpc.SensorBlockingStub sensorBlockingStub;

  /**
   * Instantiates a new Simple unary rpc client.
   *
   * @param host the host
   * @param port the port
   */
  public SensorRPCClient(String host, int port) {
    this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

    sensorBlockingStub = SensorGrpc.newBlockingStub(channel);

  }

  /**
   * Stop the client.
   *
   * @throws InterruptedException the interrupted exception
   */
  public void stop() throws InterruptedException {
//    Initiates an orderly shutdown in which preexisting calls continue but new calls are
//    immediately cancelled. Waits for the channel to become terminated, giving up if the timeout
//    is reached.
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /**
   * Request readings.
   */
  public void requestReadings() {
    ReadingsResponse response = sensorBlockingStub.getReadings(
            ReadingsRequest.newBuilder().build()
    );
    logger.info("Received response: " + response);
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws InterruptedException the interrupted exception
   */
  public static void main(String[] args) throws InterruptedException {
    SensorRPCClient client = new SensorRPCClient("127.0.0.1", 3000);

    client.requestReadings();

    client.stop();
  }
}
