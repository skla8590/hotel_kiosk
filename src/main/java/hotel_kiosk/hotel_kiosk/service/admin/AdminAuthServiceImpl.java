package hotel_kiosk.hotel_kiosk.service.admin;

import hotel_kiosk.security.AuthMapper;
import hotel_kiosk.service.admin.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {
    private final AuthMapper authMapper;

    /* 관리자 아이디 존재 여부 */
    @Override
    public boolean existsById(String adminId) {
        return authMapper.existsById(adminId) > 0;
    }

    /* 관리자 이메일 존재 여부 */
    @Override
    public boolean existsByEmail(String email) {
        return authMapper.existsByEmail(email) > 0;
    }

    /* 최고 관리자 연락처 조회 */
    @Override
    public String findSuperAdminPhone() {
        return authMapper.findSuperAdminPhone();
    }
}
