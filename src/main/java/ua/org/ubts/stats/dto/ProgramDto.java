package ua.org.ubts.stats.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProgramDto extends BaseDto {

    private String name;

    private List<GroupDto> groups;

}
