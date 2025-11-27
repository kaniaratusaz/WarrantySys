package model;

public class Product {
    private int id;
    private String name;
    private String model;
    private int warrantyPeriodMonths;

    public Product() {}

    public Product(int id, String name, String model, int warrantyPeriodMonths) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.warrantyPeriodMonths = warrantyPeriodMonths;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getWarrantyPeriodMonths() { return warrantyPeriodMonths; }
    public void setWarrantyPeriodMonths(int warrantyPeriodMonths) { this.warrantyPeriodMonths = warrantyPeriodMonths; }

    @Override
    public String toString() {
        return name + " (" + model + ")";
    }
}
