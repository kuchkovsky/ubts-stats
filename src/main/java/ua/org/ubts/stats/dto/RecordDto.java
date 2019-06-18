package ua.org.ubts.stats.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RecordDto extends BaseDto {

    private Integer prayerMinutes;

    private Integer christWitnesses;

    private LocalDate date;

}
