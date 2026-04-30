package hotel_kiosk.service.admin;

import hotel_kiosk.mapper.admin.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAccountServiceImpl implements AdminAccountService {
    private final AdminMapper adminMapper;

    /* 관리자 권한 변경 */
    @Override
    public void updateRole(String adminId, String adminGrade) {
        adminMapper.updateRole(adminId, adminGrade);
    }

    /* 관리자 상태 변경 */
    @Override
    public void updateStatement(String adminId, String statement) {
        adminMapper.updateStatement(adminId, statement);
    }
}
