package com.mycompany.lab5.reservation.business;

import com.mycompany.lab5.reservation.helper.Reservation;
import com.mycompany.lab5.reservation.persistence.ReservationStore;
import java.util.List;

public class ReservationManager {
    private final ReservationStore store = new ReservationStore();
    public Reservation create(Reservation reservation) { return store.create(reservation); }
    public List<Reservation> all() { return store.all(); }
    public Reservation cancel(int id) { return store.cancel(id); }
}
