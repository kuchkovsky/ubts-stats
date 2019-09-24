package ua.org.ubts.stats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RichRecordDto extends RecordDto {

    @JsonProperty("userId")
    private String user;

}
