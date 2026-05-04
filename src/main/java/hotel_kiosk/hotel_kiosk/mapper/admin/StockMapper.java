package hotel_kiosk.hotel_kiosk.mapper.admin;

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
    List<Stocks> findStockByName(String stockName);
    List<Stocks> findStockByStatus(String stockStatus);

    /* 재고 수정 */
    void updateStock(StocksDTO stocksDTO);      // 재고 수정
    void updateStockStatus();           // 상태

    /* 재고 삭제 */
    void deleteStock(int stockId);
}
