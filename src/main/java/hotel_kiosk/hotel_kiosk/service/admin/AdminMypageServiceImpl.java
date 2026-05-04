package hotel_kiosk.hotel_kiosk.service.admin;

import hotel_kiosk.mapper.admin.AdminMapper;
import hotel_kiosk.service.admin.AdminMypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMypageServiceImpl implements AdminMypageService {
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    /* 관리자 비밀번호 수정 */
    @Override
    public void changePassword(String adminId, String currentPw, String newPw) {
        String encodePw = adminMapper.findPasswordById(adminId);

        if (!passwordEncoder.matches(currentPw, encodePw)) {
            throw new IllegalArgumentException("현재 비밀번호가 틀렸습니다.");
        }

        String newEncodePw = passwordEncoder.encode(newPw);

        adminMapper.updatePassword(adminId, newEncodePw);
    }
}
