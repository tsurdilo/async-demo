package io.banking.consumer.service;

import io.banking.consumer.model.AccountInfoPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
    
import io.banking.consumer.model.TransactionEventPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MessageHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerService.class);

    private Map<String, Integer> customerAccounts = new HashMap<>();

    @Autowired
    PublisherService publisherService;


    @KafkaListener(topics = "customer.transaction.03", groupId = "myGroupId", properties = {"spring.json.value.default.type=io.banking.consumer.model.TransactionEventPayload"})
    public void consumeTransactionEvent(@Payload TransactionEventPayload payload,
                                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key,
                                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {

        if(!customerAccounts.containsKey(payload.getCustomerId())) {
            customerAccounts.put(payload.getCustomerId(), 0);
        }

        customerAccounts.put(payload.getCustomerId(), customerAccounts.get(payload.getCustomerId()) + payload.getAmount());

        System.out.println("******* Sending totals: *******");
        AccountInfoPayload accountInfoPayload = new AccountInfoPayload();
        accountInfoPayload.setCustomerId(payload.getCustomerId());
        accountInfoPayload.setTransactionId(payload.getTransactionId());
        accountInfoPayload.setTotal(customerAccounts.get(payload.getCustomerId()));
        publisherService.getAccountInfo((new Random()).nextInt(), accountInfoPayload);

    }
    
    

}
