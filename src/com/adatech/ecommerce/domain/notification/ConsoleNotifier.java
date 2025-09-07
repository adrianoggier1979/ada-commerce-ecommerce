package com.adatech.ecommerce.domain.notification;

public class ConsoleNotifier implements Notifier{

    @Override
    public void notify(String toEmail, String subjetc, String body) {
        System.out.println("[EMAIL] " + toEmail);
        System.out.println("[EMAIL] " + subjetc);
        System.out.println("[EMAIL] " + body);

    }
}
