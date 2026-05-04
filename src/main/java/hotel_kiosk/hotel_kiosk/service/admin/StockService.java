package hotel_kiosk.hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.StocksDTO;

import java.util.List;

public interface StockService {
    void addStock(StocksDTO stocksDTO);

    List<StocksDTO> read();

    List<StocksDTO> readByName(String stockName);
    List<StocksDTO> readByStatus(String stockStatus);

    void modifyStock(StocksDTO stocksDTO);

    void remove(int stockId);

}
