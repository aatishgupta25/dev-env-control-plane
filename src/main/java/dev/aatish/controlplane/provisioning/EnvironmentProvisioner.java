package dev.aatish.controlplane.provisioning;

import dev.aatish.controlplane.environment.Environment;

public interface EnvironmentProvisioner {
    void ensurePresent(Environment environment);
    void ensureAbsent(Environment environment);
}
