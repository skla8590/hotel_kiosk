package hotel_kiosk.service.customer;

import hotel_kiosk.dto.customer.ReservationsDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@Log4j2
@SpringBootTest
class ReservationIdServiceImplTest {
    @Autowired
    private ReservationIdService reservationIdService;

    @Test
    void getOneReservation() {
        String reservationId = "260421-400731";
        ReservationsDTO result = reservationIdService.getOneReservation(reservationId);
        log.info(result);
    }
}