package com.in6225.crms.notifservice.service;

// Install the Java helper library from twilio.com/docs/java/install
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.in6225.crms.notifservice.config.TwilioConfig;
import com.in6225.crms.rideevents.DriverAssignedEvent;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotifService {

    private final TwilioConfig config;

    public NotifService(TwilioConfig config) {
        this.config = config;
    }

    public void handleDriverAssignedEvent(DriverAssignedEvent driverAssignedEvent) {
        String sid = config.getAccountSid();
        String token = config.getAuthToken();
        String toPhoneNumber = "whatsapp:+65" + driverAssignedEvent.getPhoneNumber();
        System.out.println(toPhoneNumber);
        String fromPhoneNumber = config.getFromPhoneNumber();

        Twilio.init(sid, token);
        Message message = Message
                .creator(new com.twilio.type.PhoneNumber(toPhoneNumber),
                        new com.twilio.type.PhoneNumber(fromPhoneNumber),
                        "New ride alert: " + driverAssignedEvent.getRideId())
                .create();
        System.out.println(message.getBody());
    }
}
