package hr.tvz.vibecheck.api.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchResult {
    private String id;
    private String type;
    private String titleText;
    private String subtitleText;
    private String imageUrl;

}