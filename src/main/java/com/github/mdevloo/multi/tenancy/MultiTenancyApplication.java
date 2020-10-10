package com.github.mdevloo.multi.tenancy;

import com.github.mdevloo.multi.tenancy.fwk.multitenancy.MultiTenancyRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaRepositories(repositoryBaseClass = MultiTenancyRepository.class)
public class MultiTenancyApplication {

  public static void main(final String[] args) {
    SpringApplication.run(MultiTenancyApplication.class, args);
  }
}
