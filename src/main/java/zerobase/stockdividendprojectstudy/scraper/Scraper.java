package zerobase.stockdividendprojectstudy.scraper;

import zerobase.stockdividendprojectstudy.model.Company;
import zerobase.stockdividendprojectstudy.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
