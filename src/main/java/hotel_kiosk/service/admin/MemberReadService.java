package hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.CustomerPageRequestDTO;
import hotel_kiosk.dto.admin.CustomerPageResponseDTO;
import hotel_kiosk.dto.admin.PageRequestDTO;
import hotel_kiosk.dto.admin.PageResponseDTO;
import hotel_kiosk.dto.customer.MemberDTO;
import hotel_kiosk.dto.customer.ReservationsDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface MemberReadService {
    CustomerPageResponseDTO<MemberDTO> read(CustomerPageRequestDTO pageRequestDTO);
    List<MemberDTO> readAll();

    MemberDTO find(int id);

    List<MemberDTO> readByName(String name);

    List<MemberDTO> readByPhone(String phone);

    /* 포인트 복구 */
    void addPoint(Long memberNo, int usedPoint);

    /* 포인트 회수 */
    void usePoint(Long memberNo, int earnedPoint);

    int getAllSumPayment(int id);

    LocalDate getRecentDate(int id);
}
