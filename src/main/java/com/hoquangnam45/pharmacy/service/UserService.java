package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.component.mapper.UserMapper;
import com.hoquangnam45.pharmacy.constant.PaymentMethod;
import com.hoquangnam45.pharmacy.entity.PaymentInfo;
import com.hoquangnam45.pharmacy.entity.PhoneNumber;
import com.hoquangnam45.pharmacy.entity.User;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.CustomAuthenticationPrincipal;
import com.hoquangnam45.pharmacy.pojo.RegisterRequest;
import com.hoquangnam45.pharmacy.pojo.UserProfile;
import com.hoquangnam45.pharmacy.repo.CartRepo;
import com.hoquangnam45.pharmacy.repo.PhoneRepo;
import com.hoquangnam45.pharmacy.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PhoneRepo phoneRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CartRepo cartRepo;

    public UserService(UserMapper userMapper, UserRepo userRepo, PhoneRepo phoneRepo, PasswordEncoder passwordEncoder, CartRepo cartRepo) {
        this.userRepo = userRepo;
        this.phoneRepo = phoneRepo;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.cartRepo = cartRepo;
    }

    public User createUser(RegisterRequest registerRequest) {
        String encryptedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = userMapper.createNewUser(registerRequest);
        user.setPassword(encryptedPassword);
        user.setPaymentInfos(Set.of(PaymentInfo.builder()
                .method(PaymentMethod.COD)
                .build()));
        user = userRepo.save(user);
        return user;
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

    public User getUserByEmail(RegisterRequest registerRequest) {
        return Optional.ofNullable(registerRequest.getEmail())
                .map(userRepo::findUserByEmail)
                .orElse(null);
    }

    public User getUserByUsername(RegisterRequest registerRequest) {
        return Optional.ofNullable(registerRequest.getUsername())
                .map(userRepo::findUserByUsername)
                .orElse(null);
    }
}
