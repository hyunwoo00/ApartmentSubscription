package project.apartment.controller.member;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import project.apartment.SecurityUtil;
import project.apartment.controller.member.dto.JoinMemberDto;
import project.apartment.controller.member.dto.ReIssueDto;
import project.apartment.controller.member.dto.SignInDto;
import project.apartment.controller.member.dto.SignOutDto;
import project.apartment.domain.Member;
import project.apartment.jwt.JwtToken;
import project.apartment.service.MemberService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public MemberResponse joinMember(@RequestBody @Valid JoinMemberDto joinMemberDto) {
        Member member = JoinMemberDto.toEntity(joinMemberDto);

        Long id = memberService.join(member);

        return new MemberResponse(member.getId(), member.getEmail());

    }

    @PostMapping("/sign-in")
    public String signIn(@RequestBody SignInDto signInDto) {
        String username = signInDto.getUsername();
        String password = signInDto.getPassword();
        JwtToken jwtToken = memberService.signIn(username, password);

        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken.getAccessToken();
    }
    @PostMapping("/reissue")
    public String reIssueToken(@RequestBody ReIssueDto reIssueDto) {
        String username = reIssueDto.getUsername();

        return memberService.reIssueAccessToken(username);
    }
    @PostMapping("/logout")
    public ResponseEntity logOut(HttpServletRequest request, @RequestBody SignOutDto signOutDto) {
        String accessToken = "";

        accessToken = resolveToken(request);
        String email = signOutDto.getEmail();

        memberService.signOut(accessToken, email);

        return new ResponseEntity(HttpStatus.OK);
    }

    private static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @PostMapping("/test")
    public String test() {
        return SecurityUtil.getCurrentUsername();
    }

    @Data
    static class MemberResponse {
        private Long id;
        private String email;

        public MemberResponse(Long id, String email) {
            this.id = id;
            this.email = email;
        }
    }
}
