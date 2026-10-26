package BM_UserService.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {

    private static final String TOPIC = "user-registered";

    private final KafkaTemplate<String, UserRegisteredEvents> kafkaTemplate;

    public UserProducer(
            KafkaTemplate<String, UserRegisteredEvents> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserRegistered(UserRegisteredEvents event) {

        kafkaTemplate.send(
                TOPIC,
                String.valueOf(event.getUserId()),
                event);
    }
}