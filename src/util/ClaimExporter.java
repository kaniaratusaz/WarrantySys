package util;

import model.WarrantyClaim;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ClaimExporter {

    public void exportToCsv(List<WarrantyClaim> claims, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("ClaimNumber,Customer,Product,Status,ClaimDate\n");
            for (WarrantyClaim c : claims) {
                writer.write(c.getClaimNumber() + "," +
                        (c.getCustomer() != null ? c.getCustomer().getName() : "") + "," +
                        (c.getProduct() != null ? c.getProduct().getName() : "") + "," +
                        c.getStatus() + "," +
                        c.getClaimDate() + "\n");
            }
        }
    }
}
