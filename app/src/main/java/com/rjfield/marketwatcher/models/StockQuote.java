package com.rjfield.marketwatcher.models;

public class StockQuote {

    private String accountName;

    private String ticker;
    private Double price;

    private Double holdingAmount;

    public StockQuote(String t, Double p) {
        this.ticker = t;
        this.price = p;
    }

    public StockQuote(String an, String t, Double p, Double h) {
        this.accountName = an;
        this.ticker = t;
        this.price = p;
        this.holdingAmount = h;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Double getHoldingAmount() {
        return holdingAmount;
    }

    public void setHoldingAmount(Double holdingAmount) {
        this.holdingAmount = holdingAmount;
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
