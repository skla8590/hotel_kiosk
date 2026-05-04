package hotel_kiosk.hotel_kiosk.service.admin;

public interface EmailAuthService {
    /* 인증 번호 저장 */
    void saveAuthCode(String email, String code);

    /* 인증 번호 조회 */
    String getAuthCode(String email);

    /* 인증 번호 삭제 */
    void deleteAuthCode(String email);

    /* 관리자 인증 번호 검증 */
    void verifyCode(String email, String inputCode);
}
