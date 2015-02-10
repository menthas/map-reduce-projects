package edu.neu.cs6260.a2;

/**
 * Created by menthas on 2/10/15.
 */
public class SaleItem {
    private String category;
    private double price;

    public SaleItem(String category, double price) {
        setCategory(category);
        setPrice(price);
    }

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
