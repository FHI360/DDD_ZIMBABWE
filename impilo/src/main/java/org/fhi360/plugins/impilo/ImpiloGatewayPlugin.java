package org.fhi360.plugins.impilo;

import io.github.jbella.snl.core.api.bootstrap.EnhancedSpringBootstrap;
import org.laxture.sbp.SpringBootPlugin;
import org.laxture.sbp.spring.boot.SpringBootstrap;
import org.laxture.sbp.spring.boot.configurer.SbpDataSourceConfigurer;
import org.pf4j.PluginWrapper;

public class ImpiloGatewayPlugin extends SpringBootPlugin  {
    public ImpiloGatewayPlugin(PluginWrapper wrapper) {
        super(wrapper, new SbpDataSourceConfigurer());
    }

    @Override
    protected SpringBootstrap createSpringBootstrap() {
        return new EnhancedSpringBootstrap(this, ImpiloGatewayPluginApp.class);
    }
}



