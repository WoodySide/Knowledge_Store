package com.webApp.event.listener;

import com.webApp.event.OnRegenerateEmailVerificationEvent;
import com.webApp.exception_handling.MailSendException;
import com.webApp.model.User;
import com.webApp.model.token.EmailVerificationToken;
import com.webApp.service.MailService;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
@Slf4j
public class OnRegenerateEmailVerificationListener implements ApplicationListener<OnRegenerateEmailVerificationEvent> {

    private final MailService mailService;

    public OnRegenerateEmailVerificationListener(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     *  As soon  as a registration event is complete, invoke the email verification
     */

    @Override
    @Async
    public void onApplicationEvent(OnRegenerateEmailVerificationEvent onRegenerateEmailVerificationEvent) {
        resendEmailVerification(onRegenerateEmailVerificationEvent);
    }

    /**
     * Send email verification to the user ans persist the token in the database
     */

    private void resendEmailVerification(OnRegenerateEmailVerificationEvent event) {
        User user = event.getUser();
        EmailVerificationToken emailVerificationToken = event.getToken();
        String recipientAddress = user.getEmail();

        String emailConfirmationUrl = event.getRedirectUrl().queryParam("token",
                emailVerificationToken.getToken()).toUriString();

        try {
            mailService.sendEmailVerification(emailConfirmationUrl, recipientAddress);
        } catch (IOException | TemplateException | MessagingException e) {
            log.error(e.getMessage());
            throw new MailSendException(recipientAddress, "Email Verification");
        }
    }

}

