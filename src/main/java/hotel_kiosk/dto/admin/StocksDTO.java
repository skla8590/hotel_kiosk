package hotel_kiosk.dto.admin;


import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StocksDTO {
    private Integer stockId; // '재고 ID'
    private String stockName; // '재고명'
    private Integer stockCount; // '재고 수량'
    private Integer minStock; // '재고 최소 보유 수량'
    private String stockStatus; // '재고 보유 상태'
}
