package com.github.mdevloo.multi.tenancy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
class TransactionalExecutor {

  @Transactional(propagation = Propagation.REQUIRED)
  public void required(final Runnable runnable) {
    runnable.run();
  }
}
