package ch.uzh.ifi.hase.soprafs23.AsosApi;

import ch.uzh.ifi.hase.soprafs23.entity.Article;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;
import java.util.List;

public class AsosApiUtility {

    private static List<Article> GenerateArticleList(Products products){
        List<Product> productsList = new ArrayList<>();
        List<Article> articleList = new ArrayList<>();
        productsList = products.getProducts();
        for(Product product : productsList){
            articleList.add(ProductToArticleConverter(product));
        }
        return articleList;
    }

    private static Article ProductToArticleConverter(Product product){

        return new Article(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getBrandName(),
                product.getUrl(),
                product.getImageUrl());
    }

    public static List<Article> getArticles(int limit, Category category) throws JsonProcessingException, UnirestException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        HttpResponse<JsonNode> jsonNodeHttpResponse = createRequest(limit, category);
        String jsonString = jsonNodeHttpResponse.getBody().toString();

        Products products = mapper.readValue(jsonString, Products.class);

        List<Article> articleList = GenerateArticleList(products);

        return articleList;
    }

    private static HttpResponse<JsonNode> createRequest(int limit, Category category) throws UnirestException {
        String secret = System.getenv("secret");
        String BaseUrl = "https://asos2.p.rapidapi.com/";
        HttpResponse<JsonNode> response = Unirest.get(BaseUrl + "products/v2/list?store=US&offset=0&categoryId="+ category.categoryId() +"&limit=" + limit + "&country=US&sort=freshness&currency=USD&sizeSchema=US&lang=en-US")
                .header("X-RapidAPI-Key", secret)
                .header("X-RapidAPI-Host","asos2.p.rapidapi.com")
                .asJson();
        return response;
    }


}
