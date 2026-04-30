package hotel_kiosk.mapper.admin;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MembersPointMapper {
    /* 사용 포인트 조회 (음수 합계) */
    int selectUsedPoint(Long idx);

    /* 적립 포인트 조회 (양수 합계) */
    int selectEarnedPoint(Long idx);

    /* 포인트 기록 추가 */
    void insertPointHistory(Long memberNo, Long idx, int earnedPoint, int usedPoint);
}
