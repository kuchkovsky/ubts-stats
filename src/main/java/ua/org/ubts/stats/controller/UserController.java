package ua.org.ubts.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.org.ubts.stats.converter.UserConverter;
import ua.org.ubts.stats.dto.MessengerIdDto;
import ua.org.ubts.stats.dto.UserDto;
import ua.org.ubts.stats.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserDto userDto) {
        userService.createUser(userConverter.convertToEntity(userDto));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping
    public void updateUser(@RequestBody UserDto userDto, Principal principal) {
        userService.updateUser(userConverter.convertToEntity(userDto), principal);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Boolean ldap, @RequestParam(required = false) String phone) {
        return userConverter.convertToDto(userService.getUsers(ldap, phone));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userConverter.convertToDto(userService.getUser(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{id}/messenger_ids")
    public void setUserMessengerIds(@PathVariable Long id, @RequestBody MessengerIdDto messengerIdDto) {
        userService.setUserMessengerIds(id, messengerIdDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public UserDto getCurrentUser(Principal principal) {
        return userConverter.convertToDto(userService.getUser(principal));
    }

    @GetMapping("/telegram_id/{id}")
    public UserDto getUserByTelegramId(@PathVariable String id) {
        return userConverter.convertToDto(userService.getUserByTelegramId(id));
    }

}
