package hr.tvz.vibecheck.api.user.dto;

public class SearchResult {
    private Long id;
    private String type;
    private String titleText;
    private String subtitleText;

    public SearchResult(Long id, String type, String titleText, String subtitleText) {
        this.id = id;
        this.type = type;
        this.titleText = titleText;
        this.subtitleText = subtitleText;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitleText() {
        return titleText;
    }

    public String getSubtitleText() {
        return subtitleText;
    }
}