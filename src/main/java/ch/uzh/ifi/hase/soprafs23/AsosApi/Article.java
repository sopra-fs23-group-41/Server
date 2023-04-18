package ch.uzh.ifi.hase.soprafs23.AsosApi;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class Article {

    private int id;
    private String name;
    private String description;

    private String gender;

    private int productCode;

    private Brand brand; //hold a field brand id, name, description

    private Media media; // media holds a field images with url etc.

    private Price price; // holds a field current with current price in dollar

    public Article(){
        super();
    }
    public Article(int id, String name, String description, String gender, int productCode, Brand brand, Media media, Price price){
        this.id = id;
        this.name = name;
        this.description = description;
        this.gender = gender;
        this.productCode = productCode;
        this.brand = brand;
        this.media = media;
        this.price = price;
    }
    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", gender='" + gender + '\'' +
                ", productCode=" + productCode +
                ", brand=" + brand +
                ", media=" + media +
                ", price=" + price +
                '}';
    }
}
