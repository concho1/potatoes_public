package com.goott.potatoes.manager.model;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@WebListener
public class SessionListener implements HttpSessionListener {

    private static final Map<String, HttpSession> sessions = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        System.out.println("session created : "+session.getId());
        sessions.put(session.getId(),session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        System.out.println("session destoryed : "+session.getId());
        sessions.remove(session.getId());
    }

    public static HttpSession getSessionByUserId(String userId) {

        synchronized (sessions) {
            for(HttpSession session : sessions.values()) {
                if(userId.equals(session.getAttribute("userId"))) {
                    return session;
                }
            }
        }
        return null;
    }

}
