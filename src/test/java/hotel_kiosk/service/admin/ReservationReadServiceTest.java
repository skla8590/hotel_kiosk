package hotel_kiosk.service.admin;

import hotel_kiosk.dto.customer.ReservationsDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class ReservationReadServiceTest {
    @Autowired
    private ReservationReadService reservationReadService;

    @Test
    void read() {
        List<ReservationsDTO> reservationDTOS = reservationReadService.read();
        for (ReservationsDTO reservationDTO : reservationDTOS) {
            log.info(reservationDTO);
        }
    }

    @Test
    void readByCheckIn() {
        String checkIn = "2026-03-28";
        List<ReservationsDTO> reservationDTOS = reservationReadService.readByCheckIn(checkIn);
        for (ReservationsDTO reservationDTO : reservationDTOS) {
            log.info(reservationDTO);
        }
    }

    @Test
    void readByStatus() {
        String status = "Reserved";
        List<ReservationsDTO> reservationDTOS = reservationReadService.readByStatus(status);
        for (ReservationsDTO reservationDTO : reservationDTOS) {
            log.info(reservationDTO);
        }
    }

    @Test
    void readById() {
        String id = "35";
        ReservationsDTO reservationDTO = reservationReadService.readByNum(id);
        log.info(reservationDTO);
    }

    @Test
    void readByName() {
        String name = "테스트";
        ReservationsDTO reservationDTO = reservationReadService.readByName(name);
        log.info(reservationDTO);
    }

    @Test
    void modify() {
        ReservationsDTO reservationDTO = ReservationsDTO.builder()
                .reservationId("40")
                .status("Reserved")
                .checkinDate(LocalDate.of(2026, 4, 1))
                .checkoutDate(LocalDate.of(2026, 4, 30))
                .roomNo(1000)
                .build();
        reservationReadService.modify(reservationDTO);
    }

    @Test
    void testOption() {
        String id = "260330-733406";
        ReservationsDTO reservationDTO = reservationReadService.getRoomOptions(id);
        log.info(reservationDTO);
    }
}