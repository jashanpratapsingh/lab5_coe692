package com.mycompany.lab5.inventory.helper;

public class InventoryItem {
    private String assetTag;
    private int equipmentId;
    private String status;

    public InventoryItem() {}
    public InventoryItem(String assetTag, int equipmentId, String status) {
        this.assetTag = assetTag; this.equipmentId = equipmentId; this.status = status;
    }
    public String getAssetTag() { return assetTag; }
    public void setAssetTag(String assetTag) { this.assetTag = assetTag; }
    public int getEquipmentId() { return equipmentId; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
