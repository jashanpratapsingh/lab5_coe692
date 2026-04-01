package com.mycompany.lab5.reservation.helper;

public class Reservation {
    private int id;
    private String username;
    private int equipmentId;
    private String startDate;
    private String endDate;
    private String status;

    public Reservation() {}
    public Reservation(int id, String username, int equipmentId, String startDate, String endDate, String status) {
        this.id = id; this.username = username; this.equipmentId = equipmentId; this.startDate = startDate; this.endDate = endDate; this.status = status;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getEquipmentId() { return equipmentId; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
