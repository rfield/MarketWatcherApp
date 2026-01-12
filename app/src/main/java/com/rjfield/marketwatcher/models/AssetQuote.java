package com.rjfield.marketwatcher.models;

public class AssetQuote extends Asset {

    private Double price;

    public AssetQuote(String t, Double p) {
        this.ticker = t;
        this.price = p;
    }

    public AssetQuote(String an, String t, Double p, Double h) {
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
