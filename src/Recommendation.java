public class Recommendation {
    private Place place;
    private String reason;
    private int score;
    private boolean personal;       // true = picked from this user's own activity

    public Recommendation(Place place, String reason, int score, boolean personal) {
        this.place = place;
        this.reason = reason;
        this.score = score;
        this.personal = personal;
    }

    public Place getPlace() { return place; }
    public String getReason() { return reason; }
    public int getScore() { return score; }
    public boolean isPersonal() { return personal; }
}
