package antifraud.dto;

import javax.validation.constraints.NotEmpty;

public class UserDTOInputForRoleUpdate {
    @NotEmpty
    private String username;
    @NotEmpty
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getRole() {
        return role;
    }

}
