package com.newtech.tech_str.web;

import com.newtech.tech_str.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/download-files")
    public ResponseEntity<String> downloadWeatherAlerts() {
        try {
            String baseUrl = "https://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_CELLS_STAT/";
            String downloadDirectory = "C:/Users/miaas/Desktop/tech-str/src/main/resources/downloads";
            weatherService.downloadFirstTenFiles(baseUrl, downloadDirectory);
            return ResponseEntity.ok("Downloaded first 10 files successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to download files: " + e.getMessage());
        }
    }
}
