package project.apartment.controller.member.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.*;
import project.apartment.domain.Address;
import project.apartment.domain.Member;

@Data
public class JoinMemberDto {

    @Column(nullable = false)
    @Email
    private String email;
    @Column(nullable = false)
    private String password;
    private String name;
    private String siDo;
    private String sgg;

    public static Member toEntity(JoinMemberDto joinMemberDto) {
        return Member.createMember(joinMemberDto.getName(),
                joinMemberDto.getEmail(),
                joinMemberDto.getPassword(),
                Address.createAddress(joinMemberDto.getSiDo(), joinMemberDto.getSgg(), ""),
                "USER");
    }


}
