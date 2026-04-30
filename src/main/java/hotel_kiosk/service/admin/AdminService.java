package hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.AdminDTO;

import java.util.List;

public interface AdminService {
    /* 관리자 계정 생성 */
    void add(AdminDTO adminDTO, String ip);

    /* 관리자 계정 목록 */
    List<AdminDTO> getAll();

    /* 관리자 계정 조회 */
    AdminDTO getOne(String adminId);

    /* 관리자 검색 키워드 조회 */
    List<AdminDTO> searchKeyword (String keyword, String adminGrade);

    /* 관리자 계정 수정 */
    void modify(AdminDTO adminDTO);

    /* 관리자 계정 랜덤 비밀번호 자동 생성 */
    String generateTempPassword();
}
