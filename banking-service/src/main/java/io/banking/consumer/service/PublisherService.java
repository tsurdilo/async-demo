package io.banking.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import io.banking.consumer.model.AccountInfoPayload;
    

@Service
public class PublisherService {

    @Autowired
    private KafkaTemplate<Integer, Object> kafkaTemplate;
    
    public void getAccountInfo(Integer key, AccountInfoPayload accountInfoPayload) {
        Message<AccountInfoPayload> message = MessageBuilder.withPayload(accountInfoPayload)
                .setHeader(KafkaHeaders.TOPIC, "customer.accountinfo.03")
                .setHeader(KafkaHeaders.MESSAGE_KEY, key)
                .build();
        kafkaTemplate.send(message);
    }
}
