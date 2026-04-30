package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.Stocks;
import hotel_kiosk.dto.admin.ReservationSummaryDTO;
import hotel_kiosk.dto.admin.RoomSummaryDTO;
import hotel_kiosk.dto.admin.StocksDTO;

import java.util.List;

public interface DashboardService {
    int countAllRoom();

    int countCheckinRoom();

    int countTodayCheckin();

    int countTodayReservation();

    ReservationSummaryDTO countReservation();

    /* 재고 경고 목록 */
    List<StocksDTO> readStockByWarning();

    int countWaringStock();

    RoomSummaryDTO readRoom();
}
