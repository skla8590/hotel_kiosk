package hotel_kiosk.hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.OptionsMasterDTO;

import java.util.List;

public interface OptionService {
    /* 옵션 정책 등록 */
    void add (OptionsMasterDTO optionsMasterDTO);

    /* 옵션 정책 목록 */
    List<OptionsMasterDTO> getAll();

    /* 옵션 정책 수정 */
    void modify (OptionsMasterDTO optionsMaster);
}
