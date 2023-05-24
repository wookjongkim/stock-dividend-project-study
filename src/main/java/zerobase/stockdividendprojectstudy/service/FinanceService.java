package zerobase.stockdividendprojectstudy.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import zerobase.stockdividendprojectstudy.model.Company;
import zerobase.stockdividendprojectstudy.model.Dividend;
import zerobase.stockdividendprojectstudy.model.ScrapedResult;
import zerobase.stockdividendprojectstudy.persist.CompanyRepository;
import zerobase.stockdividendprojectstudy.persist.DividendRepository;
import zerobase.stockdividendprojectstudy.persist.entity.CompanyEntity;
import zerobase.stockdividendprojectstudy.persist.entity.DividendEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName){
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

        // 2. 조회된 회사의 아이디로 배당금 조회
        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> Dividend.builder()
                        .date(e.getDate())
                        .dividend(e.getDividend())
                        .build())
                .collect(Collectors.toList());

        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName()).build(), dividends);
    }
}
