package io.banking.consumer.model;

import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;


public class AccountInfoPayload {
    
    private @Valid String transactionId;
    
    private @Valid String customerId;
    
    private @Valid int total;
    

    

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
    

    
    @JsonProperty("total")
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccountInfoPayload accountInfoPayload = (AccountInfoPayload) o;
        return 
            Objects.equals(this.transactionId, accountInfoPayload.transactionId) &&
            Objects.equals(this.customerId, accountInfoPayload.customerId) &&
            Objects.equals(this.total, accountInfoPayload.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, customerId, total);
    }

    @Override
    public String toString() {
        return "class AccountInfoPayload {\n" +
        
                "    transactionId: " + toIndentedString(transactionId) + "\n" +
                "    customerId: " + toIndentedString(customerId) + "\n" +
                "    total: " + toIndentedString(total) + "\n" +
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