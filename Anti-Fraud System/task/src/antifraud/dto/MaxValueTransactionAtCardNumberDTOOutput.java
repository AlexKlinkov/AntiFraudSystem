package antifraud.dto;

public class MaxValueTransactionAtCardNumberDTOOutput {
    private String cardNumber;
    private long maxALLOWED;
    private long maxMANUAL;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public long getMaxALLOWED() {
        return maxALLOWED;
    }

    public void setMaxALLOWED(long maxALLOWED) {
        this.maxALLOWED = maxALLOWED;
    }

    public long getMaxMANUAL() {
        return maxMANUAL;
    }

    public void setMaxMANUAL(long maxMANUAL) {
        this.maxMANUAL = maxMANUAL;
    }
}
