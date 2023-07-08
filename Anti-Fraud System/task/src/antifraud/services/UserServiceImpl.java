package antifraud.services;

import antifraud.dto.*;
import antifraud.exceptions.ConflictException;
import antifraud.exceptions.ForbiddenException;
import antifraud.exceptions.NotFoundException;
import antifraud.mappers.UserMapper;
import antifraud.models.User;
import antifraud.repositories.UserRepository;
import antifraud.utilities.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserContext userContext;

    public ResponseEntity<Object> createUser(UserDTOInput userDTOInput) {
        if (userRepository.existsByUsernameIgnoreCase(userDTOInput.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        String role;
        if (userRepository.count() == 0) {
            role = "ROLE_ADMINISTRATOR";
        } else {
            role = "ROLE_MERCHANT";
        }
        User user = new User();
        user.setName(userDTOInput.getName());
        user.setUsername(userDTOInput.getUsername());
        user.setPassword(encoder.encode(userDTOInput.getPassword()));
        user.setRole(role);
        if (!role.equals("ROLE_ADMINISTRATOR")) {
            user.setLocked(true);
        }
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.userDTOOutputFromUser(user));
    }

    public ResponseEntity<Object> getUsers() {
        List<UserDTOOutput> users = new ArrayList<>();
        List<User> usersFromBD = userRepository.findAll();
        User userWhoSentQuery = returnUserWhoSentQueryFromContext();
        if (!usersFromBD.isEmpty() && (userWhoSentQuery.getRole().equals("ROLE_ADMINISTRATOR")
            || userWhoSentQuery.getRole().equals("ROLE_SUPPORT"))) {
            users = usersFromBD.stream()
                    .map(userMapper::userDTOOutputFromUser)
                    .sorted(Comparator.comparing(UserDTOOutput::getId))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    public ResponseEntity<Object> deleteUserByUserName(String username) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            User user = userRepository.findUserByUsernameIgnoreCase(username);
            UserDTOForDelete userDTOForDelete = userMapper.userDTOForDeleteFromUser(user);
            userRepository.delete(user);
            return ResponseEntity.status(HttpStatus.OK).body(userDTOForDelete);
        } else {
            User userWhoSentRequest = returnUserWhoSentQueryFromContext();
            if (userWhoSentRequest != null) {
                if (userWhoSentRequest.getRole().equals("ROLE_ADMINISTRATOR")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_SUPPORT");
                ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_MERCHANT");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    public ResponseEntity<Object> updateUserRole(UserDTOInputForRoleUpdate userDTOInput) {
        String username = userDTOInput.getUsername();
        String userRole = userDTOInput.getRole();
        ifUserDoesNotExistInDataBaseReturnQueryStatusNotFound(username);
        if (userRole.equals("ADMINISTRATOR") || userRole.equals("USER")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        User userFromBD = userRepository.findUserByUsernameIgnoreCase(username);
        returnRoleConflictExceptionIfRoleDoesNotCorrespondingParam(userFromBD, userRole);
        userFromBD.setRole("ROLE_" + userRole);
        userRepository.save(userFromBD);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.userDTOOutputFromUser(
                userRepository.findUserByUsernameIgnoreCase(username))
        );
    }

    public ResponseEntity<Object> setUserAccountStatus(UserDTOInputForLockUnlock userDTOInput) {
        ifUserDoesNotExistInDataBaseReturnQueryStatusNotFound(userDTOInput.getUsername());
        User userFromBD = userRepository.findUserByUsernameIgnoreCase(userDTOInput.getUsername());
        returnRoleBadRequestExceptionIfRoleCorrespondingParam(userFromBD, "ROLE_ADMINISTRATOR");
        UserDTOOutputAccountStatus result = userMapper.userDTOOutputAccountStatusFromUserDTOInputForLockUnlock(
                userDTOInput
        );
        updateStatusOfUserAccountCorrespondingParam(userFromBD, userDTOInput.getOperation());
        userRepository.save(userFromBD);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // HELPER METHODS
    private User returnUserWhoSentQueryFromContext() {
        String usernameWhoSentQuery = userContext.getUserNameFromContext();
        return userRepository.findUserByUsernameIgnoreCase(usernameWhoSentQuery);
    }

    public void ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden(String role) {
        User userWhoSentQuery = returnUserWhoSentQueryFromContext();
        if (userWhoSentQuery == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        if (!userWhoSentQuery.getRole().equals(role)) {
            throw new ForbiddenException();
        }
    }

    private void ifUserDoesNotExistInDataBaseReturnQueryStatusNotFound(String username) {
        if (!userRepository.existsByUsernameIgnoreCase(username)) {
            throw new NotFoundException();
        }
    }

    private void returnRoleConflictExceptionIfRoleDoesNotCorrespondingParam(User userFromBD, String role) {
        if (userFromBD.getRole().equals("ROLE_" + role)) {
            throw new ConflictException();
        }
    }

    private void returnRoleBadRequestExceptionIfRoleCorrespondingParam(User userFromBD, String role) {
        if (userFromBD.getRole().equals(role)) {
            throw new ConflictException();
        }
    }

    private void updateStatusOfUserAccountCorrespondingParam(User userFromBD, String lockOrUnlockUserAccountStatus) {
        userFromBD.setLocked(!lockOrUnlockUserAccountStatus.equals("UNLOCK"));
    }
}
