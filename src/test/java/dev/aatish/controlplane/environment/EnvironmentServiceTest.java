package dev.aatish.controlplane.environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class EnvironmentServiceTest {
    private final EnvironmentRepository repository = mock(EnvironmentRepository.class);
    private final WorkspaceTemplates templates = new WorkspaceTemplates();
    private final EnvironmentService service = new EnvironmentService(repository, templates);

    @Test
    void createStartsPending() {
        when(repository.save(any(Environment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Environment environment = service.create(new CreateEnvironmentRequest("compiler", "ubuntu"));

        assertEquals("compiler", environment.getName());
        assertEquals("ubuntu:24.04", environment.getTemplate());
        assertEquals(EnvironmentStatus.PENDING, environment.getStatus());
    }

    @Test
    void createRejectsMissingFields() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(new CreateEnvironmentRequest("compiler", " ")));
    }

    @Test
    void createRejectsUnknownTemplate() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(new CreateEnvironmentRequest("compiler", "unknown")));
    }

    @Test
    void deleteMarksEnvironmentForReconciliation() {
        Environment environment = new Environment("env-1", "compiler", "ubuntu:24.04");
        when(repository.findById("env-1")).thenReturn(Optional.of(environment));

        service.delete("env-1");

        assertEquals(EnvironmentStatus.DELETING, environment.getStatus());
        verify(repository).findById("env-1");
    }
}
