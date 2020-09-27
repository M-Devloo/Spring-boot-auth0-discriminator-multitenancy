package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import lombok.AllArgsConstructor;
import org.hibernate.EmptyInterceptor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class HibernateInterceptorConfiguration implements HibernatePropertiesCustomizer {

  private final EmptyInterceptor tenantInterceptor;

  @Override
  public void customize(final Map<String, Object> hibernateProperties) {
    hibernateProperties.put("hibernate.session_factory.interceptor", this.tenantInterceptor);
  }
}
