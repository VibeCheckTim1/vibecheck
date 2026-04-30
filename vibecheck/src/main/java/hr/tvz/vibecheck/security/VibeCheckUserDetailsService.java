package hr.tvz.vibecheck.security;

import hr.tvz.vibecheck.api.security.entity.Role;
import hr.tvz.vibecheck.api.security.repository.RoleRepository;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class VibeCheckUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public @NullMarked UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> VibeCheckUserDetails.builder()
                        .id(user.getIdUser())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .roles(roleRepository.findAllByUserId(user.getIdUser()).stream()
                                .map(Role::getName)
                                .toList())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
