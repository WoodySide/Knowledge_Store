package com.webApp.event.listener;

import com.webApp.event.OnUserAccountChangeEvent;
import com.webApp.exception_handling.MailSendException;
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
public class OnUserAccountChangeListener implements ApplicationListener<OnUserAccountChangeEvent> {

    private final MailService mailService;

    public OnUserAccountChangeListener(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     *As soon as a registration event is complete, invoke the email verification
     * asynchronously is an another thread pool
     */
    @Override
    @Async
    public void onApplicationEvent(OnUserAccountChangeEvent onUserAccountChangeEvent) {
        sendAccountChangeEmail(onUserAccountChangeEvent);
    }

    /**
     * Sends Reset Link to the mail address with a password reset link token
     */
    private void sendAccountChangeEmail(OnUserAccountChangeEvent event) {
        User user = event.getUser();
        String action = event.getAction();
        String actionStatus = event.getActionStatus();
        String recipientAddress = user.getEmail();

        try {
            mailService.sendAccountChangeEmail(action, actionStatus, recipientAddress);
        } catch (IOException | TemplateException | MessagingException e) {
            log.error(e.getMessage());
            throw new MailSendException(recipientAddress, "Account Change Mail");
        }
    }


}
