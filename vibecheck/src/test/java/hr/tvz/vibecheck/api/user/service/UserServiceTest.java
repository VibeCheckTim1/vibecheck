package hr.tvz.vibecheck.api.user.service;

import hr.tvz.vibecheck.api.user.dto.UserOutputDto;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.mapper.UserMapper;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.exception.custom.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserOutputDto sampleDto() {
        return new UserOutputDto(1L, "johndoe", "John", "Doe", "Bio text", "john@email.com", "avatar.png", false);
    }

    @Test
    void findOneById_shouldReturnUser_whenUserExists() {
        User user = new User();
        UserOutputDto dto = sampleDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(dto);

        UserOutputDto result = userService.findOneById(1L);

        assertThat(result).isEqualTo(dto);
        verify(userRepository).findById(1L);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void findOneById_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findOneById(99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(99L);
        verifyNoInteractions(userMapper);
    }
}