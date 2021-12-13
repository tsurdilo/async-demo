package io.banking.wf.demo;

import io.banking.wf.demo.model.TransactionEventPayload;
import io.banking.wf.demo.workflow.TemporalServerlessActivities;
import io.banking.wf.demo.workflow.TemporalServerlessWorkflow;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class AppLifecycleBean {

    private WorkflowClient client;
    private WorkerFactory factory;

    @ConfigProperty(name = "sw.task.queue")
    String taskQueue;

    @Inject
    TemporalServerlessActivities activities;


    void onStart(@Observes StartupEvent ev) {

        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        client = WorkflowClient.newInstance(service);
        factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(TemporalServerlessWorkflow.class);
        worker.registerActivitiesImplementations(activities);

        factory.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        factory.shutdown();
    }

    public WorkflowClient getClient() {
        return client;
    }
}
