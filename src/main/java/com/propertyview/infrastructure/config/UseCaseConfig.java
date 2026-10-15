package com.propertyview.infrastructure.config;

import com.propertyview.application.port.in.CreateHotelUseCase;
import com.propertyview.application.service.CreateHotelService;
import com.propertyview.application.service.HotelQueryService;
import com.propertyview.domain.port.HotelRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UseCaseConfig {

    @Bean
    CreateHotelUseCase createHotelUseCase(HotelRepository hotels) {
        return new CreateHotelService(hotels);
    }

    @Bean
    HotelQueryService hotelQueryService(HotelRepository hotels) {
        return new HotelQueryService(hotels);
    }
}
