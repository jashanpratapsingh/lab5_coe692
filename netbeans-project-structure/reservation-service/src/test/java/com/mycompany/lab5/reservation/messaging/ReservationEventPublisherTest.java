package com.mycompany.lab5.reservation.messaging;

import com.mycompany.lab5.reservation.helper.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReservationEventPublisherTest {
    @Test
    void buildPayloadIncludesRequiredFields() {
        ReservationEventPublisher publisher = new ReservationEventPublisher();
        Reservation reservation = new Reservation(12, "student1", 5, "2026-04-01", "2026-04-03", "CREATED");

        String payload = publisher.buildPayload(reservation, "CREATED");
        Assertions.assertTrue(payload.startsWith("v1|"));
        Assertions.assertTrue(payload.contains("eventId=reservation-12-created"));
        Assertions.assertTrue(payload.contains("action=CREATED"));
        Assertions.assertTrue(payload.contains("reservationId=12"));
        Assertions.assertTrue(payload.contains("equipmentId=5"));
        Assertions.assertTrue(payload.contains("username=student1"));
    }
}
