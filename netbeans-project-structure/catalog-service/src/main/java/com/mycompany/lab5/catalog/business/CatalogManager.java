package com.mycompany.lab5.catalog.business;

import com.mycompany.lab5.catalog.helper.Equipment;
import com.mycompany.lab5.catalog.persistence.CatalogStore;
import java.util.List;

public class CatalogManager {
    private final CatalogStore store = new CatalogStore();
    public List<Equipment> list(String q) { return store.search(q); }
}
