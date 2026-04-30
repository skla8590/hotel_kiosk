package hotel_kiosk.service.customer;

import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.customer.AvailableRoomForExtendedDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class ExtendedReservationServiceTest {
    @Autowired
    private ExtendedReservationService extendedReservationService;

    @Test
    void testGetReservation() {
        String reservationId = "260328-699318";
        log.info(extendedReservationService.getReservationById(reservationId));
    }

    @Test
    void testGetDetail() {
        String reservationId = "260328-699318";
        List<AvailableRoomForExtendedDTO> result = extendedReservationService.getReservationDetail(reservationId);
        for (AvailableRoomForExtendedDTO dto : result) {
            log.info(dto);
        }
    }

    @Test
    void testGetAvailableRoom() {
        Reservations reservations = Reservations.builder()
                .reservationId("260330-206569")
                .roomNo(1101)
                .checkinDate(LocalDate.of(2026, 3, 30))
                .checkoutDate(LocalDate.of(2026, 4, 1)).build();
        log.info(extendedReservationService.getAvailableRoom(reservations));
    }

    @Test
    void testGetRoomInfo() {
        int roomNo = 1201;
        log.info(extendedReservationService.getRoomInfo(roomNo));
    }

    @Test
    void testGetMemberInfo() {
        Long memberNo = 1L;
        log.info(extendedReservationService.getMemberInfo(memberNo));
    }

    @Test
    void testGetPaymentAmount() {
        String reservationId = "260419-327693";
        log.info(extendedReservationService.getPaymentAmount(reservationId));
    }
}