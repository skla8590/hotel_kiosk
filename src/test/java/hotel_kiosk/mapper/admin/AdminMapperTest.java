package hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.domain.admin.Stocks;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.admin.PageRequestDTO;
import hotel_kiosk.dto.admin.PageResponseDTO;
import hotel_kiosk.dto.admin.ReservationSummaryDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@SpringBootTest
class AdminMapperTest {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private MemberReadMapper memberReadMapper;
    @Autowired
    private ReservationReadMapper reservationReadMapper;
    @Autowired
    private DashboardMapper dashboardMapper;

//    @Test
//    void getTime() {
//        log.info(adminMapper.getTime());
//    }

    @Test
    void selectAllTest() {
        List<Members> membersList = memberReadMapper.findCustomerPage();
        for (Members members : membersList) {
            log.info(members);
        }
    }

    @Test
    void selectByName() {
        String name = "테스트";
        List<Members> membersList = memberReadMapper.findCustomerByName(name);
        for (Members members : membersList) {
            log.info(members);
        }
    }

    @Test
    void selectByPhone() {
        String phone = "01011111111";
        List<Members> membersList = memberReadMapper.findCustomerByPhone(phone);
        for (Members members : membersList) {
            log.info(members);
        }
    }

    @Test
    void insertRoom() {
        RoomMaster roomMaster = RoomMaster.builder()
                .roomNo(1001)
                .roomName("테스트")
                .roomType("테스트타입")
                .roomView("테스트뷰")
                .roomFloor(10)
                .basePrice(1000)
                .maxPeople(2)
                .bedType("테스트")
                .area(10.0)
                .rating(5.0)
                .imageUrl("test")
                .build();
        log.info(roomMaster);
        roomMapper.insertRoomMaster(roomMaster);
        roomMapper.insertRoomStatus(roomMaster);
    }

    @Test
    void selectAllRoom() {
        List<RoomMaster> roomMasters = roomMapper.findAllRoom();
        for (RoomMaster roomMaster : roomMasters) {
            log.info(roomMaster);
        }
    }

    @Test
    void selectRoomByName() {
        int floor = 10;
        String name = "테스트";
        List<RoomMaster> roomMasters = roomMapper.findRoomByName(name, floor);
        for (RoomMaster roomMaster : roomMasters) {
            log.info(roomMaster);
        }
    }

    @Test
    void selectRoomByType() {
        int floor = 10;
        String type = "테스트타입";
        List<RoomMaster> roomMasters = roomMapper.findRoomByType(type, floor);
        for (RoomMaster roomMaster : roomMasters) {
            log.info(roomMaster);
        }
    }

    @Test
    void updateRoom() {
        RoomMaster roomMaster = RoomMaster.builder()
                .roomNo(1000)
                .roomName("수정 테스트")
                .roomType("수정 테스트타입")
                .roomView("수정 테스트뷰")
                .roomFloor(10)
                .basePrice(1300)
                .maxPeople(3)
                .bedType("수정 테스트")
                .area(10.0)
                .rating(4.5)
                .imageUrl("수정 test")
                .build();
        roomMapper.updateRoomMaster(roomMaster);
    }

    @Test
    void updateCurrentRoom() {
        RoomMaster roomMaster = RoomMaster.builder()
                .roomNo(1001)
                .roomStatus("Maintenance")
                .build();
        roomMapper.updateRoomCurrentStatus(roomMaster);
    }

    @Test
    void deleteRoom() {
        int roomNo = 1000;
        roomMapper.deleteRoomStatus(roomNo);
        roomMapper.deleteRoomMaster(roomNo);
    }

    @Test
    void insertStock() {
        Stocks stocks = Stocks.builder()
                .stockName("테스트")
                .stockCount(3)
                .minStock(4)
                .build();
        stockMapper.insertStock(stocks);
        stockMapper.updateStockStatus();
    }

    @Test
    void findAllStock() {
        List<Stocks> stocksList = stockMapper.findAllStock();
        for (Stocks stocks : stocksList) {
            log.info(stocks);
        }
    }

    @Test
    void updateStockUp() {
        int stockId = 1;
        stockMapper.updateStockUp(stockId);
        stockMapper.updateStockStatus();
    }

    @Test
    void updateStockDown() {
        int stockId = 1;
        stockMapper.updateStockDown(stockId);
        stockMapper.updateStockStatus();
    }

    @Test
    void findStockByWarning() {
        List<Stocks> stocksList = stockMapper.findStockByWarning();
        for (Stocks stocks : stocksList) {
            log.info(stocks);
        }
    }

    @Test
    void deleteStock() {
        int stockId = 1;
        stockMapper.deleteStock(stockId);
    }

    @Test
    void findAllReservation() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(10)
                .size(10)
                .build();
//        PageResponseDTO<Reservations> reservationsList = reservationReadMapper.findAllReservation(pageRequestDTO);
//        for (Reservations reservations : reservationsList) {
//            log.info(reservations);
//        }
    }

    @Test
    void findReservationByCheckin() {
        String checkin = "2026-03-28";
        List<Reservations> reservationsList = reservationReadMapper.findReservationByCheckin(checkin);
        for (Reservations reservations : reservationsList) {
            log.info(reservations);
        }

    }

    @Test
    void findReservationByStatus() {
        String status = "Reserved";
        List<Reservations> reservationsList = reservationReadMapper.findReservationByStatus(status);
        for (Reservations reservations : reservationsList) {
            log.info(reservations);
        }
    }

    @Test
    void findReservationByNum() {
        String reservationId = "40";
        Reservations reservations = reservationReadMapper.findReservationByNum(reservationId);
        log.info(reservations);

    }

    @Test
    void findReservationByName() {
        String name = "테스트";
        Reservations reservations = reservationReadMapper.findReservationByName(name);
        log.info(reservations);
    }

    @Test
    void updateReservation() {
        Reservations reservations = Reservations.builder()
                .reservationId("40")
                .status("Reserved")
                .checkinDate(LocalDate.of(2026, 3, 31))
                .checkoutDate(LocalDate.of(2026, 4, 30))
                .roomNo(1000)
                .build();
        reservationReadMapper.updateReservation(reservations);
    }

    @Test
    void countRoom() {
        int room = dashboardMapper.countRoom();
        log.info(room);
    }

    @Test
    void countCheckin() {
        int checkin = dashboardMapper.countCheckin();
        log.info(checkin);

    }

    @Test
    void countCheckinToday() {
        int todayCheckin = dashboardMapper.countCheckinToday();
        log.info(todayCheckin);
    }

    @Test
    void countReservationToday() {
        int reservation = dashboardMapper.countReservationToday();
        log.info(reservation);
    }

    @Test
    void countStockWarning() {
        int stock = dashboardMapper.countStockWarning();
        log.info(stock);
    }

    @Test
    void countReservationTest() {
        ReservationSummaryDTO result = dashboardMapper.countReservation();
        log.info(result);
    }

    @Test
    void findRoomOptionsTest() {
        String id = "260705-586025";
        log.info(reservationReadMapper.findRoomOptions(id));
    }
}