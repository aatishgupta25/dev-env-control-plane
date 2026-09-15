package dev.aatish.controlplane.environment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentRepository extends JpaRepository<Environment, String> {
    List<Environment> findByStatus(EnvironmentStatus status);
}
