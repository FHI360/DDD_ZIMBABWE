package org.fhi360.plugins.impilo;

import io.github.jbella.snl.core.api.bootstrap.EnhancedSpringBootstrap;
import io.github.jbella.snl.core.api.bootstrap.JpaSpringBootPlugin;
import org.fhi360.plugins.impilo.domain.ImpiloGatewayDomain;
import org.laxture.sbp.spring.boot.SpringBootstrap;
import org.pf4j.PluginWrapper;

import java.util.List;

public class ImpiloGatewayPlugin extends JpaSpringBootPlugin {
    public ImpiloGatewayPlugin(PluginWrapper wrapper) {
        super(wrapper, List.of(ImpiloGatewayDomain.class));
    }

    @Override
    protected SpringBootstrap createSpringBootstrap() {
        return new EnhancedSpringBootstrap(this, ImpiloGatewayPluginApp.class);
    }
}



