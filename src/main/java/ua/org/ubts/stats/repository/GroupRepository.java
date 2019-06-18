package ua.org.ubts.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.org.ubts.stats.entity.GroupEntity;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<GroupEntity, Integer> {

    Optional<GroupEntity> findByName(String name);

}
