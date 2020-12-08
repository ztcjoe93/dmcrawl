import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.*;
import java.nio.file.*;

public class Crawler {
    final static String mainDirectory = System.getProperty("user.dir");
    final static String baseUrl = 
        "https://api-danmemo.wrightflyer.net/asset/notice/index?read_more=1";
    static CrawlerOptions options = new CrawlerOptions();
    final static String[] languages = {"en", "jp"};

    final static String testUrl = "https://api-danmemo-us.wrightflyer.net/asset/notice/view/2440";

    public static void main(String[] args) {
        // folder creation for notifications storage
        Path newsPath = Paths.get(mainDirectory + "/notifications/");
        try {
            Files.createDirectories(newsPath);
            for (String lang: languages){
                // sub-folders for assets
                String[] filePaths = {"/", "/img/", "/css/", "/js/"};
                for (String fp: filePaths){
                    Files.createDirectory(Paths.get(newsPath + "/" + lang + fp));
                }
            }
        } catch (IOException e) {
            // EN/JP folder exists
        }



        Site test = new Site(testUrl);
        test.crawl();

        /*
        Frame userInterface = new Frame();
        SwingUtilities.invokeLater(() -> {
        });
        */
    }

    void initializeAssets() {
                
    }
}
