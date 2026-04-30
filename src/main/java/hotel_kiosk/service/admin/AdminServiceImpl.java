package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.Admin;
import hotel_kiosk.dto.admin.AdminDTO;
import hotel_kiosk.mapper.admin.AdminMapper;
import hotel_kiosk.security.AuthMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminMapper adminMapper;
    private final AuthMapper authMapper;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    /* 관리자 계정 생성 */
    @Override
    public void add(AdminDTO adminDTO, String ip) {
        log.info("AdminDTO : {}", adminDTO);

        // 아이디 중복 여부 확인
        int existsId = authMapper.existsById(adminDTO.getAdminId());

        if (existsId > 0) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        // 이메일 중복 여부 확인
        int existsEmail = authMapper.existsByEmail(adminDTO.getAdminEmail());

        if (existsEmail > 0) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 1. 랜덤 비밀번호 생성
        String tempPw = generateTempPassword();

        // 2. 암호화
        String encodedPw = passwordEncoder.encode(tempPw);

        // 3. DTO에 세팅
        adminDTO.setPassword(encodedPw);
        adminDTO.setLastLoginIp(ip);

        // 4. DTO -> VO
        Admin admin = modelMapper.map(adminDTO, Admin.class);
        log.info("Admin : {}", admin);

        adminMapper.insert(admin);

        // 5. 이메일 발송
        try {
            emailService.sendTempPassword(adminDTO.getAdminEmail(), tempPw);
        } catch (Exception e) {
            log.error("메일 발송 실패", e);
        }
    }

    /* 관리자 계정 목록 */
    @Override
    public List<AdminDTO> getAll() {
        List<Admin> admins = adminMapper.selectAll();
        List<AdminDTO> adminDTOList = new ArrayList<>();

        for (Admin admin : admins) {
            AdminDTO adminDTO = modelMapper.map(admin, AdminDTO.class);
            adminDTOList.add(adminDTO);
        }

        for (AdminDTO adminDTO : adminDTOList) {
            log.info("AdminDTO : {}", adminDTO);
        }

        return adminDTOList;
    }

    /* 관리자 계정 조회 */
    @Override
    public AdminDTO getOne(String adminId) {
        Admin admin = adminMapper.selectOne(adminId);

        AdminDTO adminDTO = modelMapper.map(admin, AdminDTO.class);

        log.info(adminDTO);

        return adminDTO;
    }

    /* 관리자 검색 키워드 조회 */
    @Override
    public List<AdminDTO> searchKeyword(String keyword, String adminGrade) {
        List<Admin> admins = adminMapper.selectKeyword(keyword, adminGrade);
        List<AdminDTO> adminDTOList = new ArrayList<>();

        for (Admin admin : admins) {
            AdminDTO adminDTO = modelMapper.map(admin, AdminDTO.class);
            adminDTOList.add(adminDTO);
        }

        for (AdminDTO adminDTO : adminDTOList) {
            log.info("AdminDTO : {}", adminDTO);
        }

        return adminDTOList;
    }

    /* 관리자 계정 수정 */
    @Override
    public void modify(AdminDTO adminDTO) {
        if (adminDTO.getAdminId() == null) {
            throw new IllegalArgumentException("adminId 없음");
        }

        // DTO -> VO
        Admin admin = modelMapper.map(adminDTO, Admin.class);
        log.info("Admin : {}", admin);

        adminMapper.update(admin);
    }

    /* 관리자 계정 랜덤 비밀번호 자동 생성 */
    @Override
    public String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
