package hr.fer.tel.rassus.coordinator;

import hr.fer.tel.rassus.kafka.KafkaProducerService;
import org.json.JSONObject;

import java.util.Scanner;
import java.util.logging.Logger;

public class Coordinator {
    private static final Logger logger = Logger.getLogger(Coordinator.class.getName());

    public static void main(String[] args) {


        KafkaProducerService producer = new KafkaProducerService();

        Scanner scanner = new Scanner(System.in);

        logger.info("Coordinator started.");

        while (true) {
            logger.info("Enter command (Start/Stop):");

            String command = scanner.nextLine().trim();

            if(command.isEmpty() || !command.equals("Start") && !command.equals("Stop")) {
                if(command.equalsIgnoreCase("Quit") || command.equalsIgnoreCase("Q")) {
                    logger.info("Quitting...");
                    break;
                }
                logger.info("Invalid command. Please enter 'Start' or 'Stop'.");
                continue;
            }

            JSONObject json_command = new JSONObject();
            json_command.put("type", command);

            producer.send("Command", null, json_command.toString());

        }

        producer.close();
        scanner.close();
    }
}
