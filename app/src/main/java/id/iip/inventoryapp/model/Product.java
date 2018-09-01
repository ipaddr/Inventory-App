package id.iip.inventoryapp.model;

public class Product {

    /** name of the product */
    private String mName;
    /** quantity of the product */
    private int mQuantity;
    /** price per item of the product */
    private double mPrice;

    /**
     * Constructor with related param
     * @param name
     * @param quantity
     * @param price
     */
    public Product(String name, int quantity, double price) {
        this.mName = name;
        this.mQuantity = quantity;
        this.mPrice = price;
    }

    public String getName() {
        return mName;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public double getPrice() {
        return mPrice;
    }
}
