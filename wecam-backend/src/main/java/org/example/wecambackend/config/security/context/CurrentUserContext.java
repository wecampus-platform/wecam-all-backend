package org.example.wecambackend.config.security.context;

import org.example.wecambackend.config.security.UserDetailsImpl;

public class CurrentUserContext {
    private static final ThreadLocal<UserDetailsImpl> currentUserHolder = new ThreadLocal<>();

    public static void set(UserDetailsImpl user) {
        currentUserHolder.set(user);
    }

    public static UserDetailsImpl get() {
        return currentUserHolder.get();
    }

    public static void clear() {
        currentUserHolder.remove();
    }
}
