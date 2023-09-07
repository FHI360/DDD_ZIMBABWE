package org.fhi360.plugins.impilo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImpiloGatewayPluginApp {
    public static void main(String[] args) {
        SpringApplication.run(ImpiloGatewayPluginApp.class, args);
    }
}



