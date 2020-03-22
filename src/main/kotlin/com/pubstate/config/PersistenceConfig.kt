package com.pubstate.config

import com.pubstate.util.Settings
import io.ebean.EbeanServer
import io.ebean.EbeanServerFactory
import io.ebean.config.ServerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersistenceConfig {

  @Bean
  fun ebeanServer(): EbeanServer {
    val config = ServerConfig()
    config.name = "db"
    config.isDefaultServer = true
    config.loadFromProperties()
    config.dataSourceConfig.username = Settings.getProperty("db.username")
    config.dataSourceConfig.password = Settings.getProperty("db.password")

    return EbeanServerFactory.create(config)
  }
}
