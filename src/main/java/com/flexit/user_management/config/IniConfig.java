package com.flexit.user_management.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IniConfig {

    @Bean
    public org.apache.commons.configuration.Configuration getIniConfig(@Autowired ApplicationArguments applicationArguments) throws org.apache.commons.configuration.ConfigurationException {
        return new PropertiesConfiguration(applicationArguments.getOptionValues("Dpath").get(0));
    }
}