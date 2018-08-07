package io.codelens.tools.thorntail.builders;

import io.codelens.tools.thorntail.ModelBuilder;
import io.codelens.tools.thorntail.Node;
import io.codelens.tools.thorntail.SchemaModel;

/**
 * network config based on org.wildfly.swarm.container.runtime.NetworkConfigurer
 */
public class NetworkModelBuilder implements ModelBuilder {
    
    private static final String SOCKET_BINDING = ".socket-bindings.";
    private static final String OUTPUT_SOCKET_BINDING = ".outbound-socket-bindings.";

    @Override
    public void build(SchemaModel model) {
        String networkInterfacesPrefix = "swarm.network.interfaces.";
        String networkSockerBindingGroupsPrefix = "swarm.network.socket-binding-groups.";

        model.addPath(networkInterfacesPrefix + Node.KEY + ".bind", "Bind address");
        model.addPath(networkSockerBindingGroupsPrefix + Node.KEY + ".port-offset", "Port offset", Integer.class);
        model.addPath(networkSockerBindingGroupsPrefix + Node.KEY + ".default-interface", "The default interface");
        model.addPath(networkSockerBindingGroupsPrefix + Node.KEY + ".http.port", "HTTP port", Integer.class);
        model.addPath(networkSockerBindingGroupsPrefix + Node.KEY + OUTPUT_SOCKET_BINDING + Node.KEY + ".remote-host", "Remote host");
        model.addPath(networkSockerBindingGroupsPrefix + Node.KEY + OUTPUT_SOCKET_BINDING + Node.KEY + ".remote-port", "Remote port", Integer.class);
        model.addPath(networkSockerBindingGroupsPrefix + Node.KEY + SOCKET_BINDING + Node.KEY + ".interface", "");
        model.addPath(networkSockerBindingGroupsPrefix + Node.KEY + SOCKET_BINDING + Node.KEY + ".multicast-port", "", Integer.class);
        model.addPath(networkSockerBindingGroupsPrefix + Node.KEY + SOCKET_BINDING + Node.KEY + ".multicast-address", "");
        model.addPath(networkSockerBindingGroupsPrefix + Node.KEY + SOCKET_BINDING + Node.KEY + ".port", "", Integer.class);

        Node networkKey = model.createOrGetNodeByPath(networkSockerBindingGroupsPrefix + Node.KEY);
        networkKey.addKeySuggestion("standard-sockets");
    }
    
}
