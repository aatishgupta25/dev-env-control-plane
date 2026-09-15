package dev.aatish.controlplane.environment;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceTemplates {
    private final Map<String, String> templates = Map.of(
            "java", "eclipse-temurin:21-jdk",
            "python", "python:3.12",
            "ubuntu", "ubuntu:24.04");

    public String resolve(String template) {
        String image = templates.get(template);
        if (image == null) {
            throw new IllegalArgumentException("unknown workspace template: " + template);
        }
        return image;
    }
}
