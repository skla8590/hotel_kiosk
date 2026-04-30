package hotel_kiosk.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailAuthServiceImpl implements EmailAuthService {
    private final RedisTemplate<String, String> redisTemplate;
    private long EXPIRE_TIME = 5; // 인증 번호 만료 시간 5분

    /* 인증 번호 저장 */
    @Override
    public void saveAuthCode(String email, String code) {
        try {
            String key = "AUTH:" + email;
            String sendKey = "AUTH:COUNT:" + email;

            // 이미 보낸 경우 체크
            if (Boolean.TRUE.equals(redisTemplate.hasKey(sendKey))) {
                Long ttl = redisTemplate.getExpire(sendKey);
                ttl = (ttl == null) ? 0 : ttl;
                throw new RuntimeException("잠시 후 다시 시도하세요 (" + ttl + "초)");
            }

            redisTemplate.opsForValue().set(key, code, EXPIRE_TIME, TimeUnit.MINUTES);

            // 1분 재전송 제한
            redisTemplate.opsForValue().set(sendKey, "1", 1, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis 연결 실패", e);
            throw new RuntimeException("인증 서비스 사용 불가 (Redis 미연결)");
        }
    }

    /* 인증 번호 조회 */
    @Override
    public String getAuthCode(String email) {
        return redisTemplate.opsForValue().get("AUTH:" + email);
    }

    /* 인증 번호 삭제 */
    @Override
    public void deleteAuthCode(String email) {
        redisTemplate.delete("AUTH:" + email);
    }

    @Override
    public void verifyCode(String email, String inputCode) {
        String key = "AUTH:" + email;
        String countKey = "AUTH:TRY:" + email;
        String sendKey = "AUTH:COUNT:" + email;

        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            throw new RuntimeException("인증번호 만료 또는 없음");
        }

        // 시도 횟수 증가
        Long count = redisTemplate.opsForValue().increment(countKey);

        // 처음 생성될 때 TTL 설정
        if (count != null && count == 1) {
            redisTemplate.expire(countKey, 5, TimeUnit.MINUTES);
        }

        if (count != null && count > 5) {
            deleteAuthCode(email);
            redisTemplate.delete(sendKey);
            throw new RuntimeException("시도 횟수 초과");
        }

        if (!savedCode.equals(inputCode)) {
            throw new RuntimeException("인증번호 불일치");
        }

        // 성공 시 삭제
        deleteAuthCode(email);
        redisTemplate.delete(countKey);
        redisTemplate.delete(sendKey);
    }
}
