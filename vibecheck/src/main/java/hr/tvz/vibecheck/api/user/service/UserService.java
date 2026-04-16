package hr.tvz.vibecheck.api.user.service;

import hr.tvz.vibecheck.api.user.dto.UserOutputDto;
import hr.tvz.vibecheck.api.user.mapper.UserMapper;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserOutputDto findOneById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return userMapper.toUserResponse(user);
    }
}
