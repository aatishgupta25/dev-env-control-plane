package dev.aatish.controlplane.environment;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/environments")
public class EnvironmentController {
    private final EnvironmentService service;

    public EnvironmentController(EnvironmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Environment> create(@RequestBody CreateEnvironmentRequest request) {
        Environment created = service.create(request);
        return ResponseEntity.created(URI.create("/environments/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Environment> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public Environment get(@PathVariable String id) {
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.accepted().build();
    }
}
