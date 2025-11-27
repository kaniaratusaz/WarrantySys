package dao;

import model.Customer;
import model.Product;
import model.WarrantyClaim;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarrantyClaimDAO {

    public void insert(WarrantyClaim c) throws SQLException {
        String sql = "INSERT INTO warranty_claims(claim_number, customer_id, product_id, serial_number, " +
                     "purchase_date, claim_date, status, issue_description, resolution_notes) " +
                     "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getClaimNumber());
            ps.setInt(2, c.getCustomer().getId());
            ps.setInt(3, c.getProduct().getId());
            ps.setString(4, c.getSerialNumber());
            ps.setDate(5, c.getPurchaseDate() != null ? Date.valueOf(c.getPurchaseDate()) : null);
            ps.setDate(6, Date.valueOf(c.getClaimDate()));
            ps.setString(7, c.getStatus());
            ps.setString(8, c.getIssueDescription());
            ps.setString(9, c.getResolutionNotes());
            ps.executeUpdate();
        }
    }

    public void update(WarrantyClaim c) throws SQLException {
        String sql = "UPDATE warranty_claims SET claim_number=?, customer_id=?, product_id=?, serial_number=?, " +
                     "purchase_date=?, claim_date=?, status=?, issue_description=?, resolution_notes=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getClaimNumber());
            ps.setInt(2, c.getCustomer().getId());
            ps.setInt(3, c.getProduct().getId());
            ps.setString(4, c.getSerialNumber());
            ps.setDate(5, c.getPurchaseDate() != null ? Date.valueOf(c.getPurchaseDate()) : null);
            ps.setDate(6, Date.valueOf(c.getClaimDate()));
            ps.setString(7, c.getStatus());
            ps.setString(8, c.getIssueDescription());
            ps.setString(9, c.getResolutionNotes());
            ps.setInt(10, c.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM warranty_claims WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<WarrantyClaim> findAll() throws SQLException {
        List<WarrantyClaim> list = new ArrayList<>();
        String sql = "SELECT wc.*, c.name AS customer_name, c.phone, c.email, c.address, " +
                     "p.name AS product_name, p.model, p.warranty_period_months " +
                     "FROM warranty_claims wc " +
                     "JOIN customers c ON wc.customer_id = c.id " +
                     "JOIN products p ON wc.product_id = p.id";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Customer cust = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                );

                Product prod = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("model"),
                        rs.getInt("warranty_period_months")
                );

                WarrantyClaim wc = new WarrantyClaim();
                wc.setId(rs.getInt("id"));
                wc.setClaimNumber(rs.getString("claim_number"));
                wc.setCustomer(cust);
                wc.setProduct(prod);
                wc.setSerialNumber(rs.getString("serial_number"));

                Date pur = rs.getDate("purchase_date");
                if (pur != null) {
                    wc.setPurchaseDate(pur.toLocalDate());
                }
                wc.setClaimDate(rs.getDate("claim_date").toLocalDate());
                wc.setStatus(rs.getString("status"));
                wc.setIssueDescription(rs.getString("issue_description"));
                wc.setResolutionNotes(rs.getString("resolution_notes"));

                list.add(wc);
            }
        }
        return list;
    }
}
