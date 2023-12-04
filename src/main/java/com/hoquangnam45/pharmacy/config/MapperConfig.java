package com.hoquangnam45.pharmacy.config;

import com.hoquangnam45.pharmacy.component.MedicineMapper;
import com.hoquangnam45.pharmacy.component.UserMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    public MedicineMapper medicineMapper() {
        return Mappers.getMapper(MedicineMapper.class);
    }
}
