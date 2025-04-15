package dk.itveen.inbound;

import dk.itveen.helper.InMemReactiveMessagingLifecycleManager;
import dk.itveen.message.MessageEvent;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.jms.JmsProperties;
import io.smallrye.reactive.messaging.jms.JmsPropertiesBuilder;
import io.smallrye.reactive.messaging.jms.OutgoingJmsMessageMetadata;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dk.itveen.inbound.MessageIn.CHANNEL;
import static dk.itveen.inbound.MessageIn.CONTEXT_ID;

@QuarkusTestResource(value = InMemReactiveMessagingLifecycleManager.class, initArgs = {
        @ResourceArg(value = "incoming", name = CHANNEL)
})
@QuarkusTest
class MessageInTest {

    @Inject
    @Any
    InMemoryConnector connector;

    @Test
    void sendMessage() {

        // Arrange
        InMemorySource<Object> source = connector.source(CHANNEL);
        Message<MessageEvent> eventMessage = createMessage("myId");

        // Act
        source.send(eventMessage);

        // Assert

    }

    private static Message<MessageEvent> createMessage(String id) {
        MessageEvent messageEvent = new MessageEvent(id);
        JmsPropertiesBuilder jmsPropertiesBuilder = JmsProperties.builder();
        JmsProperties jmsProperties = jmsPropertiesBuilder.with(CONTEXT_ID, UUID.randomUUID().toString()).build();
        OutgoingJmsMessageMetadata metadata = OutgoingJmsMessageMetadata.builder().withProperties(jmsProperties).build();
        return Message.of(messageEvent).addMetadata(metadata);
    }

}