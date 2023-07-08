package antifraud.dto;


import javax.validation.constraints.NotEmpty;

public class UserDTOInput {
    @NotEmpty
    private String name;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
}
