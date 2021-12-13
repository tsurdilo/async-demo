package io.banking.wf.demo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.banking.wf.demo.model.AccountInfoPayload;
import io.smallrye.reactive.messaging.kafka.Record;
import io.temporal.client.WorkflowStub;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.Optional;

@ApplicationScoped
public class AccountInfoConsumer {

    @ConfigProperty(name = "sw.task.queue")
    String taskQueue;

    @ConfigProperty(name = "sw.id.prefix")
    String idPrefix;

    @ConfigProperty(name = "sw.id.name")
    String idName;

    @Inject
    AppLifecycleBean observer;

    @Incoming("accountinfo-in")
    public void receive(Record<Integer, String> record) {
        ObjectMapper mapper = new ObjectMapper();
        try {

            AccountInfoPayload accountInfoPayload = mapper.readValue(record.value(), AccountInfoPayload.class);

            WorkflowStub workflowStub = observer.getClient().newUntypedWorkflowStub(
                    idPrefix + idName,
                    Optional.empty(), Optional.empty());

            workflowStub.signal("setcustomertotal", accountInfoPayload.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
