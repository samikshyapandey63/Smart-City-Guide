public class User {
    private int id;
    private String name, username, password, role;
//    private String city="Kathmandu";

    public User(int id, String name, String username, String password, String role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
//    public String getCity() { return city; }
//    public void setCity(String city) { this.city = city; }
}