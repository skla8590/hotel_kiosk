package hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.StocksDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
class StockServiceTest {
    @Autowired(required = false)
    private StockService stockService;

    @Test
    void add() {
        StocksDTO stocksDTO = StocksDTO.builder()
                .stockName("서비스 테스트2")
                .stockCount(1)
                .minStock(4)
                .build();
        stockService.addStock(stocksDTO);
        stockService.modifyAllStock();
    }

    @Test
    void read() {
        List<StocksDTO> stocksDTOList = stockService.read();
        for (StocksDTO stocksDTO : stocksDTOList) {
            log.info(stocksDTO);
        }
    }

    @Test
    void readByWaring() {
        List<StocksDTO> stocksDTOList = stockService.readByWaring();
        for (StocksDTO stocksDTO : stocksDTOList) {
            log.info(stocksDTO);
        }
    }

    @Test
    void stockUP() {
        int stockId = 3;
        stockService.stockUp(stockId);
        stockService.modifyAllStock();
    }

    @Test
    void stockDown() {
        int stockId = 5;
        stockService.stockDown(stockId);
        stockService.modifyAllStock();
    }

    @Test
    void remove() {
        int stockId = 3;
        stockService.remove(stockId);
    }
}