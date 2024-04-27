package project.apartment.controller.member.dto;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignInDto {
    private String username;
    private String password;
}
