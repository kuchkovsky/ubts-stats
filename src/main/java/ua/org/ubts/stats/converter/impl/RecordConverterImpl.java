package ua.org.ubts.stats.converter.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.org.ubts.stats.converter.RecordConverter;
import ua.org.ubts.stats.dto.RecordDto;
import ua.org.ubts.stats.entity.RecordEntity;

@Component
public class RecordConverterImpl implements RecordConverter {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RecordEntity convertToEntity(RecordDto dto) {
        return modelMapper.map(dto, RecordEntity.class);
    }

    @Override
    public RecordDto convertToDto(RecordEntity entity) {
        return modelMapper.map(entity, RecordDto.class);
    }

}
