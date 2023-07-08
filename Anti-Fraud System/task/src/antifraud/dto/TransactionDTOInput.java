package antifraud.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

public class TransactionDTOInput {
    @Positive
    @NotNull
    private long amount;
    @NotEmpty
    @Pattern(regexp = "^(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])" +
            "(\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])){3}$",
            message = "Invalid IP address format")
    private String ip;
    @NotEmpty
    private String number;
    @NotEmpty
    private String region;
    @DateTimeFormat
    private String date;

    public long getAmount() {
        return amount;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getRegion() {
        return region;
    }
    public String getDate() {
        return date;
    }

}
