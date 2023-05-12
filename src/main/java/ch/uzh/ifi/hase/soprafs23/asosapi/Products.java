package ch.uzh.ifi.hase.soprafs23.asosapi;


import java.util.List;

public class Products {

    private String categoryName;

    private List<Product> products;

    public Products() {
        super();
    }

    public Products(String categoryName, List<Product> products) {
        this.categoryName = categoryName;
        this.products=products;
    }

    public List<Product> getProducts() {
        return products;
    }
}
