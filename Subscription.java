package model;

public class Subscription {
    private int id;
    private int subscriberId;
    private int magazineId;
    private String subscriberName;
    private String magazineName;
    private String startDate;
    private String endDate;
    private int months;
    private double totalPrice;
    private String status;

    public Subscription() {}

    public Subscription(int id, int subscriberId, int magazineId,
                        String subscriberName, String magazineName,
                        String startDate, String endDate,
                        int months, double totalPrice, String status) {
        this.id = id;
        this.subscriberId = subscriberId;
        this.magazineId = magazineId;
        this.subscriberName = subscriberName;
        this.magazineName = magazineName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.months = months;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSubscriberId() { return subscriberId; }
    public void setSubscriberId(int subscriberId) { this.subscriberId = subscriberId; }

    public int getMagazineId() { return magazineId; }
    public void setMagazineId(int magazineId) { this.magazineId = magazineId; }

    public String getSubscriberName() { return subscriberName; }
    public void setSubscriberName(String subscriberName) { this.subscriberName = subscriberName; }

    public String getMagazineName() { return magazineName; }
    public void setMagazineName(String magazineName) { this.magazineName = magazineName; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getMonths() { return months; }
    public void setMonths(int months) { this.months = months; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
