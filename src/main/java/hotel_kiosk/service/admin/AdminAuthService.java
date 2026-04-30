package hotel_kiosk.service.admin;

public interface AdminAuthService {
    /* 관리자 아이디 존재 여부 */
    boolean existsById(String adminId);

    /* 관리자 이메일 존재 여부 */
    boolean existsByEmail(String email);

    /* 최고 관리자 연락처 조회 */
    String findSuperAdminPhone();
}
