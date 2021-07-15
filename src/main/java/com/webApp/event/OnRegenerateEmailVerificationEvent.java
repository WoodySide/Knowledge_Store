package com.webApp.event;

import com.webApp.model.User;
import com.webApp.model.token.EmailVerificationToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
public class OnRegenerateEmailVerificationEvent extends ApplicationEvent {

    private transient  UriComponentsBuilder redirectUrl;

    private User user;

    private transient EmailVerificationToken token;

    public OnRegenerateEmailVerificationEvent(User user, UriComponentsBuilder redirectUtl, EmailVerificationToken token) {
        super(user);
        this.user = user;
        this.redirectUrl = redirectUtl;
        this.token = token;
    }
}
