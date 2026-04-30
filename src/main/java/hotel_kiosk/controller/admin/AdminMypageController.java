package hotel_kiosk.controller.admin;

import hotel_kiosk.dto.admin.AdminDTO;
import hotel_kiosk.service.admin.AdminMypageService;
import hotel_kiosk.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/admin/mypage")
@RequiredArgsConstructor
public class AdminMypageController {
    private final AdminService adminService;
    private final AdminMypageService adminMypageService;

    /* 마이페이지 - Model에 관리자 정보를 담아 Thymeleaf로 렌더링 */
    @GetMapping("")
    public String mypage(
            Model model,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        AdminDTO admin = adminService.getOne(user.getUsername());
        log.info(admin);
        model.addAttribute("auth", admin);
        return "admin/management/user/mypage";
    }

    /* 관리자 개인정보 수정 */
    @PutMapping("")
    @ResponseBody
    public ResponseEntity<Void> updateMyInfo(
            Authentication auth,
            @RequestBody AdminDTO adminDTO
    ) {
        String adminId = auth.getName();
        adminDTO.setAdminId(adminId);
        adminService.modify(adminDTO);
        return ResponseEntity.noContent().build();
    }

    /* 관리자 개인 비밀번호 수정 */
    @PatchMapping("/password")
    @ResponseBody
    public ResponseEntity<Void> changePassword(
            Authentication auth,
            @RequestBody Map<String, String> body
    ) {
        String adminId = auth.getName();

        String currentPw = body.get("currentPassword");
        String newPw = body.get("newPassword");

        if (currentPw == null || newPw == null ||
                currentPw.isBlank() || newPw.isBlank()) {
            throw new IllegalArgumentException("비밀번호 값이 없습니다.");
        }

        adminMypageService.changePassword(adminId, currentPw, newPw);

        return ResponseEntity.noContent().build();
    }
}