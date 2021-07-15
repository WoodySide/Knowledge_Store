package com.webApp.event.listener;

import com.webApp.event.OnGenerateResetLinkEvent;
import com.webApp.exception_handling.MailSendException;
import com.webApp.model.PasswordResetToken;
import com.webApp.model.User;
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
public class OnGenerateResetLinkEventListener implements ApplicationListener<OnGenerateResetLinkEvent> {

    private final MailService mailService;

    public OnGenerateResetLinkEventListener(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     *Sends Reset Link to the mail address with a password reset link token
     */
    @Override
    @Async
    public void onApplicationEvent(OnGenerateResetLinkEvent onGenerateResetLinkEvent) {
        sendResetLink(onGenerateResetLinkEvent);
    }

    /**
     * Sends Reset Link to the mail address with a password reset link token
     */
    private void sendResetLink(OnGenerateResetLinkEvent event) {
        PasswordResetToken passwordResetToken = event.getPasswordResetToken();
        User user = passwordResetToken.getUser();
        String recipientAddress = user.getEmail();
        String emailConfirmationUrl = event.getRedirectUrl().queryParam("token", passwordResetToken.getToken())
                .toUriString();

        try {
            mailService.sendResetLink(emailConfirmationUrl, recipientAddress);
        } catch (IOException | TemplateException | MessagingException e) {
            log.error(e.getMessage());
            throw new MailSendException(recipientAddress, "Email verification");
        }
    }


}
