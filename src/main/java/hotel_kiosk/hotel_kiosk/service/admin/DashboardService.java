package hotel_kiosk.hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.ReservationSummaryDTO;
import hotel_kiosk.dto.admin.RoomSummaryDTO;
import hotel_kiosk.dto.admin.StocksDTO;

import java.util.List;

public interface DashboardService {
    int countAllRoom();

    int countCheckinRoom();

    int countTodayCheckin();

    ReservationSummaryDTO countReservation();

    /* 재고 경고 목록 */
    List<StocksDTO> readStockByWarning();

    int countWaringStock();

    RoomSummaryDTO readRoom();
}
