package ua.org.ubts.stats.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrganizationDto extends BaseDto {

    private String name;

    private List<ProgramDto> programs;

}
