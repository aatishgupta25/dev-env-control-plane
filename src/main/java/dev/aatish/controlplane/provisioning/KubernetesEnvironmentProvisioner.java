package dev.aatish.controlplane.provisioning;

import dev.aatish.controlplane.environment.Environment;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class KubernetesEnvironmentProvisioner implements EnvironmentProvisioner {
    private static final String APP = "workspace";
    private final KubernetesClient client;

    public KubernetesEnvironmentProvisioner(KubernetesClient client) {
        this.client = client;
    }

    @Override
    public void ensurePresent(Environment environment) {
        String namespace = namespace(environment);

        client.namespaces().resource(new NamespaceBuilder()
                .withNewMetadata().withName(namespace).endMetadata()
                .build()).serverSideApply();

        client.apps().deployments().inNamespace(namespace).resource(new DeploymentBuilder()
                .withNewMetadata().withName(APP).endMetadata()
                .withNewSpec()
                    .withReplicas(1)
                    .withNewSelector().withMatchLabels(Map.of("app", APP)).endSelector()
                    .withNewTemplate()
                        .withNewMetadata().withLabels(Map.of("app", APP)).endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName(APP)
                                .withImage(environment.getTemplate())
                                .withCommand("sleep", "infinity")
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build()).serverSideApply();
    }

    @Override
    public void ensureAbsent(Environment environment) {
        client.namespaces().withName(namespace(environment)).delete();
    }

    private String namespace(Environment environment) {
        return "dev-" + environment.getId();
    }
}
