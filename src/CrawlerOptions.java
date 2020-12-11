import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

public class CrawlerOptions {
    String language = "en";
    HashSet<String> urlSet = new HashSet<String>();

    public CrawlerOptions() {
       File urlList = new File("sites.txt"); 

       if (urlList.exists()){
           try (BufferedReader reader = Files.newBufferedReader(Paths.get("sites.txt"))){
               String line;
               while ((line = reader.readLine()) != null) {
                   urlSet.add(line);
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

    void toggleLanguage() {
        if(this.language == "en"){
            this.language = "jp";
        } else {
            this.language = "en";
        }
    }

    String getLanguage() {
        return this.language;
    }
}
