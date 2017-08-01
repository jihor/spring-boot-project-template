package ru.rgs.dummy.config.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jihor (dmitriy_zhikharev@rgs.ru)
 * Created on 2017-08-01
 */
@Configuration
@EnableConfigurationProperties
public class PropertiesConfiguration {

    @Bean
    DummyProperties dummyProperties() {
        return new DummyProperties();
    }

}
