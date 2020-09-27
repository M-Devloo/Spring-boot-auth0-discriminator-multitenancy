package com.github.mdevloo.multi.tenancy.fwk;

import lombok.AllArgsConstructor;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
public class FlywayConfig {

  public static final String DEFAULT_SCHEMA = "core";
  private final DataSource dataSource;

  @Bean
  public Flyway flyway() {
    final FluentConfiguration flywayConfig = Flyway.configure();
    flywayConfig.locations("db/migration");
    flywayConfig.dataSource(this.dataSource);
    flywayConfig.schemas(DEFAULT_SCHEMA);
    flywayConfig.baselineOnMigrate(true);
    final Flyway load = flywayConfig.load();
    load.migrate();
    return load;
  }
}
