package nicoAntonelli.managefy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConfigurationProperties
@SuppressWarnings("unused")
public class EmailConfig {
    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final String debug;

    @Autowired
    public EmailConfig(Environment env) {
        if (env == null) {
            throw new RuntimeException("Can't access to environment variables from the file 'application.properties'!");
        }

        host = env.getProperty("spring.mail.host");
        port = env.getProperty("spring.mail.port");
        username = env.getProperty("spring.mail.username");
        password = env.getProperty("spring.mail.password");
        debug = env.getProperty("spring.mail.debug");
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Email host & sender config
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // Java mail properties config
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", Boolean.parseBoolean(debug));

        return mailSender;
    }
}
