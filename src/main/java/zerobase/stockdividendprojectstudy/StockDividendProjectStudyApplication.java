package zerobase.stockdividendprojectstudy;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class StockDividendProjectStudyApplication {

    public static void main(String[] args) {
//        SpringApplication.run(StockDividendProjectStudyApplication.class, args);

        Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1684800000&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true");
        try {
            Document document = connection.get(); // get 요청 후 파싱한 결과 리턴해줌

            Elements elements = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element element = elements.get(0);

            Element tbody = element.children().get(1);// thead는 0, tfoot은 2겠지?
            for(Element e : tbody.children()){
                String txt = e.text();
                if(!txt.endsWith("Dividend")){
                    continue;
                }

                //Apr 27, 2023 0.5 Dividend
                String[] splits = txt.split(" ");
                String month = splits[0];
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                System.out.println(year + "/" + month +"/" + day + " -> " + dividend);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
