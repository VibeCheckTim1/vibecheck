package hr.tvz.vibecheck.api.search.service;

import hr.tvz.vibecheck.api.search.dto.SearchResult;

import java.util.List;

public interface SearchProvider {
    List<SearchResult> search(String keyword);
}
