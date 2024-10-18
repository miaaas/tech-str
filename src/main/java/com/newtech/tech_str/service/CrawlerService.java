package com.newtech.tech_str.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;


@Service
public class CrawlerService {

    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(CrawlerService.class);

    private List<String> foundArticles = new ArrayList<>();

    //@Scheduled(fixedRate = 120000)
    public void crawlPage() {
        logger.info("Crawling Univerzalno for iPhone articles...");
        foundArticles.clear();

        try {
            String url = "https://www.univerzalno.com/bs/search?any_collection_id=&q=iphone";
            Document document = Jsoup.connect(url).get();

            Elements products = document.select("div.product__item.style-2");
            StringBuilder emailText = new StringBuilder();
            
            for (Element product : products) {
                String title = product.select("h2.product__title a").text().trim();
                String price = product.select("span.price").text().trim();
                String productUrl = product.select("h2.product__title a").attr("href").trim();
                
                if (title.toLowerCase().contains("iphone") && title.contains("64")) {
                    foundArticles.add(productUrl);
                    logger.info("Found: {} - Price: {} - URL: {}", title, price, "https://www.univerzalno.com/" + productUrl);
                    
                    emailText.append("Name: ").append(title)
                              .append("\nPrice: ").append(price)
                              .append("\nURL: ").append("https://www.univerzalno.com" + productUrl)
                              .append("\n\n");
                }
            }

            if (!foundArticles.isEmpty()) {
                logger.info("Sending email with {} found articles.", foundArticles.size());
                emailService.sendEmail("miatechstr@gmail.com", "Found iPhone Articles", emailText.toString());
            } else {
                logger.info("No relevant articles found.");
            }

        } catch (Exception e) {
            logger.error("Error occurred while crawling unverzalno.com: ", e);
        }
    }

    public void triggerCrawl() {
        crawlPage();
    }
}