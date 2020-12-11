import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

public class CrawlerOptions {
    String language = "en";
    final static String urlPath = "sites.txt";
    HashSet<String> urlSet = new HashSet<String>();

    public CrawlerOptions() {
       File urlList = new File(urlPath);

       if (urlList.exists()){
           try (BufferedReader reader = Files.newBufferedReader(Paths.get(urlPath))){
               String line;
               while ((line = reader.readLine()) != null) {
                   this.urlSet.add(line);
               }
           } catch (Exception e){
               e.printStackTrace();
           }
       } else {
           try {
               urlList.createNewFile();
           } catch (Exception e){
               e.printStackTrace();
           }
       }
    }

    HashSet<String> getSites() {
        return this.urlSet;
    }

    void toggleLanguage() {
        if(this.language == "en"){
            this.language = "jp";
        } else {
            this.language = "en";
        }
    }

    void updateFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(urlPath));
            for(String url: urlSet){
                writer.append(url+"\n");
            }
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void addSite(String url) {
        this.urlSet.add(url);
    }

    String getLanguage() {
        return this.language;
    }

}
