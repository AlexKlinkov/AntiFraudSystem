package antifraud.dto;

import javax.validation.constraints.NotEmpty;

public class UserDTOInputForLockUnlock {
    @NotEmpty
    private String username;
    @NotEmpty
    private String operation;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }
}
