package hr.tvz.vibecheck.api.search.controller;

import hr.tvz.vibecheck.api.search.dto.SearchResult;
import hr.tvz.vibecheck.api.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<List<SearchResult>> search(@RequestParam("q") String keyword) {
        return ResponseEntity.ok(searchService.search(keyword));
    }
}