package hotel_kiosk.hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.CustomerPageRequestDTO;
import hotel_kiosk.dto.admin.CustomerPageResponseDTO;
import hotel_kiosk.dto.customer.MemberDTO;

import java.time.LocalDate;
import java.util.List;

public interface MemberReadService {
    CustomerPageResponseDTO<MemberDTO> read(CustomerPageRequestDTO pageRequestDTO);
    List<MemberDTO> readAll();

    MemberDTO find(int id);

    List<MemberDTO> readByName(String name);

    List<MemberDTO> readByPhone(String phone);

    int getAllSumPayment(int id);

    LocalDate getRecentDate(int id);
}
