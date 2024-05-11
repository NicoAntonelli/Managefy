package nicoAntonelli.managefy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConfigurationProperties
public class EmailConfig {
    @Value("${emailConfig.email.sender.host}")
    private String host;
    @Value("${emailConfig.email.sender.port}")
    private String port;
    @Value("${emailConfig.email.sender.user}")
    private String user;
    @Value("${emailConfig.email.sender.password}")
    private String password;
    @Value("${emailConfig.email.sender.debug}")
    private Boolean debug;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Email host config
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));

        // Email sender config
        mailSender.setUsername(user);
        mailSender.setPassword(password);

        // Java mail properties config
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", debug);

        return mailSender;
    }
}
