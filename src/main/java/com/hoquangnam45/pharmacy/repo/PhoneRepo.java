package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PhoneRepo extends JpaRepository<PhoneNumber, UUID> {
    PhoneNumber findPhoneNumberByCountryCodeAndPhoneNumber(String countryCode, String phoneNumber);
}
