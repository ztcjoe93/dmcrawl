import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    public static void main(String[] args) {
        // folder creation for notifications storage
        Path newsPath = Paths.get(mainDirectory + "/notifications/");
        try {
            Files.createDirectories(newsPath);
            for (String lang: languages){
                Files.createDirectory(Paths.get(newsPath + "/" + lang + "/"));
            }
        } catch (IOException e) {
            // EN/JP folder exists
        }




        /*
        Frame userInterface = new Frame();
        SwingUtilities.invokeLater(() -> {
        });
        */
    }
}
