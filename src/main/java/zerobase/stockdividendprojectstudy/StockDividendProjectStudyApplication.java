package zerobase.stockdividendprojectstudy;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class StockDividendProjectStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockDividendProjectStudyApplication.class, args);
    }
}
