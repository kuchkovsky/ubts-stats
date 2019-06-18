package ua.org.ubts.stats.dto;

import lombok.Data;

@Data
public class UserCredentialsDto extends BaseDto {

    private String login;
    private String password;

}
