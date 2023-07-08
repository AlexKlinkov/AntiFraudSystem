package antifraud.mappers;

import antifraud.dto.*;
import antifraud.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTOOutput userDTOOutputFromUser(User user) {
        if (user == null) {
            return null;
        }

        UserDTOOutput userDTOOutput = new UserDTOOutput();
        userDTOOutput.setId(user.getId());
        userDTOOutput.setName(user.getName());
        userDTOOutput.setUsername(user.getUsername());
        userDTOOutput.setRole(user.getRole().substring(5)); // cut off prefix ROLE_
        return userDTOOutput;
    }

    public UserDTOForDelete userDTOForDeleteFromUser(User user) {
        if (user == null) {
            return null;
        }

        UserDTOForDelete userDTOForDelete = new UserDTOForDelete();
        userDTOForDelete.setUsername(user.getUsername());
        userDTOForDelete.setStatus("Deleted successfully!");
        return userDTOForDelete;
    }

    public UserDTOOutputAccountStatus userDTOOutputAccountStatusFromUserDTOInputForLockUnlock(
            UserDTOInputForLockUnlock userDTOInput
    ) {
        if (userDTOInput == null) {
            return null;
        }
        UserDTOOutputAccountStatus userDTOOutputAccountStatus = new UserDTOOutputAccountStatus();
        userDTOOutputAccountStatus.setStatus("User " + userDTOInput.getUsername() + " "
                + userDTOInput.getOperation().toLowerCase() + "ed!");
        return userDTOOutputAccountStatus;
    }
}
