package hr.tvz.vibecheck.service;

import hr.tvz.vibecheck.projections.UserStateResponse;
import hr.tvz.vibecheck.repository.UserRepository;
import hr.tvz.vibecheck.security.VibeCheckUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StateService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserStateResponse getUserState() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof VibeCheckUserDetails user)) {
            return null;
        }

        return userRepository.findUserStateByUsername(user.getUsername()).orElse(null);

    }
}
