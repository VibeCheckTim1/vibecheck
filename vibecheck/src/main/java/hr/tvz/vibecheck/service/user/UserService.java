package hr.tvz.vibecheck.service.user;

import hr.tvz.vibecheck.cloudinary.CloudinaryService;
import hr.tvz.vibecheck.dto.request.ChangePasswordRequest;
import hr.tvz.vibecheck.dto.request.CreateUserRequest;
import hr.tvz.vibecheck.dto.request.EditUserRequest;
import hr.tvz.vibecheck.dto.response.ImageUploadResponse;
import hr.tvz.vibecheck.dto.response.UserEditResponse;
import hr.tvz.vibecheck.dto.response.UserResponse;
import hr.tvz.vibecheck.dtoMapper.UserMapper;
import hr.tvz.vibecheck.entity.User;
import hr.tvz.vibecheck.enums.ProfileVisibility;
import hr.tvz.vibecheck.projections.UserStateResponse;
import hr.tvz.vibecheck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final UserMapper userMapper;

    public UserResponse findOneById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public void createUser(CreateUserRequest request) {
        if (checkDuplicate(request.email()))
            throw new RuntimeException("User with " + request.email() +  " email already exists");

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .bio(request.bio())
                .visibility(ProfileVisibility.PRIVATE)
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .avatarUrl("https://res.cloudinary.com/dqqjdinyg/image/upload/v1774976056/default_qpersr.svg") //default avatar
                .avatarPublicId(null)
                .tstamp(LocalDateTime.now())
                .build();

        userRepository.save(user);

        /*return userMapper.toUserResponse(user);*/
    }

    private boolean checkDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    private static void avatarValidation(MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (avatar.getContentType() == null || !avatar.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("File is not an image");
        }
    }

    @Transactional
    public void uploadAvatar(Long userId, MultipartFile avatar) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        avatarValidation(avatar);

        /*if (user.getAvatarPublicId() != null) {
            cloudinaryService.deleteImage(user.getAvatarPublicId());
        }*/

        ImageUploadResponse response = cloudinaryService.uploadUserAvatar(avatar, user.getIdUser());
        user.setAvatarUrl(response.imageUrl());
        user.setAvatarPublicId(response.publicId());

        userRepository.save(user);
    }

    @Transactional
    public UserEditResponse editUser(Long userId, EditUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUserFromRequest(request, user); //update request mapper

        userRepository.save(user);

        return userMapper.toUserEditResponse(user); //response DTO mapper
    }


    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.deleteById(userId);
    }


    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);

    }



}
