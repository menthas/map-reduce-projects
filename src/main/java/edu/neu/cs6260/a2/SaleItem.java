package edu.neu.cs6260.a2;

/**
 * Class that represents a single sale item
 * @author behrooz, sahil
 */
public class SaleItem {
    /**
     * Category of the item
     */
    private String category;

    /**
     * price of the item
     */
    private double price;

    /**
     * initiate with values
     * @param category
     * @param price
     */
    public SaleItem(String category, double price) {
        setCategory(category);
        setPrice(price);
    }

    /**
     * initiate to be populated later
     */
    public SaleItem() {}

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
