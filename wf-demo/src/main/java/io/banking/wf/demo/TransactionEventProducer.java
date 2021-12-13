package io.banking.wf.demo;

import io.banking.wf.demo.model.TransactionEventPayload;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import io.smallrye.reactive.messaging.kafka.Record;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Random;

@ApplicationScoped
public class TransactionEventProducer {
    @Inject
    @Channel("transaction-out")
    Emitter<Record<Integer, TransactionEventPayload>> emitter;

    public void sendTransaction(TransactionEventPayload payload) {
        emitter.send(Record.of((new Random()).nextInt(), payload));
    }
}
