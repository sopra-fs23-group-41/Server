package ch.uzh.ifi.hase.soprafs23.asosapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
    @JsonProperty("id")
    private int productId;
    private String name;
    private Price price;
    private String colour;
    private String brandName;
    private int productCode;
    private String url;
    private String imageUrl;

    public Product() {
        super();
    }

    public Product(int productId,
                   String name,
                   Price price,
                   String color,
                   String brandName,
                   int productCode,
                   String url,
                   String imageUrl) {
        this.productId=productId;
        this.name=name;
        this.price=price;
        this.colour=color;
        this.brandName=brandName;
        this.productCode=productCode;
        this.url=url;
        this.imageUrl=imageUrl;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", color='" + colour + '\'' +
                ", brandName='" + brandName + '\'' +
                ", productCode=" + productCode +
                ", url='" + url + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public String getBrandName() {
        return brandName;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public float getPrice(){
        return this.price.getPrice();
    }

    private static class Price{
        private Current current;

        public Price() {
            super();
        }

        public Price(Current current) {
            this.current = current;
        }

        @Override
        public String toString() {
            return "Price{" +
                    "current=" + current +
                    '}';
        }

        public float getPrice() {
            return this.current.getValue();
        }

        private static class Current{
            private float value;
            private String text;

            public Current() {
                super();
            }

            public Current(float value, String text) {
                this.value = value;
                this.text = text;
            }

            @Override
            public String toString() {
                return "Current{" +
                        "value=" + value +
                        ", text='" + text + '\'' +
                        '}';
            }

            public float getValue() {
                return this.value;
            }
        }
    }
}

