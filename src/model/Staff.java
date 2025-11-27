package model;

public class Staff extends User {
    public Staff() {}

    public Staff(int id, String username, String password) {
        super(id, username, password, Role.STAFF);
    }

    @Override
    public void showDashboard() {
        System.out.println("Menampilkan dashboard Staff");
    }
}
