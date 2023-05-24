package zerobase.stockdividendprojectstudy.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zerobase.stockdividendprojectstudy.model.Company;
import zerobase.stockdividendprojectstudy.model.ScrapedResult;
import zerobase.stockdividendprojectstudy.persist.CompanyRepository;
import zerobase.stockdividendprojectstudy.persist.DividendRepository;
import zerobase.stockdividendprojectstudy.persist.entity.CompanyEntity;
import zerobase.stockdividendprojectstudy.persist.entity.DividendEntity;
import zerobase.stockdividendprojectstudy.scraper.Scraper;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    // 일정 주기마다 진행
//    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFianceScheduling() {
        log.info("scraping scheduler is started");
        // 저장되어 있는 회사 목록을 조회
        List<CompanyEntity> companies = companyRepository.findAll();
        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company : companies) {
            log.info("scraping scheduler is started -> " + company.getName());
            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(
                    Company.builder()
                            .name(company.getName())
                            .ticker(company.getTicker())
                            .build()
            );

            // 스크래핑한 방금 정보 중 Db에 없는 값은 저장
            // saveAll로 하는 경우 중복이 없는 경우 반영을 안할수도
            scrapedResult.getDividends().stream()
                    // 디비든 모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach(e -> {
                        boolean exists = dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if(!exists){
                            dividendRepository.save(e);
                        }
                    });

            // 위와 같이만 하면 연속해서 많은 요청을 보내겠지? 부하가 되지 않도록 Thread Sleep을 하자
            try {
                Thread.sleep(3000); // 3초
            } catch (InterruptedException e) {
                // 인터럽트를 받는 스레드가 blocking 될 수 있는 메소드를 실행할 때 발생
                Thread.currentThread().interrupt();
            }
        }
        // 만약 스크래핑 주기를 변경하고 싶다면 어떻게 할까?
        // 변경하고 배포 매번하면 비효율적임(config로 관리하자)
    }

    // 예를들어 test1,test2가 있다고 하자
    // 이때 test1은 10초간 정지후 작업, test2는 그냥 계속 작업
    // 이를 그냥 봤을때는 test1 한번당, test2는 열번 실행될거같음
    // 근데 테스트1의 작업이 종료된 후 test2가 실행됨....(10초에 둘다 한번씩)
    // 즉 스케쥴러로 두개이상 작업을 돌리고자 하면 한 작업이 도는동안 다른 작업은 돌지 않음
    // Scheduler가 한개의 쓰레드로 도는것을 기본으로 함, 즉 두개의 작업이 동일한 쓰레드에서 동작함
    // 스케쥴러는 메인 쓰레드와 별도의 쓰레드로 동작함
    // 이를 해결하기 위해서는 여러개의 스레드를 관리하는 Thread pool이 필요
    // Thread Pool 없이 계속 생성, 삭제하는것은 비용이 많이 듬
    // CPU처리가 많거나(N+1), IO 작업이 많은 경우(N*2)
}
