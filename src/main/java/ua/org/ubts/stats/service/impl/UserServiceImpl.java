package ua.org.ubts.stats.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.org.ubts.stats.entity.RoleEntity;
import ua.org.ubts.stats.entity.UserEntity;
import ua.org.ubts.stats.exception.UserAlreadyExistsException;
import ua.org.ubts.stats.exception.UserNotFoundException;
import ua.org.ubts.stats.repository.RoleRepository;
import ua.org.ubts.stats.repository.UserRepository;
import ua.org.ubts.stats.service.UserService;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String USER_ID_NOT_FOUND_MESSAGE = "Could not find users with id=";
    private static final String USER_LOGIN_NOT_FOUND_MESSAGE = "Could not find users with login=";
    private static final String USER_ALREADY_EXISTS_MESSAGE = "User with login=%s already exists";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static Supplier<UserNotFoundException> supplyUserNotFoundException(String login) {
        return () -> new UserNotFoundException(USER_LOGIN_NOT_FOUND_MESSAGE + login);
    }

    private static Supplier<UserNotFoundException> supplyUserNotFoundException(Long id) {
        return () -> new UserNotFoundException(USER_ID_NOT_FOUND_MESSAGE + id);
    }

    @Override
    public UserEntity getUser(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(supplyUserNotFoundException(login));
    }

    @Override
    public UserEntity getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(supplyUserNotFoundException(id));
    }

    @Override
    public UserEntity getUser(Principal principal) {
        return userRepository.findByLogin(principal.getName())
                .orElseThrow(supplyUserNotFoundException(principal.getName()));
    }

    @Override
    public UserEntity getUser(Authentication authentication) {
        String login = ((String) authentication.getPrincipal());
        return getUser(login);
    }

    @Override
    public List<UserEntity> getLdapUsers() {
        return userRepository.findAll().stream()
                .filter(UserEntity::isLdapUser)
                .collect(Collectors.toList());
    }

    @Override
    public void createUser(UserEntity userEntity) {
        userRepository.findByLogin(userEntity.getLogin()).ifPresent(user -> {
            throw new UserAlreadyExistsException(String.format(USER_ALREADY_EXISTS_MESSAGE, user.getLogin()));
        });
        userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
        ArrayList<RoleEntity> roleEntities = new ArrayList<>();
        roleEntities.add(roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("What about ROLE_USER in DB, the greatest dev(ops)???")));
        userEntity.setRoles(roleEntities);
        userRepository.save(userEntity);
        log.info("New user account created: {}", userEntity.getLogin());
    }

    @Override
    public void updateUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    @Override
    public void updateUser(UserEntity userEntity, Principal principal) {
        UserEntity user = getUser(principal);
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        if (StringUtils.isNotEmpty(userEntity.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity user = getUser(id);
        userRepository.deleteById(id);
        log.info("User deleted: {}", user.getLogin());
    }

}
