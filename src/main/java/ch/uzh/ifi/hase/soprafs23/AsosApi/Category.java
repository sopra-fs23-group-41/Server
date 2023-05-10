package ch.uzh.ifi.hase.soprafs23.AsosApi;

public enum Category {

    JEANS(4208, 3630, "Jeans"),
    SNEAKERS(5775, 6456,"Sneakers"),
    JACKETS(3606, 2641, "Jackets"),
    HOODIES(5668, 11321, "Hoodies"),
    JEWELRY(5034, 4175, "Jewelry"),
    ACCESSORIES(4210, 4174, "Accessories"),
    T_SHIRTS(7616, 4718, "T-Shirts & Tanks");


    private final int categoryIdMen;
    private final int categoryIdWomen;
    private final String description;

    Category(int categoryIdMen, int categoryIdWomen, String description){
        this.categoryIdMen=categoryIdMen;
        this.categoryIdWomen=categoryIdWomen;
        this.description=description;
    }

    public int getCategoryIdMen() {
        return categoryIdMen;
    }
    public int getCategoryIdWomen(){return categoryIdWomen;}
    public String description(){
        return description;
    }
}
