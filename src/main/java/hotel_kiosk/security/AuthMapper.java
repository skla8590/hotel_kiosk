package hotel_kiosk.security;

import hotel_kiosk.domain.admin.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    /* 관리자 인증 */
    Admin findByAdminId(String adminId);

    /* 관리자 아이디 존재 여부 */
    int existsById(String adminId);

    /* 관리자 이메일 존재 여부 */
    int existsByEmail(String email);

    /* 최고 관리자 연락처 조회 */
    String findSuperAdminPhone();

    /* 로그인 실패 횟수 증가 */
    void increaseFailCount(String username);

    /* 로그인 실패 횟수 조회 */
    int findFailCount(String username);

    /* 계정 상태 잠금 */
    void lockAdmin(String username);

    /* 로그인 실패 횟수 초기화 */
    void resetFailCount(String username);
}
