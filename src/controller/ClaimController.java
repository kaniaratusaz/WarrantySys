package controller;

import dao.CustomerDAO;
import dao.ProductDAO;
import dao.WarrantyClaimDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Customer;
import model.Product;
import model.WarrantyClaim;
import util.ClaimExporter;
import util.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ClaimController {

    @FXML private TableView<WarrantyClaim> tblClaims;
    @FXML private TableColumn<WarrantyClaim, Integer> colId;
    @FXML private TableColumn<WarrantyClaim, String> colNumber;
    @FXML private TableColumn<WarrantyClaim, String> colCustomer;
    @FXML private TableColumn<WarrantyClaim, String> colProduct;
    @FXML private TableColumn<WarrantyClaim, String> colStatus;

    @FXML private ComboBox<Customer> cbCustomer;
    @FXML private ComboBox<Product> cbProduct;
    @FXML private TextField txtSerial;
    @FXML private DatePicker dpPurchaseDate;
    @FXML private DatePicker dpClaimDate;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextArea txtIssue;
    @FXML private TextArea txtResolution;

    private final WarrantyClaimDAO claimDAO = new WarrantyClaimDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final ObservableList<WarrantyClaim> claims = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colNumber.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getClaimNumber()));
        colCustomer.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getCustomer() != null ? c.getValue().getCustomer().getName() : ""));
        colProduct.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getProduct() != null ? c.getValue().getProduct().getName() : ""));
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));

        tblClaims.setItems(claims);

        tblClaims.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                cbCustomer.setValue(selected.getCustomer());
                cbProduct.setValue(selected.getProduct());
                txtSerial.setText(selected.getSerialNumber());
                dpPurchaseDate.setValue(selected.getPurchaseDate());
                dpClaimDate.setValue(selected.getClaimDate());
                cbStatus.setValue(selected.getStatus());
                txtIssue.setText(selected.getIssueDescription());
                txtResolution.setText(selected.getResolutionNotes());

                applyRoleRestrictionsOnEdit();
            }
        });

        // Semua status tersedia untuk Admin
        cbStatus.setItems(FXCollections.observableArrayList(
                "DIAJUKAN", "DIPROSES", "DITOLAK", "DISETUJUI", "SELESAI"
        ));

        // Staff restrictions
        if (Session.getRole().equalsIgnoreCase("STAFF")) {
            cbStatus.setItems(FXCollections.observableArrayList("DIAJUKAN"));
            cbStatus.setValue("DIAJUKAN");
            cbStatus.setDisable(true);
        }

        dpClaimDate.setValue(LocalDate.now());

        loadCustomersAndProducts();
        loadData();
    }

    private void applyRoleRestrictionsOnEdit() {
        if (Session.getRole().equalsIgnoreCase("STAFF")) {
            cbStatus.setDisable(true);  
        } else {
            cbStatus.setDisable(false); 
        }
    }

    private void loadCustomersAndProducts() {
        try {
            cbCustomer.setItems(FXCollections.observableArrayList(customerDAO.findAll()));
            cbProduct.setItems(FXCollections.observableArrayList(productDAO.findAll()));
        } catch (SQLException e) {
            showError("Gagal memuat pelanggan/produk: " + e.getMessage());
        }
    }

    private void loadData() {
        claims.clear();
        try {
            claims.addAll(claimDAO.findAll());
        } catch (SQLException e) {
            showError("Gagal memuat klaim: " + e.getMessage());
        }
    }

    // =========================
    // TAMBAH KLAIM
    // =========================
    @FXML
    private void handleAdd() {

        if (cbCustomer.getValue() == null || cbProduct.getValue() == null || dpClaimDate.getValue() == null) {
            showError("Pelanggan, produk, dan tanggal klaim wajib diisi.");
            return;
        }

        WarrantyClaim c = new WarrantyClaim();
        c.setClaimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        c.setCustomer(cbCustomer.getValue());
        c.setProduct(cbProduct.getValue());
        c.setSerialNumber(txtSerial.getText());
        c.setPurchaseDate(dpPurchaseDate.getValue());
        c.setClaimDate(dpClaimDate.getValue());
        c.setIssueDescription(txtIssue.getText());
        c.setResolutionNotes(txtResolution.getText());

        if (Session.getRole().equalsIgnoreCase("STAFF")) {
            c.setStatus("DIAJUKAN");
        } else {
            c.setStatus(cbStatus.getValue() != null ? cbStatus.getValue() : "DIAJUKAN");
        }

        if (c.getPurchaseDate() != null && c.getClaimDate().isBefore(c.getPurchaseDate())) {
            showError("Tanggal klaim tidak boleh sebelum tanggal pembelian.");
            return;
        }

        try {
            claimDAO.insert(c);
            loadData();
            clearForm();
        } catch (SQLException e) {
            showError("Gagal menambah klaim: " + e.getMessage());
        }
    }

    // =========================
    // UPDATE KLAIM
    // =========================
    @FXML
    private void handleUpdate() {
        WarrantyClaim selected = tblClaims.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Pilih klaim yang akan diubah.");
            return;
        }

        selected.setCustomer(cbCustomer.getValue());
        selected.setProduct(cbProduct.getValue());
        selected.setSerialNumber(txtSerial.getText());
        selected.setPurchaseDate(dpPurchaseDate.getValue());
        selected.setClaimDate(dpClaimDate.getValue());
        selected.setIssueDescription(txtIssue.getText());
        selected.setResolutionNotes(txtResolution.getText());

        if (Session.getRole().equalsIgnoreCase("STAFF")) {
            // staff tidak boleh ubah status
            selected.setStatus(selected.getStatus());
        } else {
            selected.setStatus(cbStatus.getValue());
        }

        if (selected.getPurchaseDate() != null && selected.getClaimDate().isBefore(selected.getPurchaseDate())) {
            showError("Tanggal klaim tidak boleh sebelum tanggal pembelian.");
            return;
        }

        try {
            claimDAO.update(selected);
            loadData();
            clearForm();
        } catch (SQLException e) {
            showError("Gagal mengubah klaim: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        WarrantyClaim selected = tblClaims.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Pilih klaim yang akan dihapus.");
            return;
        }

        try {
            claimDAO.delete(selected.getId());
            loadData();
            clearForm();
        } catch (SQLException e) {
            showError("Gagal menghapus klaim: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        tblClaims.getSelectionModel().clearSelection();

        if (Session.getRole().equalsIgnoreCase("STAFF")) {
            cbStatus.setValue("DIAJUKAN");
        }
    }

    @FXML
    private void handleExportCsv() {
        try {
            List<WarrantyClaim> list = claimDAO.findAll();
            ClaimExporter exporter = new ClaimExporter();
            exporter.exportToCsv(list, "claims_export.csv");
            showInfo("Data klaim berhasil diexport ke file claims_export.csv (folder project).");
        } catch (SQLException | IOException e) {
            showError("Gagal export CSV: " + e.getMessage());
        }
    }

    private void clearForm() {
        cbCustomer.setValue(null);
        cbProduct.setValue(null);
        txtSerial.clear();
        dpPurchaseDate.setValue(null);
        dpClaimDate.setValue(LocalDate.now());

        if (Session.getRole().equalsIgnoreCase("STAFF")) {
            cbStatus.setValue("DIAJUKAN");
        } else {
            cbStatus.setValue(null);
        }

        txtIssue.clear();
        txtResolution.clear();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}