package dev.aatish.controlplane.reconcile;

import dev.aatish.controlplane.environment.Environment;
import dev.aatish.controlplane.environment.EnvironmentRepository;
import dev.aatish.controlplane.environment.EnvironmentStatus;
import dev.aatish.controlplane.provisioning.EnvironmentProvisioner;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EnvironmentReconciler {
    private final EnvironmentRepository repository;
    private final EnvironmentProvisioner provisioner;

    public EnvironmentReconciler(EnvironmentRepository repository, EnvironmentProvisioner provisioner) {
        this.repository = repository;
        this.provisioner = provisioner;
    }

    @Scheduled(fixedDelayString = "${control-plane.reconcile-ms:5000}")
    @Transactional
    public void reconcile() {
        reconcilePending(repository.findByStatus(EnvironmentStatus.PENDING));
        reconcileDeleting(repository.findByStatus(EnvironmentStatus.DELETING));
    }

    private void reconcilePending(List<Environment> environments) {
        for (Environment environment : environments) {
            try {
                provisioner.ensurePresent(environment);
                environment.setStatus(EnvironmentStatus.READY);
            } catch (RuntimeException exception) {
                environment.setStatus(EnvironmentStatus.ERROR);
            }
        }
    }

    private void reconcileDeleting(List<Environment> environments) {
        for (Environment environment : environments) {
            try {
                provisioner.ensureAbsent(environment);
                repository.delete(environment);
            } catch (RuntimeException exception) {
                environment.setStatus(EnvironmentStatus.ERROR);
            }
        }
    }
}
