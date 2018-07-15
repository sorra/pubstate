package com.pubstate.config

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.EbeanServerFactory
import com.avaje.ebean.config.PropertyMap
import com.avaje.ebean.config.ServerConfig
import com.pubstate.util.Settings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersistenceConfig {
  @Bean
  fun ebeanServer(): EbeanServer {
    val config = ServerConfig()
    config.name = "db"
    val ebeanProps = PropertyMap.defaultProperties()
    Settings.props.getProperty("db.pw")?.let { pw ->
      if (pw.isNotEmpty()) {
        ebeanProps.setProperty("datasource.db.password", pw)
      }
    }
    config.loadFromProperties(ebeanProps)
    config.isDefaultServer = true

    return EbeanServerFactory.create(config)
  }
}
