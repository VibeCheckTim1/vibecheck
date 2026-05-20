package hr.tvz.vibecheck.api.search.service;

import hr.tvz.vibecheck.api.search.dto.SearchResult;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private final UserRepository userRepository;

    public SearchService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<SearchResult> search(String keyword) {
        List<SearchResult> results = new ArrayList<>();

        if (!keyword.isEmpty()) {
            // korisnici
            List<User> users = userRepository.searchUsers(keyword);
            for (User user : users) {
                results.add(mapUserToDto(user));
            }

            // kasnije dodati izvođače, pjesme, ...
        }

        return results;
    }

    private SearchResult mapUserToDto(User user) {
        return new SearchResult(
                String.valueOf(user.getIdUser()),
                "user",
                user.getUsername(),
                user.getFirstName() + " " + user.getLastName()
        );
    }
}
