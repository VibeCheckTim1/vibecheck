package hr.tvz.vibecheck.controller;

import hr.tvz.vibecheck.dto.response.SearchResult;
import hr.tvz.vibecheck.service.search.SearchService;
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