package hr.tvz.vibecheck.service.user;

import hr.tvz.vibecheck.cloudinary.CloudinaryService;
import hr.tvz.vibecheck.dto.request.*;
import hr.tvz.vibecheck.dto.response.ImageUploadResponse;
import hr.tvz.vibecheck.dto.response.UserEditResponse;
import hr.tvz.vibecheck.dto.response.UserResponse;
import hr.tvz.vibecheck.dto.dtoMapper.UserMapper;
import hr.tvz.vibecheck.entity.EmailChange;
import hr.tvz.vibecheck.entity.User;
import hr.tvz.vibecheck.enums.ProfileVisibility;
import hr.tvz.vibecheck.exception.DuplicateUserException;
import hr.tvz.vibecheck.exception.UserNotFoundException;
import hr.tvz.vibecheck.repository.email.EmailChangeRepository;
import hr.tvz.vibecheck.repository.user.UserRepository;
import hr.tvz.vibecheck.service.email.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final UserMapper userMapper;
    private final EmailChangeRepository emailRepository;

    private static final int CODE_EXPIRATION_MINUTES = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final MailService mailService;

    public UserResponse findOneById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public void createUser(CreateUserRequest request) {
        if (checkDuplicate(request.email()))
            throw new DuplicateUserException("User with " + request.email() +  " email already exists");

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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        userMapper.updateUserFromRequest(request, user); //update request mapper

        userRepository.save(user);

        return userMapper.toUserEditResponse(user); //response DTO mapper
    }


    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.deleteById(userId);
    }


    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);

    }


    @Transactional
    public void sendEmailVerificationCode(Long userId, NewEmailRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        String newMail = request.newEmail().trim().toLowerCase();

        if (user.getEmail().trim().toLowerCase().equals(newMail)) {
            throw new IllegalArgumentException("New email cannot match old email");
        }

        if (userRepository.existsByEmail(request.newEmail())) {
            throw new IllegalArgumentException("Email: " + request.newEmail() + " is already in use");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(CODE_EXPIRATION_MINUTES);

        EmailChange codeReq;
        Optional<EmailChange> reqOpt = emailRepository.findByUser(user);

        String verificationCode = generateVerificationCode();

        if (reqOpt.isPresent()) {
            codeReq = reqOpt.get();
            codeReq.setNewEmail(newMail);
            codeReq.setVerificationCode(verificationCode);
            codeReq.setExpiration(expirationTime);
        }
        else {
            codeReq = EmailChange.builder()
                    .user(user)
                    .newEmail(newMail)
                    .verificationCode(verificationCode)
                    .expiration(expirationTime)
                    .build();
        }

        mailService.sendEmailChangeVerificationCodeEmail(newMail, verificationCode);
        emailRepository.save(codeReq);

    }


    private String generateVerificationCode() {
        int code = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(code);
    }

    @Transactional
    public void confirmEmailEdit(Long userId, VerificationCodeRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        EmailChange codeReq = emailRepository.findByUser(user).orElseThrow(()
                -> new IllegalArgumentException("No pending email change request found for user: " + user.getUsername()));


        if (!codeReq.getVerificationCode().equals(request.code())) {
            throw new IllegalArgumentException("Invalid Verification code, please try again");
        }

        if (codeReq.getExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code has expired, please make a new verification code request");
        }

        user.setEmail(codeReq.getNewEmail().trim().toLowerCase());
        emailRepository.delete(codeReq);

    }


}
