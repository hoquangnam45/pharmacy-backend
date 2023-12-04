package com.hoquangnam45.pharmacy.component;

import com.hoquangnam45.pharmacy.entity.User;
import com.hoquangnam45.pharmacy.pojo.RegisterRequest;
import com.hoquangnam45.pharmacy.pojo.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    @Mapping(target = "deliveryAddresses", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "phoneNumberConfirmed", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailConfirmed", ignore = true)
    User createNewUser(RegisterRequest registerRequest);
    
    UserProfile createUserProfile(User user);
}
