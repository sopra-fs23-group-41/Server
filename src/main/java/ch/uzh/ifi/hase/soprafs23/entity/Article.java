package ch.uzh.ifi.hase.soprafs23.entity;

public class Article {
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

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }


}
