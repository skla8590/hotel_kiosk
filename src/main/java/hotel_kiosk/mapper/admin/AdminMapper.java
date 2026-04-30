package hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.Admin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    /* 관리자 계정 생성 */
    void insert (Admin admin);

    /* 관리자 계정 목록 */
    List<Admin> selectAll ();

    /* 관리자 개인정보 조회 */
    Admin selectOne (String adminId);

    /* 관리자 검색 키워드 조회 */
    List<Admin> selectKeyword (String keyword, String adminGrade);

    /* 관리자 개인정보 수정 (연락처, 이메일 등) */
    void update (Admin admin);

    /* 현재 비밀번호 조회 */
    String findPasswordById(String adminId);

    /* 비밀번호 변경 */
    void updatePassword(String adminId, String password);

    /* 관리자 권한 변경 */
    void updateRole(String adminId, String adminGrade);

    /* 관리자 상태 변경 */
    void updateStatement(String adminId, String statement);
}