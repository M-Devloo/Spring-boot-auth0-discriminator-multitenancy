package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import java.util.Map;
import lombok.AllArgsConstructor;
import org.hibernate.Interceptor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class HibernateInterceptorConfiguration implements HibernatePropertiesCustomizer {

  private final Interceptor tenantInterceptor;

  @Override
  public void customize(final Map<String, Object> hibernateProperties) {
    hibernateProperties.put("hibernate.session_factory.interceptor", this.tenantInterceptor);
  }
}
