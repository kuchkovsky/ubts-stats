package ua.org.ubts.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.org.ubts.stats.entity.RoleEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    Optional<RoleEntity> findByName(String name);

}
