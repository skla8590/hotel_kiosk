package hotel_kiosk.hotel_kiosk.service.admin;

import hotel_kiosk.service.admin.EmailAuthService;
import hotel_kiosk.service.admin.EmailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Log4j2
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EmailAuthService emailAuthService;
    private static final String mail = "skla8590@naver.com";

    /* 관리자 임시 비밀번호 발송 */
    @Override
    public void sendTempPassword(String to, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(mail);
        message.setTo(to);
        message.setSubject("관리자 계정 임시 비밀번호 안내");
        message.setText(
                "호텔 키오스크 관리자 계정이 생성되었습니다.\n\n" +
                        "아이디: " + to + "\n" +
                        "임시 비밀번호: " + tempPassword + "\n\n" +
                        "보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요."
        );

        javaMailSender.send(message);
    }

    /* 관리자 인증 코드 발송 */
    @Override
    public void sendAuthCode(String email) {
        String code = String.valueOf((int) (Math.random() * 900000 + 100000));

        emailAuthService.saveAuthCode(email, code);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(mail);
        message.setTo(email);
        message.setSubject("관리자 계정 임시 인증번호 안내");
        message.setText("인증번호: " + code);

        javaMailSender.send(message);
    }
}
