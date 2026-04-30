package hotel_kiosk.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    /* 전역 권한 설정 */
    @ModelAttribute
    public void addAdminInfo(Model model, Authentication authentication) {

        if (authentication == null) return;

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {

            model.addAttribute("adminId", user.getUsername());

            boolean isSuper = user.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_SUPER"));

            model.addAttribute("adminRole", isSuper ? "SUPER" : "GENERAL");
        }
    }

    @ModelAttribute("currentMenu")
    public String currentMenu(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/admin/dashboard")) return "dashboard";
        if (uri.startsWith("/admin/reservation")) return "reservation";
        if (uri.startsWith("/admin/room")) return "room";
        if (uri.startsWith("/admin/customer")) return "customer";
        if (uri.startsWith("/admin/stock")) return "stock";
        if (uri.startsWith("/admin/policy")) return "policy";
        if (uri.startsWith("/admin/statistic")) return "statistic";
        if (uri.startsWith("/admin/account")) return "account";

        return "";
    }
}
