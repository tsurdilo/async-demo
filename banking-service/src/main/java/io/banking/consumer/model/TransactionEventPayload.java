package io.banking.consumer.model;

import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;


public class TransactionEventPayload {
    
    private @Valid String transactionId;
    
    private @Valid String customerId;
    
    private @Valid int amount;


    /**
     * Transaction Id
     */
    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    

    /**
     * Customer Id
     */
    @JsonProperty("customerId")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    

    
    @JsonProperty("amount")
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransactionEventPayload transactionEventPayload = (TransactionEventPayload) o;
        return 
            Objects.equals(this.transactionId, transactionEventPayload.transactionId) &&
            Objects.equals(this.customerId, transactionEventPayload.customerId) &&
            Objects.equals(this.amount, transactionEventPayload.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, customerId, amount);
    }

    @Override
    public String toString() {
        return "class TransactionEventPayload {\n" +
        
                "    transactionId: " + toIndentedString(transactionId) + "\n" +
                "    customerId: " + toIndentedString(customerId) + "\n" +
                "    amount: " + toIndentedString(amount) + "\n" +
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
           return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}