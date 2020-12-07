enum Language {
    EN,
    JP
}

public class CrawlerOptions {
    Language language = Language.EN;

    public CrawlerOptions() {}

    void toggleLanguage() {
        if(this.language == Language.EN) {
            this.language = Language.JP;
        } else {
            this.language = Language.EN;
        }
    }

    String getLanguage() {
        if(this.language == Language.EN) {
            return "en";
        } else {
            return "jp";
        }
    }
}
