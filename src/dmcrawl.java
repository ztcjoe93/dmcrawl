import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Thread.sleep;
import static java.lang.Thread.yield;


public class dmcrawl{
    private final static String mainFolder = System.getProperty("user.dir");
    private static Frame mainFrame = new Frame();
    static File urlFile = new File(mainFolder + "/url.txt");
    static File enDirectory = new File(mainFolder + "/en");
    static File jpDirectory = new File(mainFolder + "/jp");
    static Thread t;

    public static void main(String[] args){
        if(!urlFile.exists()) {
            try {
                urlFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(!(enDirectory.exists() && jpDirectory.exists())) {
            enDirectory.mkdir();
            jpDirectory.mkdir();
        }

        SwingUtilities.invokeLater(() -> {
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(850, 900);
            mainFrame.setVisible(true);
            mainFrame.setResizable(false);
        });
    }
    private static boolean checkFile(String url){
        Scanner output = null;
        try {
            output = new Scanner(urlFile);
        } catch (FileNotFoundException e) {
            System.err.println("File cannot be found.");
            mainFrame.updateText("File cannot be found.");
        }

        while(output.hasNextLine()) {
            if(output.nextLine().equals(url)) {
                return true;
            }
        }
        return false;
    }

    private static void writeFile(String url) {
        BufferedWriter bw = null;
        try {
            FileWriter writer = new FileWriter(urlFile, true);
            bw = new BufferedWriter(writer);
            bw.write(url + "\n");
        } catch (IOException e) {
            System.err.println("File doesn't exist, please check directory");
            mainFrame.updateText("File doesn't exist, please check directory");
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void crawl(String lang){
        ArrayList<parentSite> siteList = new ArrayList<>();
        Document doc = null;
        String baseUrl = lang.equals("en") ? "https://api-danmemo-us.wrightflyer.net/asset/notice/index?read_more=1" :
                "https://api-danmemo.wrightflyer.net/asset/notice/index?read_more=1";

        try{
            doc = Jsoup.connect(baseUrl).get();
        } catch (IOException e){
            System.out.println("Error, exiting.");
            System.exit(-1);
        }

        for(Element site : doc.select("div.newsFeed > a"))
            siteList.add(new parentSite(site.attr("href")));

        t = new Thread(()->{
            deepCrawl(siteList, lang);
            try{
                sleep(500);
            } catch (InterruptedException e){
                e.printStackTrace();
            } finally {
                yield();
                System.out.println("\nCrawling complete.");
                mainFrame.updateText("\nCrawling complete.");

                mainFrame.toggleButton(mainFrame.getJPbutton());
                mainFrame.toggleButton(mainFrame.getENbutton());

                if (lang.equals("en")) {
                    mainFrame.getENbutton().setText("Fetch English Notifications");
                    mainFrame.getENbutton().setBackground(null);
                } else {
                    mainFrame.getJPbutton().setText("Fetch Japanese Notifications");
                    mainFrame.getJPbutton().setBackground(null);
                }
            }
        });
        t.start();
    }

    static void deepCrawl(ArrayList<parentSite> siteList, String lang){
        if(siteList.size() != 0){
            for(parentSite ps : siteList){
                if(!checkFile(ps.getUrl())){
                    System.out.println("\u001B[32m" + "New link! Scraping..." + "\u001B[0m");
                    mainFrame.updateText("New link! Scraping...\n");

                    Document doc = null;
                    try {
                        doc = Jsoup.connect(ps.getUrl()).get();
                    } catch (IOException e) {
                        System.err.println("Bad link, returning to previous link.");
                        mainFrame.updateText("Bad link, returning to previous link.\n");
                    }

                    assert doc != null;
                    String title = doc.select(".title").text();
                    ps.setTitle(title);
                    System.out.println(ps);
                    mainFrame.updateText(ps.getTitle() + "\n");

                    String fileName = title.replaceAll("[\\\\/:*?\"<>|]", "");
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(
                                (lang.equals("en")?enDirectory:jpDirectory).toString() + "/" +
                                        fileName + ".html"));
                        writer.write(doc.outerHtml());
                        writeFile(ps.getUrl());
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Elements links = doc.select("a");

                    int counter = 0;
                    for (Element link : links) {
                        if ((link.attr("href").equals("javascript:void(0);")) &&
                                link.attr("href").contains("api-danmemo")) {
                            String nextLink = link.attr("onclick").split("'")[1];
                            ps.addToList(new parentSite(nextLink));
                            counter += 1;
                        }
                    }

                    if (counter > 0) {
                        System.out.printf("\u001B[34m" + "%d link(s) found!" + "\u001B[0m" + "%n", counter);
                    }
                    deepCrawl(ps.getList(), lang);
                } else {
                    System.out.println("\u001B[31m" + "Link already exists, moving to next." + "\u001B[0m");
                    mainFrame.updateText("Link already exists, moving to next.\n");
                }
            }
        }
    }
}

abstract class site{
    protected String url, title;
    public abstract String getUrl();
    public abstract String getTitle();
}

class parentSite extends site{
    private ArrayList<parentSite> siteList = new ArrayList<>();
    public parentSite(String url){this.url = url;}

    @Override
    public String getUrl(){return this.url;}

    @Override
    public String getTitle(){return this.title;}

    public void setTitle(String title){this.title = title;}
    public void addToList(parentSite pSite){this.siteList.add(pSite);}
    public ArrayList<parentSite> getList(){return siteList;}

    @Override
    public String toString(){return this.title;}
}

class Frame extends JFrame{

    private final static String mainFolder = System.getProperty("user.dir");
    private JTextArea crawlInfo = new JTextArea(20, 70);

    private JButton buttonJapanese = new JButton("Fetch Japanese Notifications");
    private JButton buttonEnglish = new JButton("Fetch English Notifications");

    private JButton enFolder = new JButton("English Notifications");
    private JButton jpFolder = new JButton("Japanese Notifications");

    public Frame(){
        super("Danmemo News Crawler");
        setLayout(new FlowLayout());

        Icon image = new ImageIcon("C:/Users/ZT/IdeaProjects/dmcrawl/res/danmemo.jpg");
        JLabel mainImage = new JLabel(null, image, SwingConstants.CENTER);
        JPanel imagePanel = new JPanel();
        imagePanel.add(mainImage);

        buttonEnglish.addActionListener(e -> {
            crawlInfo.setText("");
            buttonEnglish.setText("Fetching...");
            buttonEnglish.setEnabled(false);
            buttonEnglish.setBackground(Color.red);
            buttonJapanese.setEnabled(false);
            dmcrawl.crawl("en");
        });

        buttonJapanese.addActionListener(e -> {
            crawlInfo.setText("");
            buttonJapanese.setText("Fetching...");
            buttonEnglish.setEnabled(false);
            buttonJapanese.setEnabled(false);
            dmcrawl.crawl("jp");
        });

        JPanel optionPanel = new JPanel();
        optionPanel.add(buttonEnglish);
        optionPanel.add(buttonJapanese);

        crawlInfo.setEditable(false);
        DefaultCaret caret = (DefaultCaret) crawlInfo.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(crawlInfo, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getPreferredSize();

        JPanel scrollPanel = new JPanel();
        scrollPanel.add(scrollPane);

        JPanel folderPanel = new JPanel();
        folderPanel.add(enFolder);
        folderPanel.add(jpFolder);

        enFolder.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(mainFolder + "\\en"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        jpFolder.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(mainFolder + "\\jp"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        add(imagePanel);
        add(optionPanel);
        add(scrollPanel);
        add(folderPanel);
    }

    public void updateText(String text){crawlInfo.append(text);}
    public void toggleButton(JButton jb){jb.setEnabled(true);}
    public JButton getJPbutton(){return buttonJapanese;}
    public JButton getENbutton() {return buttonEnglish;}
}