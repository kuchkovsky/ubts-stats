package ua.org.ubts.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.org.ubts.stats.entity.OrganizationEntity;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Integer> {

    Optional<OrganizationEntity> findByName(String name);

}
