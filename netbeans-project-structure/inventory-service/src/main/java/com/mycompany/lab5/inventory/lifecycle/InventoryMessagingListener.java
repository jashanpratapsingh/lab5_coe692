package com.mycompany.lab5.inventory.lifecycle;

import com.mycompany.lab5.inventory.messaging.InventoryEventSubscriber;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InventoryMessagingListener implements ServletContextListener {
    private final InventoryEventSubscriber subscriber = new InventoryEventSubscriber();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        subscriber.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        subscriber.stop();
    }
}
