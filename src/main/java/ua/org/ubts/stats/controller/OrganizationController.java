package ua.org.ubts.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.org.ubts.stats.converter.OrganizationConverter;
import ua.org.ubts.stats.dto.OrganizationDto;
import ua.org.ubts.stats.service.OrganizationService;

import java.util.List;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationConverter organizationConverter;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<OrganizationDto> getOrganizations() {
        return organizationConverter.convertToDto(organizationService.getOrganizations());
    }

}
