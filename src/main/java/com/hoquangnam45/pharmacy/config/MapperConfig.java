package com.hoquangnam45.pharmacy.config;

import com.hoquangnam45.pharmacy.component.mapper.AuditMapper;
import com.hoquangnam45.pharmacy.component.mapper.MedicineMapper;
import com.hoquangnam45.pharmacy.component.mapper.OrderMapper;
import com.hoquangnam45.pharmacy.component.mapper.UserMapper;
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


    @Bean
    public AuditMapper auditMapper() {
        return Mappers.getMapper(AuditMapper.class);
    }

    @Bean
    public OrderMapper orderMapper() {
        return Mappers.getMapper(OrderMapper.class);
    }
}
