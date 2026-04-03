package hr.tvz.vibecheck.security;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VibeCheckUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private List<String> roles;

    @Override
    public @Nonnull Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Deprecated
    @Override
    public @Nonnull String getUsername() {
        return username;
    }

    public @Nonnull String getEmail() {
        return username;
    }
}
