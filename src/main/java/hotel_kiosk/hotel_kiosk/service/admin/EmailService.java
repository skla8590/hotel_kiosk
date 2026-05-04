package hotel_kiosk.hotel_kiosk.service.admin;

public interface EmailService {
    /* 관리자 임시 비밀번호 발송 */
    void sendTempPassword(String to, String tempPassword);

    /* 관리자 인증 번호 발송 */
    void sendAuthCode(String email);
}
