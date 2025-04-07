package com.in6225.crms.rideevents;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoDriverAvailableEvent implements Serializable {
    private Long rideId;

    // Convert object to JSON
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Static method to create object from JSON
    public static NoDriverAvailableEvent fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, NoDriverAvailableEvent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

