package dev.aatish.controlplane.environment;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnvironmentService {
    private final EnvironmentRepository repository;

    public EnvironmentService(EnvironmentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Environment create(CreateEnvironmentRequest request) {
        Environment environment = new Environment(
                UUID.randomUUID().toString(),
                request.name(),
                request.template());
        return repository.save(environment);
    }

    public List<Environment> list() {
        return repository.findAll();
    }

    public Environment get(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EnvironmentNotFoundException(id));
    }

    @Transactional
    public void delete(String id) {
        Environment environment = get(id);
        environment.setStatus(EnvironmentStatus.DELETING);
    }
}
