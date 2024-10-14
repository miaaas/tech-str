package com.newtech.tech_str.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newtech.tech_str.service.CrawlerService;

@RestController
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @GetMapping("/triggercrawler")
    public String triggerCrawl() {
        crawlerService.triggerCrawl();
        return "Crawler triggered!";
    }

}
