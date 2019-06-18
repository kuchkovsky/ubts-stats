package ua.org.ubts.stats.converter.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.org.ubts.stats.converter.OrganizationConverter;
import ua.org.ubts.stats.dto.OrganizationDto;
import ua.org.ubts.stats.entity.OrganizationEntity;

@Component
public class OrganizationConverterImpl implements OrganizationConverter {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrganizationEntity convertToEntity(OrganizationDto dto) {
        return modelMapper.map(dto, OrganizationEntity.class);
    }

    @Override
    public OrganizationDto convertToDto(OrganizationEntity entity) {
        return modelMapper.map(entity, OrganizationDto.class);
    }

}
