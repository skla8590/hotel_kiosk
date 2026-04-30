package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.Admin;
import hotel_kiosk.dto.admin.AdminDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class AdminServiceImplTest {
    @Autowired
    private AdminService adminService;

    /* 관리자 계정 생성 */
    @Test
    void add() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        AdminDTO adminDTO = AdminDTO.builder()
                .adminId("admin2")
                .password(passwordEncoder.encode("1234"))
                .adminName("admin2")
                .adminEmail("admin2@naver.com")
                .adminPhone("010-2345-7890")
                .adminGrade("GENERAL")
                .lastLoginIp("123.456.7890.123")
                .statement("Working")
                .build();

        adminService.add(adminDTO, "");
    }

    /* 관리자 계정 목록 */
    @Test
    void getAll() {
        List<AdminDTO> adminDTOList = adminService.getAll();
        for (AdminDTO adminDTO : adminDTOList) {
            log.info(adminDTO);
        }
    }

    /* 관리자 계정 조회 */
    @Test
    void getOne() {
        String adminId = "admin";

        AdminDTO adminDTO = adminService.getOne(adminId);
        log.info(adminDTO);
    }

    /* 관리자 계정 수정 */
    @Test
    void modify() {
        String adminId = "admin2";

        AdminDTO adminDTO = AdminDTO.builder()
                .adminId(adminId)
                .statement("Locked")
                .build();

        adminService.modify(adminDTO);
        log.info(adminService.getOne(adminId));
    }
}