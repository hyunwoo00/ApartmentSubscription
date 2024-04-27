package project.apartment.service;


import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.apartment.domain.Member;
import project.apartment.exception.jwt.ExpiredRefreshTokenException;
import project.apartment.exception.member.NotRegisteredMemberException;
import project.apartment.exception.member.RegisteredMemberException;
import project.apartment.jwt.JwtToken;
import project.apartment.jwt.JwtTokenProvider;
import project.apartment.redis.CacheNames;
import project.apartment.redis.RedisDao;
import project.apartment.repository.MemberRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisDao redisDao;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {

        //가입된 회원인지 체크
        if(checkingMember(member)){
            throw new RegisteredMemberException("존재하는 회원입니다.");
        }

        //비밀번호 암호화해서 저장.
        String rawPassword = member.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        member.updatePassword(encodedPassword);

        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 로그인
     * @param username : email
     * @param password
     * @return
     */
    //@Cacheable(cacheNames = CacheNames.LOGINUSER, key = "'login'+ #p0 ", unless = "#result== null")
    @Transactional
    public JwtToken signIn(String username, String password) {
        //1. username(email) + password를 기반으로 Authentication 객체 생성
        // 이 때 authentication 은 인증 여부를 확인하는 authenticated 값은 false
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        //2. 실제 검증, authentication() 메서드를 통해 요청된 Member에 대한 검증 진행
        //authenticate 메서드가 실행될 때 CustomUserDetailService에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder
                .getObject()
                .authenticate(authToken);


        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        redisDao.setRefreshToken(username, jwtToken.getRefreshToken(), jwtToken.getRefreshTokenExpire().getTime());

        return jwtToken;
    }

    @Transactional
    public String reIssueAccessToken(String username){
        String refreshToken = redisDao.getRefreshToken(username);

        //refreshToken의 유효할 경우 accessToken 재발급
        if(jwtTokenProvider.validateToken(refreshToken)) {

            Member member = memberRepository.findByEmail(username);

            String authorities = member.getRoles().stream()
                    .map(e -> "ROLE_" + e)
                    .collect(Collectors.joining(","));

            return jwtTokenProvider.generateAccessToken(username, authorities);
        }
        //refreshToken이 유효하지 않은 경우 validateToken에서 에러를 던짐.
        else{
            throw new ExpiredRefreshTokenException("Refresh 토큰이 만료되었습니다.");
        }



    }

    /**
     * 로그아웃
     * 블랙 리스트에 accessToken 추가.
     */
    //@CacheEvict(cacheNames = CacheNames.USERBYEMAIL, key = "'login'+#p1")
    @Transactional
    public void signOut(String accessToken, String email) {
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisDao.setBlackList(accessToken, "logout", expiration);
        if (redisDao.hasKey(email)) {
            redisDao.deleteRefreshToken(email);
        } else{
            throw new IllegalArgumentException("이미 로그아웃한 유저입니다.");
        }
    }
    /**
     * 이메일을 통한 중복 회원 체크
     * 가입O -> Exception
     * 가입X -> void
     */
    private boolean checkingMember(Member member) {

        if(member == null) return false;

        //이메일을 통해 멤버를 찾지 못하면 예외를 발생시킴.
        try {
            memberRepository.findByEmail(member.getEmail());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 회원 탈퇴
     */
    public void withdrawMember(Long memberId) {

        //회원인지 확인
        Member member = memberRepository.findOne(memberId);
        if (!checkingMember(member)) {
            throw new NotRegisteredMemberException("존재하지 않는 회원입니다.");
        }

        memberRepository.delete(member);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void updatePassword(Long memberId, String newPassword) {
        Member member = memberRepository.findOne(memberId);

        member.updatePassword(passwordEncoder.encode(newPassword));
    }
}
