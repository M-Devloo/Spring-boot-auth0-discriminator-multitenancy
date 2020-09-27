package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import org.hibernate.EmptyInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateMultiTenancyInterceptorBean {

  @Bean
  public EmptyInterceptor hibernateTenantInterceptor() {
    return new TenantInterceptor();
  }
}
