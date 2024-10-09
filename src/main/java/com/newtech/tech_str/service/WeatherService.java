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

    // download files koristeći dati URL sa NIOm
    public void downloadFiles(String fileUrl, String outputFilePath) throws IOException {
        URL url = new URL(fileUrl); //kreiramo URL object koji sadrzi nas zeljeni URL
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());//opens URL connection i odatle data-u convertamo u byte channel kako bi lakse handlali velike količine data
        Path outputPath = Paths.get(outputFilePath); //kreiramo path object za zeljenu destinaciju pohran fajlova

        Files.createDirectories(outputPath.getParent()); //ukoliko directory/path ne postoji, ovdje ga pravimo

        // download file
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {//ovdje kreiramo fileOutputStream kako bismo otvorili naš path i unijeli promjene odnosno download files u taj foldercic
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);// nakon otvaranja, uzimamo channel i pomoću readableByteChannel koji je povezan na naš url transferujemo data/files i naš path tj. folder
        } //koristimo try da bismo odmah zatvorili naš path čim zvršimo radnju

        System.out.println("File downloaded: " + outputFilePath);
    }

    // fetch-a content od URL
    public List<String> fetchContent(String baseUrl) throws Exception {
        StringBuilder content = new StringBuilder(); //bolje nego '+'
        URL url = new URL(baseUrl);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) { //cita content line by line
            String inputLine;
            while ((inputLine = in.readLine()) != null) { //iterates svaku liniju contenta
                content.append(inputLine).append("\n"); // zatim svaku tu liniju sprema u content
            }
        }
    
        System.out.println("Fetched content from URL:\n" + content.toString());
    
        return extractFileUrls(content.toString(), baseUrl);
    }
    
    //extract-a URL od fajlova sto cemo spasiti 
    public List<String> extractFileUrls(String content, String baseUrl) {
        List<String> fileUrls = new ArrayList<>();
        String[] lines = content.split("\n"); //razdvaja content org URL-a u zasebno
        
        for (String line : lines) {
            
            if (line.contains("href=\"") && line.contains(".zip")) { //provjeravamo da li je fajl .zip
                
                int startIndex = line.indexOf("href=\"") + 6;  //trazimo indeks prvog karaktera fajla i ide + 6 jer 'h' 'r' 'e' 'f' '=' '"'
                int endIndex = line.indexOf("\"", startIndex); //index zadnjeg karaktera fajla nadjemo tako sto trazi sljedeci ' " ' pocevsi od startIndexa 
                
                if (startIndex >= 6 && endIndex > startIndex) {
                    String relativeUrl = line.substring(startIndex, endIndex); //extractamo ovako sve karaktere od prvog do zadnjeg

                    if (relativeUrl.endsWith(".zip")) { //znam da su .zip fajlovi
                        String completeUrl = baseUrl + relativeUrl;
                        fileUrls.add(completeUrl);  // dodamo finalni URL fajla nasoj listi
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
