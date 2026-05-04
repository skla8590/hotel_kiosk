package hotel_kiosk.hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.Stocks;
import hotel_kiosk.dto.admin.ReservationSummaryDTO;
import hotel_kiosk.dto.admin.RoomSummaryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DashboardMapper {
    /* 현재 객실 수 */
    int countRoom();

    /* 현재 입실 수 */
    int countCheckin();

    /* 오늘 체크인 수 */
    int countCheckinToday();

    /* 예약 현황 수 */
    ReservationSummaryDTO countReservation();

    /* 재고 경고 목록 */
    List<Stocks> findStockByWarning();

    /* 재고 경고 수 */
    int countStockWarning();

    /* 객실 현황 수 */
    RoomSummaryDTO countRooms();
}
