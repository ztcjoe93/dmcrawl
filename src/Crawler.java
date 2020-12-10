import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.*;
import java.nio.file.*;

public class Crawler {
    final static String mainDirectory = System.getProperty("user.dir");
    final static String baseUrl = 
        "https://api-danmemo-us.wrightflyer.net/asset/notice/index?read_more=1";
    static CrawlerOptions options = new CrawlerOptions();
    final static String[] languages = {"jp"};//{"en", "jp"};

    final static Map<String, String> notifHm = Map.of("news", "1", "info", "4", "update", "2", "malfunc", "3");

    final static String testUrl = "https://api-danmemo.wrightflyer.net/asset/notice/view/3124";

    public static void main(String[] args) {
        // folder creation for notifications storage
        Path newsPath = Paths.get(mainDirectory + "/notifications/");
        try {
            for (String lang: languages){
                // sub-folders for assets
                Files.createDirectories(Paths.get(newsPath + "/" + lang + "/"));
                for (Map.Entry<String, String> set: notifHm.entrySet()){
                   Files.createDirectories(Paths.get(newsPath + "/" + lang + "/" + set.getKey() + "/"));
                    String[] filePaths = {"img/", "css/", "js/"};
                    for (String fp: filePaths){
                        Files.createDirectories(Paths.get(newsPath + "/" + lang + "/" + set.getKey() + "/" + fp));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // EN/JP folder exists
        }


        /*
        Site testSite = new Site(testUrl, "info");
        testSite.crawl();
        */

        for (String lang: languages) {
            System.out.printf("Parsing %s now...%n", lang);
            Document doc = null;
            String docUrl = null;

            if (lang == "en") {
                docUrl = baseUrl; 
            } else {
                docUrl = baseUrl.replace("-us", "");
            }

            try {
                doc = Jsoup.connect(docUrl).get();
            } catch(IOException e) {
                e.printStackTrace();
            }


            Elements newsElements = null, infoElements = null, updateElements = null, malfuncElements = null;
            for (Map.Entry<String, String> set: notifHm.entrySet()) {
                Elements ex = null;
                switch(set.getKey()){
                    case "news":
                        ex = newsElements;
                        break;
                    case "info":
                        ex = infoElements;
                        break;
                    case "update":
                        ex = updateElements;
                        break;
                    case "malfunc":
                        ex = malfuncElements;
                        break;
                }
                ex = doc.select("#tab-panel" + set.getValue() + " > .newsFeed > a");
                for(Element e: ex){
                    Site site = new Site(e.attr("href"), set.getKey());
                    site.crawl();
                }
            }
        }
    }
}
