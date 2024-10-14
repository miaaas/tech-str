package com.newtech.tech_str.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    public void downloadFiles(String fileUrl, String outputFilePath) throws IOException {
        URL url = new URL(fileUrl); 
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        Path outputPath = Paths.get(outputFilePath); 

        Files.createDirectories(outputPath.getParent());

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

        System.out.println("File downloaded: " + outputFilePath);
    }

    public List<String> fetchContent(String baseUrl) throws Exception {
        StringBuilder content = new StringBuilder(); 
        URL url = new URL(baseUrl);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) { //cita content line by line
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }
        }
    
        System.out.println("Fetched content from URL:\n" + content.toString());
    
        return extractFileUrls(content.toString(), baseUrl);
    }
    
    public List<String> extractFileUrls(String content, String baseUrl) {
        List<String> fileUrls = new ArrayList<>();
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            
            if (line.contains("href=\"") && line.contains(".zip")) {
                
                int startIndex = line.indexOf("href=\"") + 6;
                int endIndex = line.indexOf("\"", startIndex);
                
                if (startIndex >= 6 && endIndex > startIndex) {
                    String relativeUrl = line.substring(startIndex, endIndex);

                    if (relativeUrl.endsWith(".zip")) {
                        String completeUrl = baseUrl + relativeUrl;
                        fileUrls.add(completeUrl);
                    }
                }
            }
        }

        System.out.println("Found " + fileUrls.size() + " file(s).");
        return fileUrls;
    }

    public void downloadFirstTenFiles(String baseUrl, String downloadDirectory) throws Exception {
        List<String> fileUrls = fetchContent(baseUrl);
  
        int limit = Math.min(10, fileUrls.size());
        for (int i = 0; i < limit; i++) {
            String fileUrl = fileUrls.get(i);
            String outputFilePath = downloadDirectory + "/" + fileUrl.substring(fileUrl.lastIndexOf('/') + 1); //ovo nam je finalni path za download fajlova tj. nas path do foldera + ime fajla
            downloadFiles(fileUrl, outputFilePath);
        }
    }


}
