package dev.aatish.controlplane.environment;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnvironmentService {
    private final EnvironmentRepository repository;
    private final WorkspaceTemplates templates;

    public EnvironmentService(EnvironmentRepository repository, WorkspaceTemplates templates) {
        this.repository = repository;
        this.templates = templates;
    }

    @Transactional
    public Environment create(CreateEnvironmentRequest request) {
        if (request == null || blank(request.name()) || blank(request.template())) {
            throw new IllegalArgumentException("name and template are required");
        }

        Environment environment = new Environment(
                UUID.randomUUID().toString(),
                request.name(),
                templates.resolve(request.template()));
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

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
