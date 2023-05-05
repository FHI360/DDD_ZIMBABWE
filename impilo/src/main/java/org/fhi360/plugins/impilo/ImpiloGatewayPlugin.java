package org.fhi360.plugins.impilo;

import io.github.jbella.snl.core.api.bootstrap.EnhancedSharedDataSourceSpringBootstrap;
import org.laxture.sbp.SpringBootPlugin;
import org.laxture.sbp.spring.boot.SpringBootstrap;
import org.pf4j.PluginWrapper;

public class ImpiloGatewayPlugin extends SpringBootPlugin  {
    public ImpiloGatewayPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected SpringBootstrap createSpringBootstrap() {
        return new EnhancedSharedDataSourceSpringBootstrap(this, ImpiloGatewayPluginApp.class);
    }
}



