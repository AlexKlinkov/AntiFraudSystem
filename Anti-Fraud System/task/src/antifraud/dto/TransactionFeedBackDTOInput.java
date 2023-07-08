package antifraud.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

public class TransactionFeedBackDTOInput {
    @Positive
    private Long transactionId;
    @Pattern(regexp = "ALLOWED|MANUAL_PROCESSING|PROHIBITED", message = "Invalid feedback value")
    private String feedback;

    public Long getTransactionId() {
        return transactionId;
    }

    public String getFeedback() {
        return feedback;
    }

}
