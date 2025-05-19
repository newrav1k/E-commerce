package ru.newrav1k.mirea.orderservice.configuration;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ru.newrav1k.mirea.orderservice.service.client")
public class FeignClientConfiguration {

}