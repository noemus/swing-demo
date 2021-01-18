package app;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static java.util.Collections.singletonMap;

@Configuration
@ComponentScan
@PropertySource("classpath:default.properties")
public class AppConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer setUp() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static CustomScopeConfigurer customScopes() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.setScopes(singletonMap(SwingScope.SWING_SCOPE, new SwingScope()));
        return configurer;
    }
}
