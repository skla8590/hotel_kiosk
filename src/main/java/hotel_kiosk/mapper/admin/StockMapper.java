package hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.Stocks;
import hotel_kiosk.dto.admin.StocksDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StockMapper {
    /* 재고 등록 */
    void insertStock(Stocks stocks);

    /* 재고 목록 */
    List<Stocks> findAllStock();

    /* 재고 경보 조회 */
    List<Stocks> findStockByWarning();
    List<Stocks> findStockByName(String stockName);
    List<Stocks> findStockByStatus(String stockStatus);

    /* 재고 수량 수정 */
    void updateStockUp(int stockId);    // 재고 충당
    void updateStockDown(int stockId);  // 재고 소비
    void updateStock(StocksDTO stocksDTO);      // 재고 수정
    void updateStockStatus();           // 상태

    /* 재고 삭제 */
    void deleteStock(int stockId);
}
