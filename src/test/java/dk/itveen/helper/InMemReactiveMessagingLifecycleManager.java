package dk.itveen.helper;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import java.util.HashMap;
import java.util.Map;

public class InMemReactiveMessagingLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Map<String, String> params = new HashMap<>();

    @Override
    public void init(Map<String, String> params) {
        this.params.putAll(params);
    }

    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        for (Map.Entry<String, String> con : this.params.entrySet()) {
            switch (con.getValue()) {
                case "incoming":
                    env.putAll(InMemoryConnector.switchIncomingChannelsToInMemory(con.getKey()));
                    break;
                case "outgoing":
                    env.putAll(InMemoryConnector.switchOutgoingChannelsToInMemory(con.getKey()));
                    break;
                default:
                    throw new RuntimeException("Unknown channel type: " + con.getValue());
            }
        }
        return env;
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}

