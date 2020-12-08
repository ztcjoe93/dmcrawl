public class CrawlerOptions {
    String language = "en";

    public CrawlerOptions() {}

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
