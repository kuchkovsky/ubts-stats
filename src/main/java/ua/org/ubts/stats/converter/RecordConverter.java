package ua.org.ubts.stats.converter;

import ua.org.ubts.stats.dto.RecordDto;
import ua.org.ubts.stats.dto.RichRecordDto;
import ua.org.ubts.stats.entity.RecordEntity;

import java.util.List;

public interface RecordConverter extends GenericConverter<RecordDto, RecordEntity> {

    RichRecordDto convertToRichDto(RecordEntity entity);

    List<RichRecordDto> convertToRichDto(List<RecordEntity> entities);

}
