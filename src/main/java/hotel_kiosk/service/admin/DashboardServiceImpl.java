package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.domain.admin.Stocks;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.dto.admin.ReservationSummaryDTO;
import hotel_kiosk.dto.admin.RoomSummaryDTO;
import hotel_kiosk.dto.admin.StocksDTO;
import hotel_kiosk.dto.customer.MemberDTO;
import hotel_kiosk.mapper.admin.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final DashboardMapper dashboardMapper;
    private final ModelMapper modelMapper;

    @Override
    public int countAllRoom() {
        return dashboardMapper.countRoom();
    }

    @Override
    public int countCheckinRoom() {
        return dashboardMapper.countCheckin();
    }

    @Override
    public int countTodayCheckin() {
        return dashboardMapper.countCheckinToday();
    }

    @Override
    public int countTodayReservation() {
        return dashboardMapper.countReservationToday();
    }

    @Override
    public ReservationSummaryDTO countReservation() {
        return dashboardMapper.countReservation();
    }

    @Override
    public List<StocksDTO> readStockByWarning() {
        List<Stocks> stocks = dashboardMapper.findStockByWarning();
        List<StocksDTO> dtoList = new ArrayList<>();
        for (Stocks stocks1 : stocks) {
            dtoList.add(modelMapper.map(stocks1, StocksDTO.class));
        }
        return dtoList;
    }

    @Override
    public int countWaringStock() {
        return dashboardMapper.countStockWarning();
    }

    @Override
    public RoomSummaryDTO readRoom() {
        return dashboardMapper.countRooms();
    }
}
