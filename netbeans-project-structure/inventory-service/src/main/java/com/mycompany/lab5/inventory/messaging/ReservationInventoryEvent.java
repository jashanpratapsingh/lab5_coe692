package com.mycompany.lab5.inventory.messaging;

import java.util.HashMap;
import java.util.Map;

public class ReservationInventoryEvent {
    private final String eventId;
    private final String action;
    private final int reservationId;
    private final int equipmentId;

    public ReservationInventoryEvent(String eventId, String action, int reservationId, int equipmentId) {
        this.eventId = eventId;
        this.action = action;
        this.reservationId = reservationId;
        this.equipmentId = equipmentId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getAction() {
        return action;
    }

    public int getReservationId() {
        return reservationId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public static ReservationInventoryEvent fromPayload(String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            return null;
        }

        String[] tokens = payload.split("\\|");
        if (tokens.length < 6 || !"v1".equals(tokens[0])) {
            return null;
        }

        Map<String, String> kv = new HashMap<>();
        for (int i = 1; i < tokens.length; i++) {
            int idx = tokens[i].indexOf('=');
            if (idx <= 0 || idx == tokens[i].length() - 1) {
                continue;
            }
            kv.put(tokens[i].substring(0, idx), tokens[i].substring(idx + 1));
        }

        String eventId = kv.get("eventId");
        String action = kv.get("action");
        int reservationId = parseInt(kv.get("reservationId"));
        int equipmentId = parseInt(kv.get("equipmentId"));
        if (eventId == null || action == null) {
            return null;
        }
        return new ReservationInventoryEvent(eventId, action, reservationId, equipmentId);
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return 0;
        }
    }
}
