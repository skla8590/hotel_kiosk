package hotel_kiosk.service.admin;

public interface AdminAccountService {
    /* 관리자 권한 변경 */
    void updateRole(String adminId, String adminGrade);

    /* 관리자 상태 변경 */
    void updateStatement(String adminId, String statement);
}
