package model;

public class Admin extends User {
    public Admin() {}

    public Admin(int id, String username, String password) {
        super(id, username, password, Role.ADMIN);
    }

    @Override
    public void showDashboard() {
        System.out.println("Menampilkan dashboard Admin");
    }
}
