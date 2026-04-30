package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.Stocks;
import hotel_kiosk.dto.admin.StocksDTO;
import hotel_kiosk.mapper.admin.StockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockMapper stockMapper;
    private final ModelMapper modelMapper;

    @Override
    public void addStock(StocksDTO stocksDTO) {
        log.info(stocksDTO);

        Stocks stocks = modelMapper.map(stocksDTO, Stocks.class);
        log.info(stocks);
        stockMapper.insertStock(stocks);
        stockMapper.updateStockStatus();
    }

    @Override
    public List<StocksDTO> read() {

        List<Stocks> stocksList = stockMapper.findAllStock();
        List<StocksDTO> dtoList = new ArrayList<>();
        for (Stocks stocks : stocksList) {
            dtoList.add(modelMapper.map(stocks, StocksDTO.class));
        }
        return dtoList;
    }

    @Override
    public List<StocksDTO> readByWaring() {
        List<Stocks> stocksList = stockMapper.findStockByWarning();
        List<StocksDTO> dtoList = new ArrayList<>();
        for (Stocks stocks : stocksList) {
            dtoList.add(modelMapper.map(stocks, StocksDTO.class));
        }
        return dtoList;
    }

    @Override
    public List<StocksDTO> readByName(String stockName) {
        List<Stocks> stocksList = stockMapper.findStockByName(stockName);
        List<StocksDTO> dtoList = new ArrayList<>();
        for (Stocks stocks : stocksList) {
            dtoList.add(modelMapper.map(stocks, StocksDTO.class));
        }
        return dtoList;
    }

    @Override
    public List<StocksDTO> readByStatus(String stockStatus) {
        List<Stocks> stocksList = stockMapper.findStockByStatus(stockStatus);
        List<StocksDTO> dtoList = new ArrayList<>();
        for (Stocks stocks : stocksList) {
            dtoList.add(modelMapper.map(stocks, StocksDTO.class));
        }
        return dtoList;
    }

    @Override
    public void stockUp(int stockId) {
        stockMapper.updateStockUp(stockId);
        stockMapper.updateStockStatus();
    }

    @Override
    public void stockDown(int stockId) {
        stockMapper.updateStockDown(stockId);
        stockMapper.updateStockStatus();
    }

    @Override
    public void modifyStock(StocksDTO stocksDTO) {
        stockMapper.updateStock(stocksDTO);
        stockMapper.updateStockStatus();
    }

    @Override
    public void modifyAllStock() {
        stockMapper.updateStockStatus();
    }

    @Override
    public void remove(int stockId) {
        stockMapper.deleteStock(stockId);
    }
}
