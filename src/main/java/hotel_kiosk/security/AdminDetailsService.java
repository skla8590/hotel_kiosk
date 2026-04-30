package hotel_kiosk.security;

import hotel_kiosk.domain.admin.Admin;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {
    private final AuthMapper authMapper;

    @Override
    /* 관리자 인증 */
    public UserDetails loadUserByUsername(String username) {
        log.info("loadUserByUsername: {}", username);

        Admin admin = authMapper.findByAdminId(username);

        if (admin == null) {
            throw new UsernameNotFoundException("존재하지 않는 관리자");
        }

        String role = admin.getAdminGrade();

        List<GrantedAuthority> authorities = new ArrayList<>();

        if ("SUPER".equals(role)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SUPER"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_GENERAL"));
        }

        /*
         * 계정 상태에 따른 인증 제한 처리
         * 반드시 Spring Security 전용 예외를 사용해야 함
         * - LockedException         → 계정 잠김
         * - DisabledException       → 비활성(퇴사)
         * - AccountExpiredException → 휴직
         * RuntimeException 사용 시
         * → InternalAuthenticationServiceException으로 wrapping되어
         * failureHandler에서 구분 불가능해짐
         */
        String statement = admin.getStatement();

        if ("Locked".equalsIgnoreCase(statement)) {
            throw new LockedException("잠긴 계정");
        }

        if ("Leave".equalsIgnoreCase(statement)) {
            throw new DisabledException("퇴사 계정");
        }

        if ("Absence".equalsIgnoreCase(statement)) {
            throw new AccountExpiredException("휴직 계정");
        }

        return User.builder()
                .username(admin.getAdminId())
                .password(admin.getPassword())
                .roles(admin.getAdminGrade())
                .authorities(authorities)
                .build();
    }
}