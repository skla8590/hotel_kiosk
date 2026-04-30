package hotel_kiosk.controller.admin;

import hotel_kiosk.domain.admin.Admin;
import hotel_kiosk.security.AuthMapper;
import hotel_kiosk.service.admin.AdminAuthService;
import hotel_kiosk.service.admin.EmailAuthService;
import hotel_kiosk.service.admin.EmailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * AdminAuthController
 * ─────────────────────────────────────────────────────
 * 담당 URL
 * GET  /admin/login          → 로그인 페이지 렌더
 * POST /admin/login          → Spring Security 가 처리 (직접 구현 X)
 * POST /admin/email/verify          → 인증번호 발송
 * POST /admin/email/verify/confirm  → 인증번호 확인
 * ─────────────────────────────────────────────────────
 */
@Log4j2
@Controller
@RequiredArgsConstructor
public class AdminAuthController {
    private final AdminAuthService adminAuthService;
    private final EmailService emailService;
    private final EmailAuthService emailAuthService;
    private final AuthMapper authMapper;

    /* 마지막 로그인 아이디 세션 키 */
    private static final String SESSION_LAST_ID = "LAST_LOGIN_ID";

    /* ═══════════════════════════════════════════════════
       1. 로그인 페이지 (GET /admin/login)
    ═══════════════════════════════════════════════════ */
    /**
     * 로그인 폼을 렌더링합니다.
     *
     * <p>Spring Security 가 인증 실패 시 /admin/login?error 로 리다이렉트하므로
     * {code param.error} 파라미터를 감지해 에러 메시지와 실패 횟수를 모델에 담아 전달합니다.</p>
     *
     * <p>실패 횟수가 5회 이상이면 {code locked = true} 를 전달해
     * Thymeleaf 가 잠금 모달을 자동으로 열도록 합니다.</p>
     * <p>
     * param error   Spring Security 실패 시 ?error 파라미터 (없으면 null)
     * param session 현재 HTTP 세션
     * param model   Thymeleaf 모델
     * return 템플릿 경로: admin/login
     */
    @GetMapping("/admin/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            HttpSession session,
            Model model
    ) {
        // 최고 관리자 연락처 (DB 또는 설정에서 가져오기)
        model.addAttribute("superAdminPhone", adminAuthService.findSuperAdminPhone());

        String username = (String) session.getAttribute(SESSION_LAST_ID);

        int failCount = 0;

        if (username != null) {
            Admin admin = authMapper.findByAdminId(username);
            if (admin != null) {
                failCount = admin.getFailCount();
            }
        }

        if (error != null) {
            String errorMsg;

            // ──────────── 에러 메시지 결정 ────────────────
            switch (error) {
                case "locked":
                    errorMsg = "로그인 5회 실패로 계정이 잠겼습니다. 최고 관리자에게 문의하세요.";
                    break;
                case "leave", "absence":
                    errorMsg = "해당 계정은 사용할 수 없습니다.";
                    break;
                case "fail":
                default:
                    errorMsg = "아이디 또는 비밀번호가 올바르지 않습니다. (" + failCount + "/5)";
            }

            model.addAttribute("errorMsg", errorMsg);
            model.addAttribute("failCount", failCount);

        }

        return "admin/management/auth/login";   // templates/admin/login.html
    }

    /* ═══════════════════════════════════════════════════
       2. 이메일 인증번호 발송 (POST /admin/email/verify)
    ═══════════════════════════════════════════════════ */
    /**
     * 입력된 이메일로 6자리 인증번호를 발송합니다.
     *
     * <p>인증번호와 만료 시각을 세션에 저장하고,
     * 실제 이메일 발송은 {@code EmailService} 에 위임합니다.</p>
     */
    @PostMapping("/admin/email/verify")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendVerifyCode(
            @RequestBody Map<String, String> body
    ) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "이메일을 입력해주세요."));
        }

        // ──────────── 이메일 존재 여부 확인 ───────────────
        if (!adminAuthService.existsByEmail(email)) {
            return ResponseEntity.ok(
                    Map.of("success", false, "message", "등록된 이메일이 아닙니다."));
        }

        // ──────────── 인증 번호 Redis 저장 및 이메일 발송 ─────────────────
        emailService.sendAuthCode(email);

        log.info("인증번호 발송 완료: {}", email);

        return ResponseEntity.ok(
                Map.of("success", true, "message", "인증번호가 발송되었습니다."));
    }

    /* ═══════════════════════════════════════════════════
       3. 인증번호 확인 (POST /admin/email/verify/confirm)
    ═══════════════════════════════════════════════════ */
    /**
     * 사용자가 입력한 인증번호를 세션에 저장된 값과 비교합니다.
     * <p>
     * 일치하고 만료 시각 이내이면 성공,
     * 그 외에는 실패 응답을 반환합니다.
     */
    @PostMapping("/admin/email/verify/confirm")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmVerifyCode(
            @RequestBody Map<String, String> body
    ) {
        String email = body.get("email");
        String inputCode = body.get("code");

        if (email == null || inputCode == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "잘못된 요청입니다."));
        }

        try {
            // Redis에서 인증번호 검증
            emailAuthService.verifyCode(email, inputCode);

            return ResponseEntity.ok(
                    Map.of("success", true, "message", "인증이 완료되었습니다."));

        } catch (RuntimeException e) {
            return ResponseEntity.ok(
                    Map.of("success", false, "message", e.getMessage()));
        }
    }
}
