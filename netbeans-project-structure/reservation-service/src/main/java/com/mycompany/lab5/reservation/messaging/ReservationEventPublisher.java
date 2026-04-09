package com.mycompany.lab5.reservation.messaging;

import com.mycompany.lab5.reservation.helper.Reservation;
import io.kubemq.sdk.event.Channel;
import io.kubemq.sdk.event.Event;
import io.kubemq.sdk.tools.Converter;

public class ReservationEventPublisher {
    private static final String DEFAULT_KUBEMQ_ADDRESS = "kubemq-service:50000";
    private static final String DEFAULT_CHANNEL = "reservation-events";
    private static final String CLIENT_ID = "reservation-service-publisher";

    
    public void publish(Reservation reservation, String action) {
        if (reservation == null || action == null || action.trim().isEmpty()) {
            return;
        }

        String address = resolveEnv("KUBEMQ_ADDRESS", "kubeMQAddress", DEFAULT_KUBEMQ_ADDRESS);
        String channelName = resolveEnv("RESERVATION_EVENT_CHANNEL", null, DEFAULT_CHANNEL);
        String payload = buildPayload(reservation, action.trim().toUpperCase());
        String eventId = "reservation-" + reservation.getId() + "-" + action.trim().toLowerCase();

        try {
            Channel channel = new Channel(channelName, CLIENT_ID, true, address);
            Event event = new Event();
            event.setEventId(eventId);
            event.setMetadata("reservation-event-v1");
            event.setBody(Converter.ToByteArray(payload));
            channel.SendEvent(event);
        } catch (Exception ignored) {
            // Non-blocking best-effort publish for local/dev resiliency.
        }
    }

    String buildPayload(Reservation reservation, String action) {
        long timestamp = System.currentTimeMillis();
        return "v1|"
                + "eventId=" + "reservation-" + reservation.getId() + "-" + action.toLowerCase() + "|"
                + "action=" + action + "|"
                + "reservationId=" + reservation.getId() + "|"
                + "equipmentId=" + reservation.getEquipmentId() + "|"
                + "username=" + safe(reservation.getUsername()) + "|"
                + "status=" + safe(reservation.getStatus()) + "|"
                + "timestamp=" + timestamp;
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "_").trim();
    }

    private String resolveEnv(String key, String legacyKey, String fallback) {
        String value = trimToNull(System.getenv(key));
        if (value != null) {
            return value;
        }
        if (legacyKey != null) {
            value = trimToNull(System.getenv(legacyKey));
            if (value != null) {
                return value;
            }
        }
        return fallback;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String out = value.trim();
        return out.isEmpty() ? null : out;
    }
}
