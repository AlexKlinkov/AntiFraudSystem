package antifraud.dto;

import javax.validation.constraints.NotEmpty;

public class UserDTOOutputAccountStatus {
    @NotEmpty
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
