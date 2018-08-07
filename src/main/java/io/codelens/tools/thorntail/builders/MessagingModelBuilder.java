package io.codelens.tools.thorntail.builders;

import io.codelens.tools.thorntail.ModelBuilder;
import io.codelens.tools.thorntail.SchemaModel;

public class MessagingModelBuilder implements ModelBuilder {

    @Override
    public void build(SchemaModel model) {
        // addPath("swarm.messaging.remote", "Flag to enable the remote connection", Boolean.class); // TODO
        model.addPath("swarm.messaging.remote.host", "Host of the remote connection");
        model.addPath("swarm.messaging.remote.jndi-name", "JNDI name of the remote connection");
        model.addPath("swarm.messaging.remote.name", "Name of the remote connection");
        model.addPath("swarm.messaging.remote.port", "Port of the remote connection", Integer.class);
    }
    
}
