package hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.StocksDTO;

import java.util.List;

public interface StockService {
    void addStock(StocksDTO stocksDTO);

    List<StocksDTO> read();

    List<StocksDTO> readByWaring();
    List<StocksDTO> readByName(String stockName);
    List<StocksDTO> readByStatus(String stockStatus);

    void stockUp(int stockId);
    void stockDown(int stockId);
    void modifyStock(StocksDTO stocksDTO);
    void modifyAllStock();

    void remove(int stockId);

}
