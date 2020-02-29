package com.pubstate.config

import com.pubstate.util.Settings
import io.ebean.EbeanServer
import io.ebean.EbeanServerFactory
import io.ebean.config.ServerConfig
import io.ebean.config.properties.PropertiesLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersistenceConfig {

  @Bean
  fun ebeanServer(): EbeanServer {
    val config = ServerConfig()
    config.name = "db"
    config.isDefaultServer = true

    val ebeanProps = PropertiesLoader.load()
    ebeanProps.setProperty("datasource.db.username", Settings.getProperty("db.username"))
    ebeanProps.setProperty("datasource.db.password", Settings.getProperty("db.password"))

    config.loadFromProperties()
    config.loadFromProperties(ebeanProps)
    return EbeanServerFactory.create(config)
  }
}
