package dev.aatish.controlplane.environment;

public class EnvironmentNotFoundException extends RuntimeException {
    public EnvironmentNotFoundException(String id) {
        super("Environment not found: " + id);
    }
}
