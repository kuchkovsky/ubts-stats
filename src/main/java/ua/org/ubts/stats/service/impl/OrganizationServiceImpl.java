package ua.org.ubts.stats.service.impl;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.org.ubts.stats.entity.OrganizationEntity;
import ua.org.ubts.stats.repository.OrganizationRepository;
import ua.org.ubts.stats.service.OrganizationService;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public List<OrganizationEntity> getOrganizations() {
        List<OrganizationEntity> organizations = organizationRepository.findAll();
        organizations.forEach(organization -> {
            Hibernate.initialize(organization.getPrograms());
            organization.getPrograms().forEach(program -> Hibernate.initialize(program.getGroups()));
        });
        return organizations;
    }

}
