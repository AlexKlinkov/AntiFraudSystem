package antifraud.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transaction_feedback")
@IdClass(TransactionFeedBack.TransactionFeedbackId.class)
public class TransactionFeedBack {
    @Id
    private Long transactionId;
    @Id
    private String feedback;

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Embeddable
    public static class TransactionFeedbackId implements Serializable {
        private Long transactionId;
        private String feedback;
    }
}
