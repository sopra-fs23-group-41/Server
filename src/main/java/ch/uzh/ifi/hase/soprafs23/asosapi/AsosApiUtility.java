package ch.uzh.ifi.hase.soprafs23.asosapi;

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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AsosApiUtility {
    static Logger logger = LoggerFactory.getLogger(AsosApiUtility.class);

    private static List<Article> generateArticleList(Products products){
        List<Product> productsList;
        List<Article> articleList = new ArrayList<>();
        productsList = products.getProducts();
        for(Product product : productsList){
            articleList.add(productToArticleConverter(product));
        }
        return articleList;
    }

    private static Article productToArticleConverter(Product product){

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

        List<Article> articleList = generateArticleList(products1);
        articleList.addAll(generateArticleList(products2));
        Collections.shuffle(articleList);
        return articleList;
    }

    private static Response createRequest(int limit, Category category) throws UnirestException {
        String secret = System.getenv("API_KEY");
        if(secret == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Api key not found! ");
        }
        logger.debug("secret with value: {} retrieved!", secret);
        String baseUrl = "https://asos2.p.rapidapi.com/";
        HttpResponse<JsonNode> response1 = Unirest.get(baseUrl + "products/v2/list?store=US&offset=0&categoryId="+ category.getCategoryIdMen() +"&limit=" + limit/2 + "&country=US&sort=freshness&currency=USD&sizeSchema=US&lang=en-US")
                .header("X-RapidAPI-Key", secret)
                .header("X-RapidAPI-Host","asos2.p.rapidapi.com")
                .asJson();
        HttpResponse<JsonNode> response2 = Unirest.get(baseUrl + "products/v2/list?store=US&offset=0&categoryId="+ category.getCategoryIdWomen() +"&limit=" + limit/2 + "&country=US&sort=freshness&currency=USD&sizeSchema=US&lang=en-US")
                .header("X-RapidAPI-Key", secret)
                .header("X-RapidAPI-Host","asos2.p.rapidapi.com")
                .asJson();

        return new Response(response1, response2);
    }

    // subclass used to store two different HttpResponse together
    public record Response(HttpResponse<JsonNode> response1, HttpResponse<JsonNode> response2) {
    }


}
