package com.mycompany.lab5.inventory.business;

import com.mycompany.lab5.inventory.helper.InventoryItem;
import com.mycompany.lab5.inventory.persistence.InventoryStore;
import java.util.Collection;

public class InventoryManager {
    private final InventoryStore store = new InventoryStore();
    public Collection<InventoryItem> all() { return store.all(); }
    public InventoryItem updateStatus(String tag, String status) { return store.updateStatus(tag, status); }
}
