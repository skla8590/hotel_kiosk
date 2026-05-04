package hotel_kiosk.hotel_kiosk.service.admin;

public interface MembersPointService {
    /* 사용 포인트 조회 (음수 합계) */
    int getUsedPoint(Long idx);

    /* 적립 포인트 조회 (양수 합계) */
    int getEarnedPoint(Long idx);
}
