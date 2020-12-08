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
    private String lang;

    Site(String url) {
        this.url = url;

        Pattern pattern = Pattern.compile("(.*\\.net)(.*)");
        Matcher matcher = pattern.matcher(url);

        while (matcher.find()) {
            this.baseUrl = matcher.group(1);
            // check for non-wrightflyer urls
            if (baseUrl.contains("wrightflyer")) {
                if (baseUrl.contains("us")) {
                    this.lang = "en"; } else {
                    this.lang = "jp";
                }
            } else {
                Pattern cdnPattern = Pattern.compile("(.*\\.net\\/us)(.*)");
                Matcher cdnMatcher = cdnPattern.matcher(url);

                if (cdnMatcher.matches()) {
                    this.lang = "en";
                } else {
                    this.lang = "jp";
                }
            }
        } 
    }


    void crawl() {
        Document doc = null;
        try {
            doc = Jsoup.connect(this.url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set title and id for current news
        this.title = doc.select(".title").text();

        // no valid .title tag
        if(this.title.length() == 0) {
            this.title = doc.select("title").text();
        }

        //System.out.println(this.title + "\n" + this.lang);
        this.writeToFile(doc);
        Pattern pattern = Pattern.compile("(jump\\(')(.*)('\\))");
        
        Elements links = doc.select("a[onclick*=jump]");
        // add all valid anchor tags to ArrayList
        for (Element link: links) {
            Matcher matcher = pattern.matcher(link.attr("onclick"));
            while (matcher.find()) {
                // not to use baseUrl as reference if domain is different
                if(matcher.group(2).contains("https")){
                    this.linkedSites.add(new Site(matcher.group(2))); 
                } else {
                    this.linkedSites.add(new Site(this.baseUrl + matcher.group(2)));
                }
            }
        }


        for(Site site: this.linkedSites) {
            site.crawl();
        }
    }
    
    void writeToFile(Document doc) {
        System.out.println(this.url);
        String directory = "notifications/" + this.lang + "/";

        Element css = doc.select("link[href$=.css]").first();
        Elements js = doc.select("script");

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
                e.printStackTrace();
            }
        };
        css.attr("href", "./css/" + cssName);

        Pattern jsPattern = Pattern.compile("(.*/)(.*\\/.*\\.js).*"); 
        for (Element j: js){
            System.out.println(j.attr("src"));
            Matcher jsMatcher = jsPattern.matcher(j.attr("src"));
            String jsName = null;

            while(jsMatcher.find()){
                jsName = jsMatcher.group(2).replace("/", "-");
            }

            if(jsName == null){
                continue;
            } else {
                if (!new File(directory + "js/" + jsName).exists()){         
                    try {
                        InputStream in = new URL(j.attr("src")).openStream();
                        Files.copy(in, Paths.get(directory + "js/" + jsName));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                j.attr("src", "./js/" + jsName);
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(
                new FileWriter(directory + this.title + ".html")
            );
            Files.createDirectories(Paths.get(directory + this.title));
            writer.write(doc.outerHtml());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        // extract images for posts on cdn-danmemo.akamaized
        Elements images = doc.select("img");
        Pattern urlPattern = Pattern.compile("(.*\\/)(.*)");
        for (Element image: images) {
            if(!image.attr("src").contains("cdn-danmemo.akamaized")) {
                try {
                    Matcher urlMatcher = urlPattern.matcher(this.url);
                    while (urlMatcher.find()) {
                        if (!new File("notifications/" + this.lang + "/" + image.attr("src")).exists()) {
                            InputStream in = new URL(urlMatcher.group(1) + image.attr("src")).openStream();
                            Files.copy(in, 
                                    Paths.get("notifications/" + this.lang + "/" +image.attr("src"))
                            );
                        }
                    }
                } catch (Exception e) {
                } 
            }
        }
        */
    }

    ArrayList<Site> getLinkedSites() {
        return linkedSites;
    };

    String getUrl() {
        return url;
    }
}
