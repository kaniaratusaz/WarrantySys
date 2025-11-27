package controller;

import app.WarrantyApp;
import dao.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Customer;
import model.Product;
import model.WarrantyClaim;
import util.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDashboardController {

    @FXML private Label lblUser;

    @FXML private Label lblTotalCustomers;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblTotalClaims;

    // ====== TABEL CLAIM TERBARU ======
    @FXML private TableView<WarrantyClaim> tblRecentClaims;
    @FXML private TableColumn<WarrantyClaim, String> colRecentNumber;
    @FXML private TableColumn<WarrantyClaim, String> colRecentCustomer;
    @FXML private TableColumn<WarrantyClaim, String> colRecentProduct;
    @FXML private TableColumn<WarrantyClaim, String> colRecentStatus;

    // ============================================================
    //  INITIALIZE
    // ============================================================
    @FXML
    public void initialize() {

        // Tampilkan username
        lblUser.setText("Halo, " + Session.getUsername());

        // Load statistik
        loadStats();

        // Setup tabel klaim terbaru
        setupRecentClaimsTable();

        // Load data klaim terbaru
        loadRecentClaims();
    }

    // ============================================================
    //  LOAD STATISTIK DASHBOARD
    // ============================================================
    private void loadStats() {
        try {
            lblTotalCustomers.setText(String.valueOf(countTable("customers")));
            lblTotalProducts.setText(String.valueOf(countTable("products")));
            lblTotalClaims.setText(String.valueOf(countTable("warranty_claims")));
        } catch (Exception e) {
            lblTotalCustomers.setText("-");
            lblTotalProducts.setText("-");
            lblTotalClaims.setText("-");
            e.printStackTrace();
        }
    }

    private int countTable(String table) throws Exception {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + table);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    // ============================================================
    //  SETUP TABLE KLAIM TERBARU
    // ============================================================
    private void setupRecentClaimsTable() {

        colRecentNumber.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getClaimNumber()));

        colRecentCustomer.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getCustomer().getName()));

        colRecentProduct.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getProduct().getName()));

        colRecentStatus.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
    }

    // ============================================================
    //  LOAD 10 KLAIM TERBARU
    // ============================================================
    private void loadRecentClaims() {
        try {
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT c.id, c.claim_number, c.status, " +
                            "cust.name AS customer, p.name AS product " +
                            "FROM warranty_claims c " +
                            "LEFT JOIN customers cust ON cust.id = c.customer_id " +
                            "LEFT JOIN products p ON p.id = c.product_id " +
                            "ORDER BY c.id DESC LIMIT 10"
            );

            ResultSet rs = ps.executeQuery();

            javafx.collections.ObservableList<WarrantyClaim> list =
                    javafx.collections.FXCollections.observableArrayList();

            while (rs.next()) {

                WarrantyClaim claim = new WarrantyClaim();
                claim.setId(rs.getInt("id"));
                claim.setClaimNumber(rs.getString("claim_number"));
                claim.setStatus(rs.getString("status"));

                // Set customer
                Customer cust = new Customer();
                cust.setName(rs.getString("customer"));
                claim.setCustomer(cust);

                // Set product
                Product p = new Product();
                p.setName(rs.getString("product"));
                claim.setProduct(p);

                list.add(claim);
            }

            tblRecentClaims.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    //  LOGOUT
    // ============================================================
    @FXML
    private void handleLogout(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText(null);
        alert.setContentText("Yakin ingin logout?");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {

                    Session.clear(); // hapus session

                    Stage loginStage = new Stage();
                    loginStage.setScene(new Scene(FXMLLoader.load(
                            WarrantyApp.class.getResource("/view/login-view.fxml"))));
                    loginStage.setTitle("Login");
                    loginStage.show();

                    ((Stage) lblUser.getScene().getWindow()).close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // ============================================================
    //  NAVIGASI MENU
    // ============================================================
    @FXML
    private void openCustomers(ActionEvent e) throws Exception {
        openWindow("/view/customers-view.fxml", "Kelola Pelanggan");
    }

    @FXML
    private void openProducts(ActionEvent e) throws Exception {
        openWindow("/view/products-view.fxml", "Kelola Produk");
    }

    @FXML
    private void openClaims(ActionEvent e) throws Exception {
        openWindow("/view/claims-view.fxml", "Kelola Klaim Garansi");
    }

    private void openWindow(String fxml, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(WarrantyApp.class.getResource(fxml));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
}