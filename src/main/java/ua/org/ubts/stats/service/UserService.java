package ua.org.ubts.stats.service;

import org.springframework.security.core.Authentication;
import ua.org.ubts.stats.entity.UserEntity;

import java.security.Principal;
import java.util.List;

public interface UserService {

    UserEntity getUser(Long id);

    UserEntity getUser(String login);

    UserEntity getUser(Principal principal);

    UserEntity getUser(Authentication authentication);

    List<UserEntity> getLdapUsers();

    void createUser(UserEntity userEntity);

    void updateUser(UserEntity userEntity);

    void updateUser(UserEntity userEntity, Principal principal);

    void deleteUser(Long id);

}
