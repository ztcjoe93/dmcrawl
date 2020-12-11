import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.io.*;

class Frame extends JFrame {
    private final static String bannerPath = "/res/danmemo.jpg";

    private CrawlerOptions options;
    private JButton getLang = new JButton("Get Language");
    private JButton changeLang = new JButton("Change Language");
    private JButton startParse = new JButton("Start Parsing");
    
    public Frame(CrawlerOptions options) {
        super("ダンメモ News Crawler");
        this.options = options;
        this.setLayout(new FlowLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 1000);
        this.setResizable(false);

        //Path imgPath = Paths.get(System.getProperty("user.dir")).getParent();
        ImageIcon icon = new ImageIcon("../res/danmemo.jpg");
        JLabel imgLabel = new JLabel(icon);

        JPanel topPanel = new JPanel();
        JPanel middlePanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        topPanel.add(imgLabel);
        getLang.addActionListener(e -> {
            System.out.println(this.options.getLanguage());
        });

        changeLang.addActionListener(e -> {
            this.options.toggleLanguage();
            System.out.println(this.options.getLanguage());
        });

        startParse.addActionListener(e -> {
            System.out.println("Starting to parse "+this.options.getLanguage()+" news.");
            Crawler.startParse();
        });


        middlePanel.add(getLang);
        middlePanel.add(changeLang);
        middlePanel.add(startParse);

        // add panels to frame
        this.add(topPanel);
        this.add(middlePanel);
        this.add(bottomPanel);

        this.setVisible(true);
    }

    void updateDisplay(String val){

    }
}
