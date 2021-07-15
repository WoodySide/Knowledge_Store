package com.webApp.event;

import com.webApp.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnUserAccountChangeEvent extends ApplicationEvent {

    private User user;

    private String action;

    private String actionStatus;

    public OnUserAccountChangeEvent(User user, String action, String actionStatus) {
        super(user);
        this.user = user;
        this.action = action;
        this.actionStatus = actionStatus;
    }
}
