package controller;

import dao.CustomerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Customer;

import java.sql.SQLException;

public class CustomerController {

    @FXML
    private TableView<Customer> tblCustomers;
    @FXML
    private TableColumn<Customer, Integer> colId;
    @FXML
    private TableColumn<Customer, String> colName;
    @FXML
    private TableColumn<Customer, String> colPhone;
    @FXML
    private TableColumn<Customer, String> colEmail;
    @FXML
    private TableColumn<Customer, String> colAddress;

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextArea txtAddress;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        colPhone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPhone()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        colAddress.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAddress()));

        tblCustomers.setItems(customers);
        tblCustomers.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                txtName.setText(selected.getName());
                txtPhone.setText(selected.getPhone());
                txtEmail.setText(selected.getEmail());
                txtAddress.setText(selected.getAddress());
            }
        });

        loadData();
    }

    private void loadData() {
        customers.clear();
        try {
            customers.addAll(customerDAO.findAll());
        } catch (SQLException e) {
            showError("Gagal memuat data pelanggan: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        Customer c = new Customer();
        c.setName(txtName.getText());
        c.setPhone(txtPhone.getText());
        c.setEmail(txtEmail.getText());
        c.setAddress(txtAddress.getText());

        try {
            customerDAO.insert(c);
            loadData();
            clearForm();
        } catch (SQLException e) {
            showError("Gagal menambah pelanggan: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Customer selected = tblCustomers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Pilih data pelanggan terlebih dahulu.");
            return;
        }
        selected.setName(txtName.getText());
        selected.setPhone(txtPhone.getText());
        selected.setEmail(txtEmail.getText());
        selected.setAddress(txtAddress.getText());

        try {
            customerDAO.update(selected);
            loadData();
            clearForm();
        } catch (SQLException e) {
            showError("Gagal mengubah pelanggan: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Customer selected = tblCustomers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Pilih data yang akan dihapus.");
            return;
        }
        try {
            customerDAO.delete(selected.getId());
            loadData();
            clearForm();
        } catch (SQLException e) {
            showError("Gagal menghapus pelanggan: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        tblCustomers.getSelectionModel().clearSelection();
    }

    private void clearForm() {
        txtName.clear();
        txtPhone.clear();
        txtEmail.clear();
        txtAddress.clear();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
