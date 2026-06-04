package hr.tvz.vibecheck.security;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
public class VibeCheckUserDetails implements UserDetails, OAuth2User {

    private Long id;
    @NonNull
    private String username;
    private String email;
    private String password;
    private List<String> roles;
    private Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes == null ? Map.of() : attributes;
    }

    @Override
    public @Nonnull String getName() {
        return getUsername();
    }

    @Override
    public @Nonnull Collection<? extends GrantedAuthority> getAuthorities() {
        return (roles == null ? List.<String>of() : roles).stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public @Nonnull String getUsername() {
        return username;
    }

}
