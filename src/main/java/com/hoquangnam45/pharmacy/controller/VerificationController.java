package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.constant.VerificationType;
import com.hoquangnam45.pharmacy.entity.User;
import com.hoquangnam45.pharmacy.entity.VerificationCode;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.GenericResponse;
import com.hoquangnam45.pharmacy.repo.VerificationCodeRepo;
import com.hoquangnam45.pharmacy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("verification")
@Transactional
public class VerificationController {
    private final VerificationCodeRepo verificationCodeRepo;
    private final UserService userService;

    public VerificationController(VerificationCodeRepo verificationCodeRepo, UserService userService) {
        this.verificationCodeRepo = verificationCodeRepo;
        this.userService = userService;
    }

    // Because it need to handle request come from email which is encoded in base64 to prevent url parameters escaping
    // So that why this does not use normal way to get parameters
    @GetMapping
    public ResponseEntity<GenericResponse> verify(@RequestParam("code") String code, HttpServletRequest request) {
        String path = request.getServletPath();
        Map<String, String> parameters = Stream.of(new String(Base64.getDecoder().decode(code)).split("&"))
                .map(fragment -> fragment.split("="))
                .filter(param -> param.length == 2)
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1]));
        String verificationCode = Optional.ofNullable(parameters.get("verificationCode"))
                .orElseThrow(() -> ApiError.badRequest("Missing verification code"));
        VerificationType type = Optional.ofNullable(parameters.get("type"))
                .map(VerificationType::valueOf)
                .orElseThrow(() -> ApiError.badRequest("Missing verification type"));
        VerificationCode verification = Optional.ofNullable(verificationCodeRepo.findByTypeAndVerificationCode(type, verificationCode))
                .orElseThrow(() -> ApiError.notFound("Not found verification code " + verificationCode + " for type " + type));
        boolean expired = verification.getExpiredAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC));
        if (expired) {
            throw ApiError.badRequest("Verification code has been expired");
        }
        switch (type) {
            case EMAIL -> verification.getUser().setEmailConfirmed(true);
            case PHONE -> verification.getUser().setPhoneNumberConfirmed(true);
        }
        verificationCodeRepo.delete(verification);
        return ResponseEntity.ok(new GenericResponse(200, path, "Verify successfully"));
    }

    @PostMapping("resend")
    public ResponseEntity<GenericResponse> resendVerificationCode(
            @RequestParam("userId") UUID userId,
            @RequestParam("type") VerificationType type,
            HttpServletRequest request) throws Exception {
        String path = request.getServletPath();
        User user = Optional.ofNullable(userService.getUserById(userId))
                .orElseThrow(() -> ApiError.notFound("User not found"));
        switch (type) {
            case EMAIL -> {
                if (user.getEmail() == null) {
                    throw ApiError.badRequest("User do not add email yet");
                }
                if (user.isEmailConfirmed()) {
                    return ResponseEntity.ok(new GenericResponse(200, path, "Email confirmed"));
                }
            }
            case PHONE -> {
                if (user.getPhoneNumber() == null) {
                    throw ApiError.badRequest("User do not add phone number yet");
                }
                if (user.isPhoneNumberConfirmed()) {
                    return ResponseEntity.ok(new GenericResponse(200, path, "Phone number confirmed"));
                }
            }
        }

        Optional.ofNullable(verificationCodeRepo.findByTypeAndUserId(type, userId))
                .ifPresent(verificationCode -> {
                    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
                    Duration timeBetweenSent = Duration.between(verificationCode.getLastSentAt(), now);
                    boolean expired = verificationCode.getExpiredAt().isBefore(now);
                    if (!expired && timeBetweenSent.compareTo(Duration.ofMinutes(1)) <= 0) {
                        throw ApiError.badRequest("Too many request in short period of time");
                    }
                    verificationCodeRepo.delete(verificationCode);
                });
        switch (type) {
            case EMAIL -> userService.sendConfirmationEmail(user.getEmail(), user);
            case PHONE -> throw ApiError.internalServerError("Not supported yet");
        }
        return ResponseEntity.ok(new GenericResponse(200, path, "Resend successfully"));
    }

}
