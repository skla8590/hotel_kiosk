package hotel_kiosk.hotel_kiosk.service.admin;

public interface AdminMypageService {
    /* 관리자 비밀번호 수정 */
    void changePassword(String adminId, String currentPw, String newPw);
}
