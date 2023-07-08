package antifraud.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class IPDTOInput {
    @NotEmpty
    @Pattern(regexp = "^(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])" +
            "(\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])){3}$",
            message = "Invalid IP address format")
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
