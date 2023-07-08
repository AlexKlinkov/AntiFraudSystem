package antifraud.controllers;

import antifraud.dto.UserDTOInput;
import antifraud.dto.UserDTOInputForLockUnlock;
import antifraud.dto.UserDTOInputForRoleUpdate;
import antifraud.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping
public class RegisterController {

    private final UserServiceImpl userService;

    @Autowired
    public RegisterController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<Object> register(@Valid @RequestBody UserDTOInput userDTOInput) {
        return userService.createUser(userDTOInput);
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<Object> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<Object> deleteUserByUserName(@PathVariable String username) {
        return userService.deleteUserByUserName(username);
    }

    @DeleteMapping("/api/auth/user/")
    public ResponseEntity<Object> deleteUserByUserName() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @PutMapping("/api/auth/role")
    public ResponseEntity<Object> updateUserRole(@Valid @RequestBody UserDTOInputForRoleUpdate userDTOInput) {
        return userService.updateUserRole(userDTOInput);
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<Object> setUserAccountStatus(@Valid @RequestBody UserDTOInputForLockUnlock userDTOInput) {
        return userService.setUserAccountStatus(userDTOInput);
    }
}
