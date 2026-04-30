package hotel_kiosk.service.customer;

import hotel_kiosk.domain.admin.OptionsMaster;
import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Payments;
import hotel_kiosk.domain.customer.Reservations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class OnSiteReservationServiceTest {
    @Autowired
    private OnSiteReservationService onSiteReservationService;

    @Test
    public void testGetAvailableRooms() {
        LocalDate checkinDate = LocalDate.of(2026, 3, 31);
        LocalDate checkoutDate = LocalDate.of(2026, 4, 1);
        int regPeople = 2;
        List<RoomMaster> result = onSiteReservationService.getAvailableRooms(checkinDate, checkoutDate, regPeople);
        for (RoomMaster room : result) {
            log.info("room : {}", room);
        }
    }

    @Test
    public void testGetAllOptions() {
        List<OptionsMaster> result = onSiteReservationService.getAllOptions();
        for (OptionsMaster option : result) {
            log.info(option);
        }
    }

    @Test
    public void testProcessOnSiteReservation() {
        Members members = Members.builder()
                .memberName("tester")
                .memberPhone("01040395858")
                .memberBirth(LocalDate.of(1999, 5, 29)).build();

        Reservations reservations = Reservations.builder()
                .roomNo(1205).status("Reserved")
                .checkinDate(LocalDate.of(2026, 3, 31))
                .checkoutDate(LocalDate.of(2026, 4, 3))
                .regPeople(2).addOption(0).parkingNum("4505")
                .payStatus("Success").smsStatus("Success").build();

        Payments payments = Payments.builder()
                .payMethod("Card").approvalNo("j311949105")
                .pointAmount(0).optionCharge(0)
                .smsStatus("Success").payStatus("Success").build();

        onSiteReservationService.processOnSiteReservation(members, reservations, payments);
    }
}