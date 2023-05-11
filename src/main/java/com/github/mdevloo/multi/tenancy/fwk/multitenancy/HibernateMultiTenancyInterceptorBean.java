package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import org.hibernate.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateMultiTenancyInterceptorBean {

  @Bean
  public Interceptor hibernateTenantInterceptor() {
    return new TenantInterceptor();
  }
}
