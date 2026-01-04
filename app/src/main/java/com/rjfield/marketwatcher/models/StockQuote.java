package com.rjfield.marketwatcher.models;

public class StockQuote {

    private String ticker;
    private Double price;

    public StockQuote(String t, Double p) {
        this.ticker = t;
        this.price = p;
    }
    public String getTicker() {
        return ticker;
    }

    public Double getPrice() {
        return price;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
