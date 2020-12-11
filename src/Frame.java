import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Frame extends JFrame {
    private CrawlerOptions options;
    private JLabel language = new JLabel();
    private JButton changeLang = new JButton("Change Language");
    private JButton startParse = new JButton("Start Parsing");
    private JTextArea textInfo = new JTextArea(20, 70);
    
    public Frame(CrawlerOptions options) {
        super("ダンメモ News Crawler");
        this.options = options;
        this.setLayout(new FlowLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 1000);
        this.setResizable(false);
        this.textInfo.setEditable(false);

        ImageIcon icon = new ImageIcon("../res/danmemo.jpg");
        JLabel imgLabel = new JLabel(icon);

        JPanel topPanel = new JPanel();
        JPanel middlePanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        topPanel.add(imgLabel);

        changeLang.addActionListener(e -> {
            this.options.toggleLanguage();
            this.language.setText(this.options.getLanguage());
        });

        startParse.addActionListener(e -> {
            this.updateText("Starting to parse "+this.options.getLanguage()+" news.");
            Thread t = new Thread(() -> Crawler.startParse());
            t.start();
        });

        // to enable text to be aligned to bottom of scrollbar
        DefaultCaret caret = (DefaultCaret) textInfo.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // scrollbar for jtextarea
        JScrollPane scrollPane = new JScrollPane(textInfo, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getPreferredSize();

        language.setText(options.getLanguage());
        middlePanel.add(language);
        middlePanel.add(changeLang);
        middlePanel.add(startParse);
        bottomPanel.add(scrollPane);

        // add panels to frame
        this.add(topPanel);
        this.add(middlePanel);
        this.add(bottomPanel);

        this.setVisible(true);
    }

    // to prevent race condition
    synchronized void updateText(String text){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeStamp = now.format(formatter);
        this.textInfo.append("["+timeStamp+"] "+text+"\n");
    }
}
