package io.banking.wf.demo.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.banking.wf.demo.TransactionEventProducer;
import io.banking.wf.demo.model.TransactionEventPayload;
import io.temporal.activity.Activity;
import io.temporal.activity.DynamicActivity;
import io.temporal.common.converter.EncodedValues;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class TemporalServerlessActivities implements DynamicActivity {
    private  ObjectMapper mapper = new ObjectMapper();
    private int intCount = 0;

    @Inject
    TransactionEventProducer producer;

    @Override
    public Object execute(EncodedValues args) {
        intCount++;

        //String activityType = Activity.getExecutionContext().getInfo().getActivityType();
        String customerId  = args.get(0, String.class);
        int amount = args.get(1, Integer.class);

        try {

            TransactionEventPayload transactionEventPayload = new TransactionEventPayload();
            transactionEventPayload.setTransactionId(UUID.randomUUID().toString());
            transactionEventPayload.setCustomerId(customerId);
            transactionEventPayload.setAmount(amount);
            producer.sendTransaction(transactionEventPayload);

            return mapper.readTree(
                    getReturnJson(Activity.getExecutionContext().getInfo().getActivityType() + intCount, "invoked"));
        } catch (Exception e) {
            return null;
        }
    }

    private String getReturnJson(String key, String value) {
        return "{\n" + "  \"" + key + "\": \"" + value + "\"\n" + "}";
    }
}
