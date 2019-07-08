package ua.org.ubts.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.org.ubts.stats.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByLogin(String login);

}
