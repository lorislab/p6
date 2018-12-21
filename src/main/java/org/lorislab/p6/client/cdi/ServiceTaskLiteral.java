package org.lorislab.p6.client.cdi;

import javax.enterprise.util.AnnotationLiteral;

public class ServiceTaskLiteral extends AnnotationLiteral<ServiceTask> implements ServiceTask {

    private String name;

    public ServiceTaskLiteral(String name) {
        this.name = name;
    }

    public ServiceTaskLiteral() {
        this("");
    }

    public String value() {
        return name;
    }
}