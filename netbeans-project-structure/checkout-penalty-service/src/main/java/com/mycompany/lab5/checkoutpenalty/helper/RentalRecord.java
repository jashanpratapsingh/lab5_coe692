package com.mycompany.lab5.checkoutpenalty.helper;

public class RentalRecord {
    private int id;
    private String username;
    private String assetTag;
    private String dueDate;
    private String returnDate;
    private double fineAmount;

    public RentalRecord() {}
    public RentalRecord(int id, String username, String assetTag, String dueDate) {
        this.id = id; this.username = username; this.assetTag = assetTag; this.dueDate = dueDate;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAssetTag() { return assetTag; }
    public void setAssetTag(String assetTag) { this.assetTag = assetTag; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
}
