package hr.tvz.vibecheck.api.security.service;

import hr.tvz.vibecheck.api.security.dto.RegisterRequestDto;
import hr.tvz.vibecheck.api.security.dto.LoginRequestDto;
import hr.tvz.vibecheck.api.security.dto.TokenOutputDto;
import hr.tvz.vibecheck.api.user.dto.UserOutputDto;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.mapper.UserMapper;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.exception.UserNotFoundException;
import hr.tvz.vibecheck.projections.UserStateResponse;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import hr.tvz.vibecheck.security.JwtService;
import hr.tvz.vibecheck.security.VibeCheckUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AuthenticationManager authenticationManager;
    private final VibeCheckUserDetailsService vibeCheckUserDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserOutputDto register(RegisterRequestDto request) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .bio(request.bio())
                .isPrivate(true)
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .avatarUrl("https://res.cloudinary.com/dqqjdinyg/image/upload/v1774976056/default_qpersr.svg") //default avatar
                .avatarPublicId(null)
                .tstamp(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public TokenOutputDto loginWithCredentials(LoginRequestDto loginRequestDto) {
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        if (!(auth.getPrincipal() instanceof VibeCheckUserDetails user)) {
            throw new AuthenticationCredentialsNotFoundException("Invalid credentials!");
        }

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return new TokenOutputDto(accessToken, refreshToken);
    }

    public TokenOutputDto loginWithUsername(String username) {
        var loadedUser = vibeCheckUserDetailsService.loadUserByUsername(username);

        var auth = new UsernamePasswordAuthenticationToken(loadedUser, null, loadedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        if (!(auth.getPrincipal() instanceof VibeCheckUserDetails user)) {
            throw new AuthenticationCredentialsNotFoundException("Invalid username!");
        }

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return new TokenOutputDto(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public UserStateResponse getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof VibeCheckUserDetails user)) {
            throw new UserNotFoundException();
        }

        return userRepository.findUserStateByUsername(user.getUsername()).orElse(null);
    }
}
