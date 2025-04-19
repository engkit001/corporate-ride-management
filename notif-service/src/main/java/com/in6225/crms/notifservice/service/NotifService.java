package com.in6225.crms.notifservice.service;

// Install the Java helper library from twilio.com/docs/java/install
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.in6225.crms.notifservice.config.TwilioConfig;
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

    public void sendNotif(String notif) {
        String sid = config.getAccountSid();
        String token = config.getAuthToken();
        String toPhoneNumber = config.getToPhoneNumber();
        String fromPhoneNumber = config.getFromPhoneNumber();

        ObjectMapper mapper = new ObjectMapper();
        String rideId = "";
        try {
            JsonNode node = mapper.readTree(notif);
            rideId = node.get("rideId").asText();
        } catch (Exception e) {
            e.printStackTrace(); // or log the error
        }

        Twilio.init(sid, token);
        Message message = Message
                .creator(new com.twilio.type.PhoneNumber(toPhoneNumber),
                        new com.twilio.type.PhoneNumber(fromPhoneNumber),
                        "New ride alert: " + rideId)
                .create();
        System.out.println(message.getBody());
    }
}
