package com.adatech.ecommerce.domain.notification;

public interface Notifier {
    void  notify(String toEmail, String subjetc, String body );
}
