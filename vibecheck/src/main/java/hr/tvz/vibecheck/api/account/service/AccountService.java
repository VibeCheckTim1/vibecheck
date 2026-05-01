package hr.tvz.vibecheck.api.account.service;

import hr.tvz.vibecheck.api.account.dto.*;
import hr.tvz.vibecheck.api.account.entity.EmailChange;
import hr.tvz.vibecheck.exception.custom.InvalidFileException;
import hr.tvz.vibecheck.exception.custom.InvalidPasswordException;
import hr.tvz.vibecheck.exception.custom.UserNotFoundException;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.mapper.UserMapper;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.api.account.repository.email_change.EmailChangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final MailService mailService;
    private final EmailChangeRepository emailRepository;
    private final UserMapper userMapper;

    private static final int CODE_EXPIRATION_MINUTES = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public void uploadAvatar(Long userId, MultipartFile avatar) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (avatar == null || avatar.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        if (avatar.getContentType() == null || !avatar.getContentType().startsWith("image/")) {
            throw new InvalidFileException("File must be an image");
        }
        /*if (user.getAvatarPublicId() != null) {
            cloudinaryService.deleteImage(user.getAvatarPublicId());
        }*/

        UploadImageDetailsDto response = cloudinaryService.uploadUserAvatar(avatar, user.getIdUser());
        user.setAvatarUrl(response.imageUrl());
        user.setAvatarPublicId(response.publicId());

        userRepository.save(user);
    }

    @Transactional
    public void updateAccount(Long userId, UpdateAccountRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        userMapper.updateUserFromRequest(request, user);

        //userRepository.save(user);
    }


    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        userRepository.deleteById(user.getIdUser());
    }


    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }


    @Transactional
    public void sendEmailVerificationCode(Long userId, ChangeEmailRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

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
    public void confirmEmailEdit(Long userId, VerificationCodeRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

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
