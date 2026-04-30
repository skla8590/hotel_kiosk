package hotel_kiosk.service.admin;

import hotel_kiosk.dto.customer.MemberDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class MemberReadServiceTest {
    @Autowired
    private MemberReadService memberReadService;

    @Test
    void read() {
        List<MemberDTO> memberDTOS = memberReadService.read();
        for (MemberDTO memberDTO : memberDTOS) {
            log.info(memberDTO);
        }
    }

    @Test
    void readByName() {
        String name = "민준";
        List<MemberDTO> memberDTOS = memberReadService.readByName(name);
        for (MemberDTO memberDTO : memberDTOS) {
            log.info(memberDTO);
        }
    }

    @Test
    void readByPhone() {
        String phone = "01011111111";
        List<MemberDTO> memberDTOS = memberReadService.readByPhone(phone);
        for (MemberDTO memberDTO : memberDTOS) {
            log.info(memberDTO);
        }
    }

    @Test
    void readAllPayment() {
        int id = 2;
        int result = memberReadService.getAllSumPayment(id);

        log.info(result);
    }

    @Test
    void testRecent() {
        int id = 1;
        log.info(memberReadService.getRecentDate(id));

    }
}