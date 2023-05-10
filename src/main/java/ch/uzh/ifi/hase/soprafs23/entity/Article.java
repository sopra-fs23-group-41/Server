package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Article implements Serializable {

    private int productId;

    private String name;

    private float price;

    private String brandName;

    private String url;

    private String imageUrl;

    public Article() {
        super();
    }

    public Article(int productId,
                   String name,
                   float price,
                   String brandName,
                   String url,
                   String imageUrl) {
        this.productId=productId;
        this.name=name;
        this.price=price;
        this.brandName=brandName;
        this.url=url;
        this.imageUrl=imageUrl;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
