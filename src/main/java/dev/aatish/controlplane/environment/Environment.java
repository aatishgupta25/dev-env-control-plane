package dev.aatish.controlplane.environment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "environments")
public class Environment {
    @Id
    private String id;
    private String name;
    private String template;

    @Enumerated(EnumType.STRING)
    private EnvironmentStatus status;

    protected Environment() {}

    public Environment(String id, String name, String template) {
        this.id = id;
        this.name = name;
        this.template = template;
        this.status = EnvironmentStatus.PENDING;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getTemplate() { return template; }
    public EnvironmentStatus getStatus() { return status; }
    public void setStatus(EnvironmentStatus status) { this.status = status; }
}
