package hotel_kiosk.config;

import hotel_kiosk.domain.admin.Admin;
import hotel_kiosk.mapper.admin.AdminMapper;
import hotel_kiosk.security.AdminDetailsService;
import hotel_kiosk.security.AuthMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfiguration
 * ─────────────────────────────────────────────────────
 * Spring Security 설정
 * - 로그인/로그아웃 URL 매핑
 * - 권한별 접근 제어 (SUPER_ADMIN / GENERAL_ADMIN)
 * - BCrypt 비밀번호 인코더
 * ─────────────────────────────────────────────────────
 */
@Log4j2
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AdminDetailsService adminDetailsService;
    private final AuthMapper authMapper;
    private final AdminMapper adminMapper;

    /* 계정 잠금 임계값 */
    private static final int MAX_FAIL_COUNT = 5;
    /* 마지막 로그인 아이디 세션 키 */
    private static final String SESSION_LAST_ID = "LAST_LOGIN_ID";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(adminDetailsService)

                /* 클로드 - 아예 Security 설정에서 고객(customer) 관련 경로는 CSRF 검증을 제외하는 게 맞아요.
                고객 키오스크는 세션 기반 인증이 필요 없으니까요. */

                /* ───────── 고객 경로 CSRF 제외 (키오스크는 세션 인증 불필요) ───────── */
                // todo(JHL) url 정리되면 "/JHotel/**" 있으면 될 듯?!
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/onsite/**", "/pay/**", "/checkin/**", "/JHotel/**", "/extended/**"
                        )
                )

                /* ───────────── 접근 권한 설정 ──────────────── */
                .authorizeHttpRequests(auth -> auth

                        // 로그인 페이지 · 정적 리소스 · 이메일 인증 API 는 누구나 접근 가능
                        .requestMatchers(
                                "/admin/login",
                                "/JHotel",
                                "/img/**",
                                "/css/**",
                                "/js/**",
                                "/api/admin/email/**"
                        ).permitAll()

                        // 통계·리포트, 계정 관리 → 최고 관리자만
                        .requestMatchers(
                                "/admin/statistic/**",
                                "/admin/account/**"
                        ).hasRole("SUPER")

                        // 그 외 /admin/** → 로그인한 관리자라면 모두 접근 가능
                        .requestMatchers("/admin/**", "/api/admin/**")
                        .hasAnyRole("SUPER", "GENERAL")

                        // 나머지 요청도 인증 필요
                        .anyRequest().permitAll()
                )

                /* ─────────────── 로그인 설정 ────────────────── */
                .formLogin(form -> form
                        // GET /admin/login  → AdminLoginController.loginPage()
                        .loginPage("/admin/login")

                        // POST /admin/login → Spring Security 가 인증 처리 (Controller 불필요)
                        .loginProcessingUrl("/admin/login")

                        // 로그인 성공
                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();

                            // 로그인 성공 시 DB 로그인 실패 횟수 초기화
                            authMapper.resetFailCount(username);

                            // 로그인 IP
                            String ip = request.getRemoteAddr();

                            // 로그인 IP 업데이트
                            Admin admin = Admin.builder()
                                    .adminId(username)
                                    .lastLoginIp(ip)
                                    .build();

                            adminMapper.update(admin);

                            // 로그인 세션 제거
                            request.getSession().removeAttribute(SESSION_LAST_ID);

                            response.sendRedirect("/admin/dashboard");
                        })

                        // 로그인 실패
                        .failureHandler((request, response, exception) -> {
                            /*
                             * Spring Security 내부에서 UserDetailsService에서 발생한 예외를
                             * 그대로 던지지 않고 InternalAuthenticationServiceException으로 감싸서 전달함
                             * LockedException → InternalAuthenticationServiceException (cause에 담김)
                             * 따라서 exception instanceof만 체크하면 절대 잡히지 않음
                             * 반드시 exception.getCause()까지 확인해야 함
                             */
                            Throwable cause = exception.getCause();
                            String username = request.getParameter("username");

                            // 마지막 로그인 아이디 세션 저장
                            request.getSession().setAttribute(SESSION_LAST_ID, username);

                            // 잠긴 계정
                            if (exception instanceof LockedException || cause instanceof LockedException) {
                                response.sendRedirect("/admin/login?error=locked");
                                return;
                            }

                            // 퇴사 계정
                            if (exception instanceof DisabledException || cause instanceof DisabledException) {
                                response.sendRedirect("/admin/login?error=leave");
                                return;
                            }

                            // 휴직 계정
                            if (exception instanceof AccountExpiredException || cause instanceof AccountExpiredException) {
                                response.sendRedirect("/admin/login?error=absence");
                                return;
                            }

                            Admin admin = authMapper.findByAdminId(username);

                            if (admin == null) {
                                response.sendRedirect("/admin/login?error=fail");
                                return;
                            }

                            // DB 로그인 실패 횟수 증가
                            authMapper.increaseFailCount(username);

                            admin = authMapper.findByAdminId(username);

                            if (admin.getFailCount() >= MAX_FAIL_COUNT) {
                                authMapper.lockAdmin(username); // DB의 statement 잠금 처리
                                response.sendRedirect("/admin/login?error=locked");
                                return;
                            }

                            response.sendRedirect("/admin/login?error=fail");
                        })

                        // 파라미터 이름 (HTML name 속성과 일치해야 함)
                        .usernameParameter("username")
                        .passwordParameter("password")
                )

                /* ────────────── 로그아웃 설정 ───────────────── */
                .logout(logout -> logout
                        // POST /logout → layout.html 의 로그아웃 폼이 이 URL 로 전송
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/admin/login")
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                )

                /* ──────────────── 세션 관리 ─────────────────── */
                .sessionManagement(session -> session
                        // 동일 계정 동시 로그인 1개로 제한
                        .maximumSessions(1)
                        // 새 로그인 시 기존 세션 만료 (true 이면 새 로그인 차단)
                        .maxSessionsPreventsLogin(false)
                );

        return http.build();
    }

    /* ────────────── BCrypt 비밀번호 인코더 ────────────── */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
