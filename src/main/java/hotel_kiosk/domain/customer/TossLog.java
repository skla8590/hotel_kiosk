package hotel_kiosk.domain.customer;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TossLog {
    private Long lno;               // 프라이머리 키
    private Long paymentId;         // 결제번호
    private String paymentKey;      // 토스 결제 키
    private String resultCode;      // 결과코드 (성공/실패)
    private String resultMessage;   // 결과 메시지
    private String logContent;      // 로그 기록
    private LocalDateTime createdAt; // 생성일시
}
