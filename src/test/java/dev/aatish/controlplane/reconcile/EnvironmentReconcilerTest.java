package dev.aatish.controlplane.reconcile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.aatish.controlplane.environment.Environment;
import dev.aatish.controlplane.environment.EnvironmentRepository;
import dev.aatish.controlplane.environment.EnvironmentStatus;
import dev.aatish.controlplane.provisioning.EnvironmentProvisioner;
import java.util.List;
import org.junit.jupiter.api.Test;

class EnvironmentReconcilerTest {
    private final EnvironmentRepository repository = mock(EnvironmentRepository.class);
    private final EnvironmentProvisioner provisioner = mock(EnvironmentProvisioner.class);
    private final EnvironmentReconciler reconciler = new EnvironmentReconciler(repository, provisioner);

    @Test
    void pendingEnvironmentBecomesReadyAfterProvisioning() {
        Environment environment = new Environment("env-1", "compiler", "ubuntu:24.04");
        when(repository.findByStatus(EnvironmentStatus.PENDING)).thenReturn(List.of(environment));
        when(repository.findByStatus(EnvironmentStatus.DELETING)).thenReturn(List.of());

        reconciler.reconcile();

        verify(provisioner).ensurePresent(environment);
        assertEquals(EnvironmentStatus.READY, environment.getStatus());
    }

    @Test
    void deletingEnvironmentIsCleanedUpAndRemoved() {
        Environment environment = new Environment("env-1", "compiler", "ubuntu:24.04");
        environment.setStatus(EnvironmentStatus.DELETING);
        when(repository.findByStatus(EnvironmentStatus.PENDING)).thenReturn(List.of());
        when(repository.findByStatus(EnvironmentStatus.DELETING)).thenReturn(List.of(environment));

        reconciler.reconcile();

        verify(provisioner).ensureAbsent(environment);
        verify(repository).delete(environment);
    }
}
