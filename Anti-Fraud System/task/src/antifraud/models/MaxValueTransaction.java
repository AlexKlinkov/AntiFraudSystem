package antifraud.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "max_value_transaction")
@IdClass(MaxValueTransaction.MaxValueTransactionKey.class)
public class MaxValueTransaction {

    @Id
    private String number;
    @Id
    private Long maxALLOWED;
    @Id
    private Long maxMANUAL;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getMaxALLOWED() {
        return maxALLOWED;
    }

    public void setMaxALLOWED(Long maxALLOWED) {
        this.maxALLOWED = maxALLOWED;
    }

    public Long getMaxMANUAL() {
        return maxMANUAL;
    }

    public void setMaxMANUAL(Long maxMANUAL) {
        this.maxMANUAL = maxMANUAL;
    }

    @Embeddable
    public static class MaxValueTransactionKey implements Serializable {
        private String number;
        private Long maxALLOWED;
        private Long maxMANUAL;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

}
