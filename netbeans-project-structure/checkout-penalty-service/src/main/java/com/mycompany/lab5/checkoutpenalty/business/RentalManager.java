package com.mycompany.lab5.checkoutpenalty.business;

import com.mycompany.lab5.checkoutpenalty.helper.RentalRecord;
import com.mycompany.lab5.checkoutpenalty.persistence.RentalStore;
import java.util.List;

public class RentalManager {
    private final RentalStore store = new RentalStore();
    public RentalRecord checkout(String username, String assetTag, String dueDate) { return store.checkout(username, assetTag, dueDate); }
    public RentalRecord processReturn(int id, String returnDate) { return store.processReturn(id, returnDate); }
    public List<RentalRecord> history(String user) { return store.byUser(user); }
}
