package hr.tvz.vibecheck.api.search.service;

import hr.tvz.vibecheck.api.search.dto.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final List<SearchProvider> searchProviders;

    public List<SearchResult> search(String keyword) {
        var trimmedKeyword = keyword.trim();
        if (trimmedKeyword.isEmpty()) {
            return List.of();
        }

        return searchProviders.stream()
                .flatMap(provider -> provider.search(trimmedKeyword).stream())
                .toList();
    }
}
