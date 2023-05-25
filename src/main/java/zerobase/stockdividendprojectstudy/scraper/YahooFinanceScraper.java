package zerobase.stockdividendprojectstudy.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import zerobase.stockdividendprojectstudy.model.Company;
import zerobase.stockdividendprojectstudy.model.Dividend;
import zerobase.stockdividendprojectstudy.model.ScrapedResult;
import zerobase.stockdividendprojectstudy.model.constants.Month;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper{
    //url을 밖에 꺼내놓는 이유
    // 이 클래스는 인스턴스화 할때 Heap 영역에 생성됨(객체니까), 멤버변수들도 같은 곳에 저장됨(Heap 영역, Scrap() 메서드 호출 마다 한개를 공유함)
    // 메서드 내부에서 정의한 변수는 stack에 생성됨(함수 호출시 지역변수로 들어가며 메서드 종료 시 stack에서 나감)
    // 만약 Json.connect 저부분에 url안에 값을 그냥 적엇을때, 이는 스택영역에 할당되며
    // 동시에 여러번 메서드가 실행된다면 url이 호출횟수만큼 stack에 쌓임 -> 메모리 낭비
    // 다 전역으로 쓰는게 이득일까??, 메모리 영역은 제한되어있기에 이를 초과하면 서비스가 죽어버림
    // 자바내부에서 GC가 Heap 영역의 인스턴스를 비워줌

    // 이 클래스 객체를 여러개 만들면 각각의 객체는 Heap 영역에 할당받을것(static이 아니라면 객체 갯수만큼 URL이 생김)
    // static으로 붙이면 URL 값이 static Area에 따로 저장이됨

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400; // 60 * 60 * 24

    @Override
    public ScrapedResult scrap(Company company){
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000;

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get(); // get 요청 후 파싱한 결과 리턴해줌

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableElement = parsingDivs.get(0);

            Element tbody = tableElement.children().get(1);// thead는 0, tfoot은 2겠지?

            List<Dividend> dividends = new ArrayList<>();
            for(Element e : tbody.children()){
                String txt = e.text();
                if(!txt.endsWith("Dividend")){
                    continue;
                }

                //Apr 27, 2023 0.5 Dividend
                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if(month < 0){
                    throw new RuntimeException("UnExpected Month Enum Value -> " + splits[0]);
                }

                dividends.add(new Dividend(LocalDateTime.of(year,month, day, 0, 0), dividend));
            }
            scrapResult.setDividends(dividends);

        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }

        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker){
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[1].trim();

            return new Company(ticker, title);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
