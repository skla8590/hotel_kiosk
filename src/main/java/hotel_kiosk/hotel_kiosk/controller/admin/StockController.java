package hotel_kiosk.hotel_kiosk.controller.admin;

import hotel_kiosk.dto.admin.StocksDTO;
import hotel_kiosk.service.admin.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/stock")
@Log4j2
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping
    public String stock(Model model) {
        log.info("stock...");
        List<StocksDTO> stocksDTOList = stockService.read();
        log.info(stocksDTOList);
        model.addAttribute("inventoryList", stocksDTOList);
        return "admin/operation/room/stock";
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<StocksDTO>> searchStocks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {

        log.info("검색 keyword: " + keyword);
        log.info("검색 status: " + status);

        List<StocksDTO> result = stockService.read();

        if (keyword != null && !keyword.isBlank()) {
            List<StocksDTO> byName = stockService.readByName(keyword);
            if (byName != null && !byName.isEmpty()) {
                result = result.stream()
                        .filter(byName::contains)
                        .toList();
            }
        }

        // 5. 상태 검색
        if (status != null && !status.isBlank()) {
            List<StocksDTO> byStatus = stockService.readByStatus(status);

            result = result.stream()
                    .filter(byStatus::contains)
                    .toList();
        }

        log.info(ResponseEntity.ok(result));

        return ResponseEntity.ok(result.stream()
                .distinct()
                .toList());
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public StocksDTO registerStock(@Valid @RequestBody StocksDTO stocksDTO) {
        log.info("registerStock...");
        log.info(stocksDTO);

        stockService.addStock(stocksDTO);

        return stocksDTO;
    }

    @GetMapping("/list")
    @ResponseBody
    public List<StocksDTO> getStocksList() {
        return stockService.read();
    }

    @PutMapping(value = "/{stockId}")
    @ResponseBody
    public StocksDTO modify(@PathVariable Integer stockId,
                             @RequestBody StocksDTO stocksDTO) {
        log.info("Put modify....");
        log.info(stockId);
        log.info(stocksDTO);

        stocksDTO.setStockId(stockId);

        stockService.modifyStock(stocksDTO);

        return stocksDTO;
    }

/*    @GetMapping("/stock/{stockId}")
    @ResponseBody
    public void readOne(@PathVariable(required = false) Integer stockId) {
        log.info("readOne...");
    }*/
}
