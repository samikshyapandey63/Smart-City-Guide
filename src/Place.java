public class Place {
    private int id;
    private String name, category, city, area, address, phone;
    private String openTime, closeTime, description;
    private double avgRating;
    private int reviewCount;

    public Place(int id, String name, String category, String city, String area, String address,
                 String phone, String openTime, String closeTime, String description,
                 double avgRating, int reviewCount) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.city = city;
        this.area = area;
        this.address = address;
        this.phone = phone;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.description = description;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getCity() { return city; }
    public String getArea() { return area; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getOpenTime() { return openTime; }
    public String getCloseTime() { return closeTime; }
    public String getDescription() { return description; }
    public double getAvgRating() { return avgRating; }
    public int getReviewCount() { return reviewCount; }

    // "08:00 - 21:00"
    public String getHours() {
        return openTime + " - " + closeTime;
    }

    // "4.4 (9)" or "No ratings"
    public String getRatingText() {
        if (reviewCount == 0) {
            return "No ratings";
        }
        return (Math.round(avgRating * 10) / 10.0) + " (" + reviewCount + ")";
    }
}