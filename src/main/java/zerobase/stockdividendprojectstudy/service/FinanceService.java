package zerobase.stockdividendprojectstudy.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zerobase.stockdividendprojectstudy.model.Company;
import zerobase.stockdividendprojectstudy.model.Dividend;
import zerobase.stockdividendprojectstudy.model.ScrapedResult;
import zerobase.stockdividendprojectstudy.model.constants.CacheKey;
import zerobase.stockdividendprojectstudy.persist.CompanyRepository;
import zerobase.stockdividendprojectstudy.persist.DividendRepository;
import zerobase.stockdividendprojectstudy.persist.entity.CompanyEntity;
import zerobase.stockdividendprojectstudy.persist.entity.DividendEntity;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 요청이 얼마나 자주 들어올까?
    // 특정 회사에 대한 조회가 많이 들어오겠지?, 즉 동일한 데이터에 대한 요청이 많이 올 것
    // 이때 캐시에서 꺼내줌으로서 빠른 응답
    // 자주 변경되는 데이터 인가?
    // 변경이 잦으면 update할때마다 캐시의 데이터 또한 삭제나 업데이트 해야 함(이를 고려해야 함)
    // 배당금 데이터는 과거 배당 정보가 바뀌진 않겠지, 사명이 바뀌는 일도 잘 없을것
    // 추가되는 배당 정보 -> 많아봐야 분기에 한번, 데이터 변경이 드문 데이터임

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName){
        log.info("serach company -> " + companyName);
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

        // 2. 조회된 회사의 아이디로 배당금 조회
        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());

        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);

        // 이 메서드이후 데이터가 캐싱되었다면 이때는 위 코드처럼 따로 Repository를 조회하지 않고
        // Redis Cache에서 응답을 내려줌
    }
    // 레디스 쓰기전 33ms, 레디스에서 캐시 데이터 가져오는 경우 12ms
    // 캐시를 어떤 경우 삭제해야할까? 배당금 내역은 업데이트 되었지만, 캐시가 기존 데이터를 계속 응답으로 내려줌
    // 또는 캐시의 용량은 작기에 비워야할 경우가 생길수도
}
