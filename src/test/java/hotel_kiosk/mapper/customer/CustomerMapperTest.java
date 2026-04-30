package hotel_kiosk.mapper.customer;

import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.customer.ReservationsDTO;
import jdk.jfr.Label;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class CustomerMapperTest {
    @Autowired
    CustomerMapper customerMapper;

    @Test
    void selectAllCustomer() {
        Reservations reservations = customerMapper.selectOneReservation("260620-912990");
        log.info("reservations: " + reservations);
    }

}