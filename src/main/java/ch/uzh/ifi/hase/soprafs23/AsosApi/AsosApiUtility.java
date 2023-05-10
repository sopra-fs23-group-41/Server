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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AsosApiUtility {
    static Logger logger = LoggerFactory.getLogger(AsosApiUtility.class);

    private static List<Article> GenerateArticleList(Products products){
        List<Product> productsList;
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

        Response responses = createRequest(limit, category);

        HttpResponse<JsonNode> jsonNodeHttpResponse1 = responses.response1();
        HttpResponse<JsonNode> jsonNodeHttpResponse2 = responses.response2();

        String jsonString1 = jsonNodeHttpResponse1.getBody().toString();
        String jsonString2 = jsonNodeHttpResponse2.getBody().toString();

        Products products1 = mapper.readValue(jsonString1, Products.class);
        Products products2 = mapper.readValue(jsonString2, Products.class);

        List<Article> articleList = GenerateArticleList(products1);
        articleList.addAll(GenerateArticleList(products2));
        Collections.shuffle(articleList);


        return articleList;
    }

    private static Response createRequest(int limit, Category category) throws UnirestException {
        String secret = System.getenv("API_KEY");
        logger.info("secret with value: " + secret + " retrieved!");
        String BaseUrl = "https://asos2.p.rapidapi.com/";
        HttpResponse<JsonNode> response1 = Unirest.get(BaseUrl + "products/v2/list?store=US&offset=0&categoryId="+ category.getCategoryIdMen() +"&limit=" + limit/2 + "&country=US&sort=freshness&currency=USD&sizeSchema=US&lang=en-US")
                .header("X-RapidAPI-Key", "6b13e3c424mshd89ea2f0812d131p195a3djsn1e4288e93cca")
                .header("X-RapidAPI-Host","asos2.p.rapidapi.com")
                .asJson();
        HttpResponse<JsonNode> response2 = Unirest.get(BaseUrl + "products/v2/list?store=US&offset=0&categoryId="+ category.getCategoryIdWomen() +"&limit=" + limit/2 + "&country=US&sort=freshness&currency=USD&sizeSchema=US&lang=en-US")
                .header("X-RapidAPI-Key", "6b13e3c424mshd89ea2f0812d131p195a3djsn1e4288e93cca")
                .header("X-RapidAPI-Host","asos2.p.rapidapi.com")
                .asJson();

        return new Response(response1, response2);
    }

    // subclass used to store two different HttpResponse together
    public record Response(HttpResponse<JsonNode> response1, HttpResponse<JsonNode> response2) {
    }


}
