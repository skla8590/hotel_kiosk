package hotel_kiosk.controller.admin;

import hotel_kiosk.dto.admin.AdminDTO;
import hotel_kiosk.service.admin.AdminAccountService;
import hotel_kiosk.service.admin.AdminAuthService;
import hotel_kiosk.service.admin.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/admin/account")
@RequiredArgsConstructor
public class AdminAccountController {
    private final AdminService adminService;
    private final AdminAuthService adminAuthService;
    private final AdminAccountService adminAccountService;

    /* 계정 관리 페이지 - 목록 데이터를 Model에 담아 Thymeleaf로 렌더링 */
    @GetMapping("")
    public String account(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String adminGrade,
            Model model
    ) {
        List<AdminDTO> list = adminService.searchKeyword(keyword, adminGrade);
        model.addAttribute("list", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("adminGrade", adminGrade);
        return "admin/management/user/account";
    }

    /* 관리자 계정 등록 */
    @PostMapping("")
    @ResponseBody
    public ResponseEntity<?> registerAccount(
            @RequestBody AdminDTO adminDTO,
            HttpServletRequest request
    ) {
        log.info("계정 등록 - adminId={}", adminDTO.getAdminId());

        String ip = request.getRemoteAddr();

        try {
            if (adminDTO.getAdminId() == null || adminDTO.getAdminId().isBlank()) {
                throw new IllegalArgumentException("관리자 아이디는 필수입니다.");
            }

            adminService.add(adminDTO, ip);

            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* 아이디 / 이메일 중복 체크 */
    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(
            @RequestParam String type,
            @RequestParam String value
    ) {
        log.info("중복 체크 - type={}, value={}", type, value);

        boolean exists = false;

        if ("id".equals(type)) {
            exists = adminAuthService.existsById(value);
        } else if ("email".equals(type)) {
            exists = adminAuthService.existsByEmail(value);
        }

        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /* 관리자 계정 수정 */
    @PutMapping("/{adminId}")
    @ResponseBody
    public ResponseEntity<?> updateAccount(@PathVariable String adminId, @RequestBody AdminDTO adminDTO) {
        log.info("계정 수정 - id={}", adminId);

        AdminDTO target = adminService.getOne(adminId);
        if (target == null) {
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }

        adminDTO.setAdminId(adminId);
        adminService.modify(adminDTO);

        return ResponseEntity.noContent().build();
    }

    /* 관리자 권한 변경 */
    @PatchMapping("/{adminId}/adminGrade")
    @ResponseBody
    public ResponseEntity<?> updateRole(
            @PathVariable String adminId,
            @RequestBody Map<String, String> body
    ) {
        String adminGrade = body.get("adminGrade");

        log.info("권한 변경 요청 - id={}, grade={}", adminId, adminGrade);

        if (adminGrade == null || adminGrade.isBlank()) {
            throw new IllegalArgumentException("권한 값이 없습니다.");
        }

        AdminDTO target = adminService.getOne(adminId);
        if (target == null) {
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }

        adminAccountService.updateRole(adminId, adminGrade);

        return ResponseEntity.noContent().build();
    }

    /* 관리자 상태 변경 */
    @PatchMapping("/{adminId}/statement")
    @ResponseBody
    public ResponseEntity<?> updateStatement(
            @PathVariable String adminId,
            @RequestBody Map<String, String> body
    ) {
        String statement = body.get("statement");

        log.info("상태 변경 요청 - id={}, statement={}", adminId, statement);

        if (statement == null || statement.isBlank()) {
            throw new IllegalArgumentException("상태 값이 없습니다.");
        }

        AdminDTO target = adminService.getOne(adminId);
        if (target == null) {
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }

        adminAccountService.updateStatement(adminId, statement);

        return ResponseEntity.noContent().build();
    }
}