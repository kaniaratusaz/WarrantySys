package controller;

import app.WarrantyApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Role;
import model.User;
import service.AuthService;
import util.Session;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            // ===========================
            // PROSES LOGIN KE DATABASE
            // ===========================
            User user = authService.login(username, password);

            // Kalau gagal, AuthService sudah lempar exception
            // dan akan tertangkap di catch di bawah.

            // ===========================
            // SIMPAN SESSION
            // ===========================
            Session.start(user.getUsername(), user.getRole().name());

            // ===========================
            // BUKA DASHBOARD SESUAI ROLE
            // ===========================
            if (user.getRole() == Role.ADMIN) {
                openDashboard("/view/admin-dashboard.fxml", "Admin Dashboard");
            } else {
                openDashboard("/view/staff-dashboard.fxml", "Staff Dashboard");
            }

            // Tutup jendela login
            ((Stage) txtUsername.getScene().getWindow()).close();

        } catch (Exception e) {
            // TAMPILKAN STACKTRACE DI OUTPUT NETBEANS
            e.printStackTrace();
            // Pesan pop-up
            showError(e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    private void openDashboard(String fxml, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(WarrantyApp.class.getResource(fxml));
        if (loader.getLocation() == null) {
            throw new IllegalStateException("FXML tidak ditemukan: " + fxml);
        }

        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Gagal");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}