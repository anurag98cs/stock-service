package com.stock.stockservice.resource;

import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.github.kevinsawicki.stocks.DateUtils;
import com.github.kevinsawicki.stocks.StockQuoteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;



import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Calendar;

@RestController
@RequestMapping("/rest/stock")
public class StockResource {
    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/{username}")
    public List<Quote> getStock(@PathVariable("username") final String userName){

//        ResponseEntity<List<String>> quoteResponse = restTemplate.exchange("http://localhost:8300/rest/db/" + userName, HttpMethod.GET,
//                null, new ParameterizedTypeReference<List<String>>() {});

        ResponseEntity<List<String>> quoteResponse = restTemplate.exchange("http://db-service:8300/rest/db/" + userName, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {});

        List<String> quotes = quoteResponse.getBody();
        return quotes
                .stream()
                .map(quote -> {
                    //Stock stock = getStockPrice(quote);
                        return new Quote(quote, getStockPrice(quote));
                 })
                .collect(Collectors.toList());
    }

    private float getStockPrice(String quote) {
        StockQuoteRequest buyRequest = new StockQuoteRequest();
        buyRequest.setStartDate(DateUtils.today()).setEndDate(DateUtils.today())
                .setSymbol(quote);

        try {
            //if (!buyRequest.next())
                //throw new InvalidBuyDateException();
        } catch (HttpRequestException e) {
            //throw e.getCause();
        }

        float price = buyRequest.getOpen();
        if (price <= 0.0F)
            price = buyRequest.getClose();
        return price;

    }

//    private float getStockPrice(String quote){
//        try {
//            StockQuoteRequest request = new StockQuoteRequest();
//            request.setSymbol(quote);
//            return request.getClose();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//            //return new Stock(quote);
//        }
//    }

//    private Stock getStockPrice(String quote){
//        try {
//            return YahooFinance.get(quote);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new Stock(quote);
//        }
//    }


    private class Quote{
        private String quote;
        private float price;
        public Quote(String quote, float price){
            this.quote = quote;
        }

        public String getQuote() {
            return quote;
        }

        public void setQuote(String quote) {
            this.quote = quote;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }
    }
}
