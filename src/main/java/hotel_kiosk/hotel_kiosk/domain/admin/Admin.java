package hotel_kiosk.hotel_kiosk.domain.admin;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private String adminId; // '관리자 아이디'
    private String password; // '관리자 비밀번호'
    private String adminName; // '관리자 이름'
    private String adminEmail; // '관리자 이메일'
    private String adminPhone; // '관리자 연락처'
    private String  adminGrade; // '관리자 등급'
    private LocalDateTime lastLogin; // '마지막 로그인 날짜'
    private String lastLoginIp; // '마지막 로그인 IP'
    private LocalDateTime createdAt; // '관리자 계정 생성일'
    private String statement; // '관리자 계정 상태'
    private int failCount; // '로그인 실패 횟수'
}
