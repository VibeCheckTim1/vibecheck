package hr.tvz.vibecheck.service.user;

import hr.tvz.vibecheck.cloudinary.CloudinaryService;
import hr.tvz.vibecheck.dto.request.CreateUserRequest;
import hr.tvz.vibecheck.dto.response.ImageUploadResponse;
import hr.tvz.vibecheck.entity.User;
import hr.tvz.vibecheck.entity.enum_.ProfileVisibility;
import hr.tvz.vibecheck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public User createUser(CreateUserRequest request) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .bio(request.bio())
                .visibility(request.visibility() != null ? request.visibility() : ProfileVisibility.PUBLIC)
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .avatarUrl("https://res.cloudinary.com/dqqjdinyg/image/upload/v1774976056/default_qpersr.svg") //default avatar
                .avatarPublicId(null)
                .tstamp(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public void uploadAvatar(Long userId, MultipartFile avatar) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (avatar == null || avatar.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (avatar.getContentType() == null || !avatar.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("File is not an image");
        }

        if (user.getAvatarPublicId() != null) {
            cloudinaryService.deleteImage(user.getAvatarPublicId());
        }

        ImageUploadResponse response = cloudinaryService.uploadUserAvatar(avatar, user.getIdUser());
        user.setAvatarUrl(response.imageUrl());
        user.setAvatarPublicId(response.publicId());

        userRepository.save(user);
    }


    public void editUser(Long userId, MultipartFile avatar) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
    }



}
