package ru.rgs.dummy.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties holder class
 *
 * @author jihor (dmitriy_zhikharev@rgs.ru)
 * Created on 2017-08-01
 */
@ConfigurationProperties(prefix = "dummy")
@Getter
@Setter
public class DummyProperties {
    private String simpleMsg;
    private Container inner;

    @Getter
    @Setter
    public static class Container {
        private String happyBirthdayMsg;
    }
}
