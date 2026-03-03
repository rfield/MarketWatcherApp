package com.rjfield.marketwatcher.models;

public class AssetQuote extends Asset {

    private Double price;
    private Double priceChange;

    public AssetQuote() {
        super();
        this.price = 0.0;
        this.priceChange = 0.0;
    }

    public AssetQuote(String t, Double p, Double pc) {
        super(null, t, null);
        this.price = p;
        this.priceChange = pc;
    }

    public AssetQuote(String an, String t, Double p, Double pc, Double h) {
        super(an, t, h);
        this.price = p;
        this.priceChange = pc;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(Double priceChange) {
        this.priceChange = priceChange;
    }
}
