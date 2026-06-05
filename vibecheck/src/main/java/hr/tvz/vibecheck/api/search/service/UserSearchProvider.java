package hr.tvz.vibecheck.api.search.service;

import hr.tvz.vibecheck.api.search.dto.SearchResult;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class UserSearchProvider implements SearchProvider {
    private final UserRepository userRepository;

    @Override
    public List<SearchResult> search(String keyword) {
        return userRepository.searchUsers(keyword).stream()
                .map(this::toSearchResult)
                .toList();
    }

    private SearchResult toSearchResult(User user) {
        return new SearchResult(
                String.valueOf(user.getIdUser()),
                "user",
                user.getUsername(),
                user.getFirstName().concat(" ").concat(user.getLastName()),
                user.getAvatarUrl()
        );
    }
}
