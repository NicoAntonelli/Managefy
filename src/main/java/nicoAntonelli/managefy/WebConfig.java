package nicoAntonelli.managefy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConfigurationProperties
@SuppressWarnings("unused")
public class WebConfig {
    private final String frontendBaseURL;

    @Autowired
    public WebConfig(Environment env) {
        if (env == null) {
            throw new RuntimeException("Can't access to environment variables from the file 'application.properties'!");
        }

        frontendBaseURL = env.getProperty("frontend.base-url");
    }

    @Bean
    public WebMvcConfigurer corsConfig() {
        return new WebMvcConfigurer() {
            @SuppressWarnings("NullableProblems")
            @Override()
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(frontendBaseURL)
                        .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(),
                                        HttpMethod.PUT.name(), HttpMethod.DELETE.name())
                        .allowedHeaders(HttpHeaders.CONTENT_TYPE, HttpHeaders.AUTHORIZATION);
            }
        };
    }
}
