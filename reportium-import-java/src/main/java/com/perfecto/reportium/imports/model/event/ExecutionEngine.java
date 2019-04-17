package com.perfecto.reportium.imports.model.event;

public class ExecutionEngine {
    private String name;
    private String version;
    private String host;

    public ExecutionEngine() {
    }

    public ExecutionEngine(String name, String version, String host) {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
