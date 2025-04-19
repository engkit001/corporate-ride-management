package com.in6225.crms.notifservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class TwilioConfig {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.to.phone.number}")
    private String toPhoneNumber;

    @Value("${twilio.from.phone.number}")
    private String fromPhoneNumber;
}
