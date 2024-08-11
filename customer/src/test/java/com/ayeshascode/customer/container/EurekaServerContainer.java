package com.ayeshascode.customer.container;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class EurekaServerContainer extends GenericContainer<EurekaServerContainer> {

    public EurekaServerContainer() {
        super(DockerImageName.parse("ayeshawaheed7/eureka-server:latest"));
        withExposedPorts(8761);
    }

    public String getEurekaUrl() {
        return "http://" + getHost() + ":" + getMappedPort(8761) + "/eureka/";
    }
}
