package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.component.mapper.UserMapper;
import com.hoquangnam45.pharmacy.config.VerificationConfig;
import com.hoquangnam45.pharmacy.constant.VerificationType;
import com.hoquangnam45.pharmacy.constant.PaymentMethod;
import com.hoquangnam45.pharmacy.entity.VerificationCode;
import com.hoquangnam45.pharmacy.entity.PaymentInfo;
import com.hoquangnam45.pharmacy.entity.PhoneNumber;
import com.hoquangnam45.pharmacy.entity.User;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.CustomAuthenticationPrincipal;
import com.hoquangnam45.pharmacy.pojo.RegisterRequest;
import com.hoquangnam45.pharmacy.pojo.SendEmailTemplateRequest;
import com.hoquangnam45.pharmacy.pojo.UserProfile;
import com.hoquangnam45.pharmacy.repo.VerificationCodeRepo;
import com.hoquangnam45.pharmacy.repo.PhoneRepo;
import com.hoquangnam45.pharmacy.repo.UserRepo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PhoneRepo phoneRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final VerificationCodeRepo verificationCodeRepo;
    private final VerificationConfig verificationConfig;
    private final IMailService mailService;
    private final String apiUrl;

    public UserService(
            UserMapper userMapper,
            UserRepo userRepo,
            PhoneRepo phoneRepo,
            PasswordEncoder passwordEncoder,
            VerificationCodeRepo verificationCodeRepo,
            VerificationConfig verificationConfig,
            IMailService mailService,
            @Value("${pharma.apiUrl}") String apiUrl) {
        this.userRepo = userRepo;
        this.phoneRepo = phoneRepo;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.verificationCodeRepo = verificationCodeRepo;
        this.verificationConfig = verificationConfig;
        this.mailService = mailService;
        this.apiUrl = apiUrl;
    }

    public User createUser(RegisterRequest registerRequest) throws Exception {
        String encryptedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = userMapper.createNewUser(registerRequest);
        user.setPassword(encryptedPassword);
        user.setEmailConfirmed(false);
        user.setPhoneNumberConfirmed(false);
        user.setPaymentInfos(Set.of(PaymentInfo.builder()
                .method(PaymentMethod.COD)
                .build()));
        user = userRepo.save(user);

        if (registerRequest.getEmail() != null) {
            sendConfirmationEmail(registerRequest.getEmail(), user);
        }

        return user;
    }

    public void sendConfirmationEmail(String email, User user) throws Exception {
        String verificationCode = RandomStringUtils.randomAlphabetic(verificationConfig.getEmail().getLength());

        mailService.sendEmail(SendEmailTemplateRequest.builder()
                .viewName("email/confirm_email")
                .parameters(Map.of("verificationCode", verificationCode, "verificationLink", MessageFormat.format("{0}/verification?code={1}", apiUrl,
                                Base64.getEncoder().encodeToString(MessageFormat.format("verificationCode={0}&type={1}", verificationCode, VerificationType.EMAIL).getBytes()))))
                .toAddress(email)
                .build());

        OffsetDateTime sentAt = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expiredAt = sentAt.plusMinutes(verificationConfig.getEmail().getExpireTimeInMin());

        verificationCodeRepo.save(VerificationCode.builder()
                .type(VerificationType.EMAIL)
                .expiredAt(expiredAt)
                .lastSentAt(sentAt)
                .userId(user.getId())
                .verificationCode(verificationCode)
                .build());
    }

    public User getUser(CustomAuthenticationPrincipal principal) {
        return switch (principal.getType()) {
            case EMAIL -> userRepo.findUserByEmail(principal.getPrincipal());
            case USERNAME -> userRepo.findUserByUsername(principal.getPrincipal());
            case PHONE_NUMBER -> {
                String[] phoneNumberParts = Optional.of(principal.getPrincipal())
                        .map(p -> p.split("\\|"))
                        .filter(tokens -> tokens.length == 2)
                        .orElseThrow(() -> ApiError.badRequest("Invalid phone number"));
                PhoneNumber phoneNumber = phoneRepo.findPhoneNumberByCountryCodeAndPhoneNumber(phoneNumberParts[0], phoneNumberParts[1]);
                if (phoneNumber == null) {
                    yield null;
                } else {
                    yield phoneNumber.getUser();
                }
            }
        };
    }

    public UserProfile getUserProfile(UUID userId) {
        return userRepo.findById(userId)
                .map(userMapper::createUserProfile)
                .orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    public User getUserByUsername(String username) {
        return userRepo.findUserByUsername(username);
    }

    public User getUserById(UUID userId) {
        return userRepo.findById(userId).orElse(null);
    }
}
