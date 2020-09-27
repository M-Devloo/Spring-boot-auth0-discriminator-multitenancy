package com.github.mdevloo.multi.tenancy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MultiTenancyApplication {

  public static void main(final String[] args) {
    SpringApplication.run(MultiTenancyApplication.class, args);
  }
}
