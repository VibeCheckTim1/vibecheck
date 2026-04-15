package hr.tvz.vibecheck.security;

import hr.tvz.vibecheck.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class VibeCheckUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public @NullMarked UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> VibeCheckUserDetails.builder()
                        .id(user.getIdUser())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .roles(List.of()) //TODO: kada se dodaju role
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
