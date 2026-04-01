package com.mycompany.lab5.inventory.messaging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReservationInventoryEventTest {
    @Test
    void parseValidPayload() {
        String payload = "v1|eventId=reservation-10-created|action=CREATED|reservationId=10|equipmentId=3|username=student1|status=CREATED|timestamp=1700000000";
        ReservationInventoryEvent event = ReservationInventoryEvent.fromPayload(payload);

        Assertions.assertNotNull(event);
        Assertions.assertEquals("reservation-10-created", event.getEventId());
        Assertions.assertEquals("CREATED", event.getAction());
        Assertions.assertEquals(10, event.getReservationId());
        Assertions.assertEquals(3, event.getEquipmentId());
    }

    @Test
    void rejectInvalidPayload() {
        Assertions.assertNull(ReservationInventoryEvent.fromPayload(null));
        Assertions.assertNull(ReservationInventoryEvent.fromPayload("v0|bad"));
        Assertions.assertNull(ReservationInventoryEvent.fromPayload("v1|action=CREATED"));
    }
}
