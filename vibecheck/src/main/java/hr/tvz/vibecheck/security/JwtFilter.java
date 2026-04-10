package hr.tvz.vibecheck.security;

import hr.tvz.vibecheck.enums.TokenType;
import hr.tvz.vibecheck.service.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {

        var token = jwtService.getTokenFromCookie(request, TokenType.ACCESS);

        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            if (jwtService.isValid(token, TokenType.ACCESS)) {
                var username = jwtService.extractUsername(token);

                var user = userDetailsService.loadUserByUsername(username);

                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);

            }
        }
        catch (Exception e) {
            SecurityContextHolder.clearContext();
            //TODO: klijentu vratiti 401 statusni kod
        }

        chain.doFilter(request, response);
    }
}
