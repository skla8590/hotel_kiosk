package hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.OptionsMaster;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OptionMapper {
    /* 옵션 정책 등록 */
    void insert (OptionsMaster optionsMaster);

    /* 옵션 정책 목록 */
    List<OptionsMaster> selectAll();

    /* 옵션 정책 조회 */
    OptionsMaster selectOne (Long optionId);

    /* 옵션 정책 수정 */
    void update (OptionsMaster optionsMaster);

    /* 옵션 카테고리 조회 */
    OptionsMaster findCategory (Long optionId);

    int sumOptionCharge (String reservationId);
}
