package dk.itveen.inbound;

import dk.itveen.message.MessageEvent;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.jms.JmsProperties;
import io.smallrye.reactive.messaging.jms.OutgoingJmsMessageMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class MessageIn {

    public static final String CONTEXT_ID = "contextId";
    public static final String CHANNEL = "message-in";

    @Incoming(CHANNEL)
    @ActivateRequestContext
    @Blocking
    public CompletionStage<Void> receive(Message<MessageEvent> message) {
        Optional<OutgoingJmsMessageMetadata> optionalMetadata = message.getMetadata(OutgoingJmsMessageMetadata.class);

        final HashMap<String, String> mdcMap = new HashMap<>();

        optionalMetadata.ifPresent(metadata -> {
            JmsProperties properties = metadata.getProperties();
            if (properties.propertyExists(CONTEXT_ID)) {
                mdcMap.put(CONTEXT_ID, properties.getStringProperty(CONTEXT_ID));
            }
        });

        String contextId = mdcMap.get(CONTEXT_ID);
        MDC.put(CONTEXT_ID, contextId);

        if (MDC.get(CONTEXT_ID) == null) {
            throw new RuntimeException(String.format("contextId is null on MDC, but should have been: %s", contextId));
        }

        return CompletableFuture.completedStage(null);
    }

}
