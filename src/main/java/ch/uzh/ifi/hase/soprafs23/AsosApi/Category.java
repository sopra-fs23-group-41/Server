package ch.uzh.ifi.hase.soprafs23.AsosApi;

public enum Category {

    JEANS(4208, "Jeans"),
    SHOES(4209, "Shoes, Boots & Sneakers"),
    ACCESSORIES(4210, "Accessories");
    private final int categoryId;
    private final String description;
    Category(int categoryId, String description){
        this.categoryId=categoryId;
        this.description=description;
    }

    public int categoryId() {
        return categoryId;
    }
    public String description(){
        return description;
    }
}
