package hr.fer.tel.rassus.sensor;

public enum ConsumerTopics {
    COMMAND("Command"),
    REGISTER("Register"),
    READINGS("Readings");

    private final String topicName;

    ConsumerTopics(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
