package io.codelens.tools.thorntail.builders;

import io.codelens.tools.thorntail.ModelBuilder;
import io.codelens.tools.thorntail.SchemaModel;

import java.util.List;

public class DeploymentModelBuilder implements ModelBuilder {

    @Override
    public void build(SchemaModel model) {
        model.addPath("swarm.deployment", "Deployments", List.class);
    }
    
}
