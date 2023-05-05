package org.fhi360.plugins.impilo;

import org.fhi360.plugins.impilo.domain.ImpiloGatewayDomain;
import io.github.jbella.snl.core.api.domain.CoreDomain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackageClasses = {CoreDomain.class, ImpiloGatewayDomain.class})
public class ImpiloGatewayPluginApp {
    public static void main(String[] args) {
        SpringApplication.run(ImpiloGatewayPluginApp.class, args);
    }
}



