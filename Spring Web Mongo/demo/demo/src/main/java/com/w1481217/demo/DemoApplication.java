package com.w1481217.demo;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"Controller", "DAO"})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private Connector createStandardConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(8080);
        return connector;
    }

    @Bean
    public EmbeddedServletContainerFactory getEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory containerFactory = new TomcatEmbeddedServletContainerFactory();
        containerFactory.addAdditionalTomcatConnectors(createStandardConnector());
        return containerFactory;
    }
}
