package hr.tvz.vibecheck.security.jwt;

import hr.tvz.vibecheck.api.security.service.AccessTokenService;
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
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final AccessTokenService accessTokenService;

    private final UserDetailsService userDetailsService;

    private static final List<String> PATHS_TO_SKIP = List.of(
            "/security/refresh-token",
            "/security/login",
            "/security/register",
            "/swagger-ui",
            "/swagger-ui.html",
            "/v3/api-docs"
    );

    public JwtFilter(AccessTokenService accessTokenService, UserDetailsService userDetailsService) {
        this.accessTokenService = accessTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {

        String path = request.getServletPath();
        if (PATHS_TO_SKIP.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        var token = accessTokenService.extractTokenFromCookie(request);

        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            if (accessTokenService.isValid(token)) {
                var username = accessTokenService.extractUsername(token);
                var user = userDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        catch (Exception e) {
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
