import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URLConnection;
import java.net.URL;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.FileAlreadyExistsException;
import java.io.*;

class Site {
    private String id, url, baseUrl, title;
    private ArrayList<Site> linkedSites = new ArrayList<Site>();
    private String lang, notifType;
    private boolean official;
    private CrawlerOptions options;
    private Frame ui;

    Site(String url, String notifType, CrawlerOptions options, Frame ui) {
        this.url = url;
        this.notifType = notifType;
        this.options = options;
        this.ui = ui;

        if(!options.getSites().contains(this.url)){
            Pattern pattern = Pattern.compile("(.*\\.net)(.*)");
            Pattern cdnPattern = Pattern.compile("(.*\\/us\\/)(.*)");

            Matcher matcher = pattern.matcher(url);

            while (matcher.find()) {
                this.baseUrl = matcher.group(1);
                // check for non-danmemo urls
                if (baseUrl.contains("api-danmemo") || baseUrl.contains("cdn-danmemo")) {
                    this.official = true;
                    if (baseUrl.contains("cdn")){
                       Matcher cdnMatcher = cdnPattern.matcher(url);

                      if (cdnMatcher.find()){
                          this.lang = "en";
                      } else {
                          this.lang = "jp";
                      }
                    } else {
                        if (baseUrl.contains("us")) {
                            this.lang = "en"; 
                        } else {
                            this.lang = "jp";
                        } 
                    }
                } else {
                    this.official = false;
                }
            } 
        }
    }

    boolean getOfficial(){
        return this.official;
    } 

    private void setTitle(Document doc){
        String title = doc.select(".title").text().replace("/", "-");
        
        // if there is no valid title on page
        if (title.length() == 0){
            title = doc.select("title").text().replace("/", "-");
        }
        this.title = title;
    }

    private void getLinks(Document doc){
        Pattern pattern = Pattern.compile("(jump\\(')(.*)('\\))");
        Elements links = doc.select("a[onclick*=jump]");
        
        // add all valid anchor tags to ArrayList
        for (Element link: links) {
            Matcher matcher = pattern.matcher(link.attr("onclick"));
            while (matcher.find()) {
                String finalizedUrl = null;
                // not to use baseUrl as reference if domain is different
                if(matcher.group(2).contains("http")){
                    finalizedUrl = matcher.group(2);
                } else {
                    finalizedUrl = this.baseUrl + matcher.group(2);
                }

                if (finalizedUrl.contains("api-danmemo") || finalizedUrl.contains("cdn-danmemo")){
                    this.linkedSites.add(new Site(finalizedUrl, notifType, this.options, this.ui));

                    Document jsLink = null;
                    String linkTitle = null;
                    try {
                        jsLink = Jsoup.connect(finalizedUrl).ignoreHttpErrors(true).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    linkTitle = jsLink.select(".title").text().replace("/", "-");
                    if(linkTitle.length() == 0) {
                        linkTitle = jsLink.select("title").text().replace("/", "-");
                    }

                    // replace jump links with static refs to files in dir
                    link.attr("onclick", "");
                    link.attr("href", "./"+linkTitle+".html");
                }
            }
        }
    }

    public void crawl() {
        Document doc = null;
        try {
            doc = Jsoup.connect(this.url).ignoreHttpErrors(true).get();
        } catch (Exception e) { 
            e.printStackTrace();
            return;
        }

        this.setTitle(doc);
        this.ui.updateText(this.title);
        this.getLinks(doc);
        this.writeToFile(doc);

        for(Site site: this.linkedSites) {
            if (site.getOfficial()){
                Thread t = new Thread(() -> site.crawl());
                t.start();
            }
        }
    }
    

    void writeToFile(Document doc) {
        String directory = "notifications/" + this.lang + "/" + this.notifType + "/";

        // grabbing all css files
        Element css = doc.select("link[href$=.css]").first();
        Pattern cssPattern = Pattern.compile("(.*\\/)(.*\\/css\\/style\\.css)");
        Matcher cssMatcher = cssPattern.matcher(css.attr("abs:href"));
        String cssName = null;

        while (cssMatcher.find()){
            cssName = cssMatcher.group(2).replace("/", "-");
        }


        if (!new File(directory + "css/" + cssName).exists()){         
            try {
                InputStream in = new URL(css.attr("abs:href")).openStream();
                Files.copy(in, Paths.get(directory + "css/" + cssName));
            } catch (Exception e) {
            }
        };
        css.attr("href", "./css/" + cssName);


        // grabbing all js files
        Elements js = doc.select("script");
        Pattern jsPattern = Pattern.compile("(.*\\/)(.*\\/.*\\.js).*"); 

        for (Element j: js){
            Matcher jsMatcher = jsPattern.matcher(j.attr("abs:src"));
            String jsName = null;

            while(jsMatcher.find()){
                jsName = jsMatcher.group(2).replace("/", "-");
            }

            // no src for custom js scripts
            if(jsName == null){
                continue;
            } else {
                if (!new File(directory + "js/" + jsName).exists()){         
                    try {
                        InputStream in = new URL(j.attr("abs:src")).openStream();
                        Files.copy(in, Paths.get(directory + "js/" + jsName));
                    } catch (Exception e) {
                    }
                }
                j.attr("src", "./js/" + jsName);
            }
        }

        // extract images 
        Elements images = doc.select("img");
        Pattern urlPattern = Pattern.compile("(.*\\/)(.*)");
        for (Element image: images) {
            Matcher urlMatcher = urlPattern.matcher(image.attr("abs:src"));

            String imgName = null;
            while(urlMatcher.find()){
               imgName = urlMatcher.group(2); 
            }
            String imgPath = "notifications/" + this.lang + "/" + this.notifType + "/img/" + imgName;
            if (!new File(imgPath).exists()) {
                try {
                    InputStream in = new URL(image.attr("abs:src")).openStream();
                    Files.copy(in, 
                            Paths.get(imgPath)
                    );
                } catch (Exception e){
                }
            }

            image.attr("src", "./img/" + imgName);
        }

        try {
            BufferedWriter writer = new BufferedWriter(
                new FileWriter(directory+this.title+".html")
            );
            writer.write(doc.outerHtml());
            this.options.addSite(this.url);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Site> getLinkedSites() {
        return linkedSites;
    };

    String getUrl() {
        return url;
    }
}
