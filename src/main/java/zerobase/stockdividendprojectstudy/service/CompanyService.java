package zerobase.stockdividendprojectstudy.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import zerobase.stockdividendprojectstudy.model.Company;
import zerobase.stockdividendprojectstudy.model.ScrapedResult;
import zerobase.stockdividendprojectstudy.persist.CompanyRepository;
import zerobase.stockdividendprojectstudy.persist.DividendRepository;
import zerobase.stockdividendprojectstudy.persist.entity.CompanyEntity;
import zerobase.stockdividendprojectstudy.persist.entity.DividendEntity;
import zerobase.stockdividendprojectstudy.scraper.Scraper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie; // 스프링부트 빈이기에 Singleton으로 관리되고 따라서 한개의 Trie만을 가짐
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker){
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if(exists){
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        // findAll은 데이터 갯수가 많으면, 이를 다 가져와야 할까?
        return this.companyRepository.findAll(pageable);
    }
    private Company storeCompanyAndDividend(String ticker){
        // ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)){
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사 배당금 정보 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                // 요소들을 다른 형태로 매핑하기위해
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword){
        Pageable limit = PageRequest.of(0,10);
        Page<CompanyEntity> companyEntities = companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }
    public void addAutoCompleteKeyWord(String keyword){
        trie.put(keyword, null);
    }

    // 갯수가 많을것을 대비해 갯수제한하는 것도 좋은 방법일 듯
    public List<String> autoComplete(String keyword){
        return (List<String>) trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    public void deleteAutoCompleteKeyword(String keyword){
        this.trie.remove(keyword);
    }
}
