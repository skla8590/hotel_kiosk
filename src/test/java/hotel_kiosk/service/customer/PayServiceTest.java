package hotel_kiosk.service.customer;

import hotel_kiosk.domain.customer.Members;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class PayServiceTest {
    @Autowired
    private PayService payService;

    @Test
    void testSelectMember() {
        String memberPhone = "01011111111";
        LocalDate memberBirth = LocalDate.of(2000, 4, 2);
        Members result = payService.getMemberByPhoneAndBirth(memberPhone, memberBirth);
        log.info("result: {}", result);
    }
}