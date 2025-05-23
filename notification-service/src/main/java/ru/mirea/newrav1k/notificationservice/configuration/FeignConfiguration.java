package ru.mirea.newrav1k.notificationservice.configuration;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ru.mirea.newrav1k.notificationservice.service.client")
public class FeignConfiguration {

}