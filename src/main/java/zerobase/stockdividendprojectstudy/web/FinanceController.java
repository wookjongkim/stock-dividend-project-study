package zerobase.stockdividendprojectstudy.web;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.stockdividendprojectstudy.model.ScrapedResult;
import zerobase.stockdividendprojectstudy.service.FinanceService;

@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {

    private final FinanceService financeService;
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(
            @PathVariable String companyName) {
        ScrapedResult result = financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(result);
    }
}
