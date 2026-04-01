package com.mycompany.lab5.inventory.messaging;

import com.mycompany.lab5.inventory.persistence.InventoryStore;
import io.grpc.stub.StreamObserver;
import io.kubemq.sdk.event.EventReceive;
import io.kubemq.sdk.event.Subscriber;
import io.kubemq.sdk.subscription.SubscribeRequest;
import io.kubemq.sdk.subscription.SubscribeType;
import io.kubemq.sdk.tools.Converter;
import java.io.IOException;

public class InventoryEventSubscriber {
    private static final String DEFAULT_KUBEMQ_ADDRESS = "kubemq-service:50000";
    private static final String DEFAULT_CHANNEL = "reservation-events";
    private static final String CLIENT_ID = "inventory-service-subscriber";

    private Thread worker;
    private volatile boolean running;

    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        worker = new Thread(this::runLoop, "inventory-kubemq-subscriber");
        worker.setDaemon(true);
        worker.start();
    }

    public synchronized void stop() {
        running = false;
        if (worker != null) {
            worker.interrupt();
            worker = null;
        }
    }

    private void runLoop() {
        while (running) {
            try {
                String address = resolveEnv("KUBEMQ_ADDRESS", "kubeMQAddress", DEFAULT_KUBEMQ_ADDRESS);
                String channelName = resolveEnv("RESERVATION_EVENT_CHANNEL", null, DEFAULT_CHANNEL);

                Subscriber subscriber = new Subscriber(address);
                SubscribeRequest req = new SubscribeRequest();
                req.setChannel(channelName);
                req.setClientID(CLIENT_ID);
                req.setSubscribeType(SubscribeType.EventsStore);

                subscriber.SubscribeToEvents(req, new StreamObserver<EventReceive>() {
                    @Override
                    public void onNext(EventReceive value) {
                        handleEvent(value);
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onCompleted() {
                    }
                });

                sleepQuietly(2000);
            } catch (Exception ignored) {
                sleepQuietly(2000);
            }
        }
    }

    private void handleEvent(EventReceive value) {
        try {
            String payload = String.valueOf(Converter.FromByteArray(value.getBody()));
            ReservationInventoryEvent event = ReservationInventoryEvent.fromPayload(payload);
            if (event == null || event.getEquipmentId() <= 0) {
                return;
            }

            InventoryStore store = new InventoryStore();
            if (store.isEventProcessed(event.getEventId())) {
                return;
            }

            String desiredStatus = "CREATED".equals(event.getAction()) ? "RESERVED" : "AVAILABLE";
            store.updateStatusByEquipmentId(event.getEquipmentId(), desiredStatus);
            store.markEventProcessed(event.getEventId(), event.getAction(), event.getReservationId(), event.getEquipmentId());
        } catch (IOException | ClassNotFoundException ignored) {
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
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
