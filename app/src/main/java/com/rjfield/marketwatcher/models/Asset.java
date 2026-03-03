package com.rjfield.marketwatcher.models;

public class Asset {
    protected String accountName;
    protected String ticker;
    protected Double holdingAmount;

    public Asset() {
    }

    public Asset(String accountName, String ticker, Double holdingAmount) {
        this.accountName = accountName;
        this.ticker = ticker;
        this.holdingAmount = holdingAmount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Double getHoldingAmount() {
        return holdingAmount;
    }

    public void setHoldingAmount(Double holdingAmount) {
        this.holdingAmount = holdingAmount;
    }
}
