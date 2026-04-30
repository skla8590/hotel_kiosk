package hotel_kiosk.service.admin;

import hotel_kiosk.mapper.admin.MembersPointMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class MembersPointServiceImpl implements MembersPointService {
    private final MembersPointMapper membersPointMapper;

    /* 사용 포인트 조회 (음수 합계) */
    @Override
    public int getUsedPoint(Long idx) {
        return membersPointMapper.selectUsedPoint(idx);
    }

    /* 적립 포인트 조회 (양수 합계) */
    @Override
    public int getEarnedPoint(Long idx) {
        return membersPointMapper.selectEarnedPoint(idx);
    }
}
