package ua.org.ubts.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.org.ubts.stats.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByLogin(String login);

    Optional<UserEntity> findByTelegramId(String telegramId);

    @Query("SELECT u FROM UserEntity u WHERE (:ldap IS null OR u.ldapUser = :ldap) AND (:phone IS null OR (u.phone1 = :phone OR u.phone2 = :phone))")
    List<UserEntity> findAll(@Param("ldap") Boolean ldap, @Param("phone") String phone);

}
