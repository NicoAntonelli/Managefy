package nicoAntonelli.managefy.services;

import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final String username;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, Environment env) {
        if (env == null) {
            throw new RuntimeException("Can't access to environment variables from the file 'application.properties'!");
        }

        this.javaMailSender = javaMailSender;
        username = env.getProperty("spring.mail.username");
    }

    @Async
    public void CodeValidationEmail(String address, String code) {
        String text = "Hi there, this is an email from Managefy App!\n" +
                "In order to validate your email you must now enter " +
                "this number in the app: '" + code + "'.\n" +
                "If you did not request any email validation, "
                +"please ignore this message.\n\n\n" +
                "-The Managefy Team\n";

        String subject = "Managefy app - User validation code";

        SendEmail(address, subject, text);
    }

    @Async
    public void SendEmail(String address, String subject, String body) {
        if (address == null || address.isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'SendEmail' - Address not supplied");
        }
        if (subject == null || subject.isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'SendEmail' - Subject not supplied");
        }
        if (body == null || body.isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'SendEmail' - Body not supplied");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(address);
        message.setFrom(username);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }
}
