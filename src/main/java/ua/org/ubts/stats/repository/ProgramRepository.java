package ua.org.ubts.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.org.ubts.stats.entity.ProgramEntity;

import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<ProgramEntity, Integer> {

    Optional<ProgramEntity> findByName(String name);

}
