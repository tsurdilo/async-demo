package io.banking.wf.demo.workflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.retry.RetryDefinition;
import io.serverlessworkflow.api.states.EventState;
import io.temporal.activity.ActivityOptions;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.common.RetryOptions;
import java.time.Duration;
import java.util.List;
import java.util.Random;

public class ServerlessWorkflowUtils {

    public static final String DEFAULT_TASK_QUEUE_NAME = "ServerlessWorkflowTaskQueue";
    public static final Random generator = new Random();
    public static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Set workflow options from DSL
     */
    public static WorkflowOptions getWorkflowOptions(Workflow workflow) {
        WorkflowOptions.Builder dslWorkflowOptionsBuilder = WorkflowOptions.newBuilder();

        if (workflow.getId() != null) {
            dslWorkflowOptionsBuilder.setWorkflowId(workflow.getId());
        }

        dslWorkflowOptionsBuilder.setTaskQueue(DEFAULT_TASK_QUEUE_NAME);

        if (workflow.getTimeouts() != null
                && workflow.getTimeouts().getWorkflowExecTimeout() != null
                && workflow.getTimeouts().getWorkflowExecTimeout().getDuration() != null) {
            dslWorkflowOptionsBuilder.setWorkflowExecutionTimeout(
                    Duration.parse(workflow.getTimeouts().getWorkflowExecTimeout().getDuration()));
        }

        if (workflow.getStart().getSchedule() != null
                && workflow.getStart().getSchedule().getCron() != null) {
            dslWorkflowOptionsBuilder.setCronSchedule(
                    workflow.getStart().getSchedule().getCron().getExpression());
        }

        return dslWorkflowOptionsBuilder.build();
    }

    /**
     * Set Activity options from DSL
     */
    public static ActivityOptions getActivityOptionsFromDsl(Workflow dslWorkflow) {
        ActivityOptions.Builder dslActivityOptionsBuilder = ActivityOptions.newBuilder();
        if (dslWorkflow.getTimeouts() != null
                && dslWorkflow.getTimeouts().getActionExecTimeout() != null) {
            dslActivityOptionsBuilder.setStartToCloseTimeout(
                    Duration.parse(dslWorkflow.getTimeouts().getActionExecTimeout()));
        }

        // In SW spec each action (activity) can define a specific retry
        // For this demo we just use the globally defined one for all actions
        if (dslWorkflow.getRetries() != null
                && dslWorkflow.getRetries().getRetryDefs() != null
                && dslWorkflow.getRetries().getRetryDefs().size() > 0) {
            RetryDefinition retryDefinition = dslWorkflow.getRetries().getRetryDefs().get(0);
            RetryOptions.Builder dslRetryOptionsBuilder = RetryOptions.newBuilder();
            if (retryDefinition.getMaxAttempts() != null) {
                dslRetryOptionsBuilder.setMaximumAttempts(
                        Integer.parseInt(retryDefinition.getMaxAttempts()));
            }
            dslRetryOptionsBuilder.setBackoffCoefficient(1.0);
            if (retryDefinition.getDelay() != null) {
                dslRetryOptionsBuilder.setInitialInterval(Duration.parse(retryDefinition.getDelay()));
            }
            if (retryDefinition.getMaxDelay() != null) {
                dslRetryOptionsBuilder.setMaximumInterval(Duration.parse(retryDefinition.getMaxDelay()));
            }
        }

        return dslActivityOptionsBuilder.build();
    }

    /**
     * Start workflow execution depending on the DSL
     */
    public static WorkflowExecution startWorkflow(
            WorkflowStub workflowStub, Workflow dslWorkflow, JsonNode workflowInput) {
        State startingDslWorkflowState = getStartingWorkflowState(dslWorkflow);
        if (startingDslWorkflowState instanceof EventState) {
            // This demo can parse only the first event
            EventState eventState = (EventState) startingDslWorkflowState;
            String eventName = eventState.getOnEvents().get(0).getEventRefs().get(0);
            // send input data as signal data
            return workflowStub.signalWithStart(
                    eventName,
                    new Object[]{workflowInput.toPrettyString()},
                    new Object[]{Workflow.toJson(dslWorkflow)});
        } else {
            // directly send input data to workflow
            return workflowStub.start(Workflow.toJson(dslWorkflow), workflowInput.toPrettyString());
        }
    }

    /**
     * Returns the starting workflow state from DSL
     */
    public static State getStartingWorkflowState(Workflow dslWorkflow) {
        String start = dslWorkflow.getStart().getStateName();
        for (State state : dslWorkflow.getStates()) {
            if (state.getName().equals(start)) {
                return state;
            }
        }
        return null;
    }

    /**
     * Returns the workflow state with the provided name or null
     */
    public static State getWorkflowStateWithName(String name, Workflow dslWorkflow) {
        for (State state : dslWorkflow.getStates()) {
            if (state.getName().equals(name)) {
                return state;
            }
        }
        return null;
    }

    /**
     * Evaluates a JsonPath expression to true/false, used for switch states data conditions
     */
    public static boolean isTrueDataCondition(String condition, String jsonData) {
        return JsonPath.parse(jsonData).read(condition, List.class).size() > 0;
    }

    public static List<String> getInputCollectionList(String condition, String jsonData) {
        return JsonPath.parse(jsonData).read(condition, List.class);
    }

    public static int getRandomInRange(int start, int end) {
        return start + generator.nextInt(end - start + 1);
    }

    public static JsonNode getErrorData(String error) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("error", error);
        return objectNode;
    }
}
