package controller;

import dao.ProductDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Product;

import java.sql.SQLException;

public class ProductController {

    @FXML
    private TableView<Product> tblProducts;
    @FXML
    private TableColumn<Product, Integer> colId;
    @FXML
    private TableColumn<Product, String> colName;
    @FXML
    private TableColumn<Product, String> colModel;
    @FXML
    private TableColumn<Product, Integer> colWarranty;

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtModel;
    @FXML
    private TextField txtWarranty;

    private final ProductDAO productDAO = new ProductDAO();
    private final ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        colModel.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getModel()));
        colWarranty.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getWarrantyPeriodMonths()).asObject());

        tblProducts.setItems(products);
        tblProducts.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                txtName.setText(selected.getName());
                txtModel.setText(selected.getModel());
                txtWarranty.setText(String.valueOf(selected.getWarrantyPeriodMonths()));
            }
        });

        loadData();
    }

    private void loadData() {
        products.clear();
        try {
            products.addAll(productDAO.findAll());
        } catch (SQLException e) {
            showError("Gagal memuat data produk: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        Product p = new Product();
        p.setName(txtName.getText());
        p.setModel(txtModel.getText());
        try {
            p.setWarrantyPeriodMonths(Integer.parseInt(txtWarranty.getText()));
        } catch (NumberFormatException ex) {
            showError("Masa garansi harus berupa angka.");
            return;
        }

        try {
            productDAO.insert(p);
            loadData();
            clearForm();
        } catch (SQLException e) {
            showError("Gagal menambah produk: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Product selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Pilih data produk terlebih dahulu.");
            return;
        }
        selected.setName(txtName.getText());
        selected.setModel(txtModel.getText());
        try {
            selected.setWarrantyPeriodMonths(Integer.parseInt(txtWarranty.getText()));
        } catch (NumberFormatException ex) {
            showError("Masa garansi harus berupa angka.");
            return;
        }

        try {
            productDAO.update(selected);
            loadData();
            clearForm();
        } catch (SQLException e) {
            showError("Gagal mengubah produk: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Product selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Pilih data yang akan dihapus.");
            return;
        }
        try {
            productDAO.delete(selected.getId());
            loadData();
            clearForm();
        } catch (SQLException e) {
            showError("Gagal menghapus produk: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        tblProducts.getSelectionModel().clearSelection();
    }

    private void clearForm() {
        txtName.clear();
        txtModel.clear();
        txtWarranty.clear();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
