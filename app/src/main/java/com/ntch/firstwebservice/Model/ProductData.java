package com.ntch.firstwebservice.Model;

public class ProductData {


    public  String productName;
    public String mrpPrice;
    public String productPrice;
    public String stockAvl;
   public String imageS;

    public ProductData(String productName, String mrpPrice, String productPrice, String stockAvl, String imageS) {
        this.productName = productName;
        this.mrpPrice = mrpPrice;
        this.productPrice = productPrice;
        this.stockAvl = stockAvl;
        this.imageS = imageS;
    }
//    public ProductData(String productName, String productPrice, String stockAvl, String imageS)
//    {
//        this.productName = productName;
//        this.productPrice = productPrice;
//        this.stockAvl = stockAvl;
//        this.imageS = imageS;
//    }
    public ProductData(String productName, String mrpPrice, String productPrice, String imageS) {
        this.productName = productName;
        this.mrpPrice = mrpPrice;
        this.productPrice = productPrice;
        this.imageS = imageS;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMrpPrice() {
        return mrpPrice;
    }

    public void setMrpPrice(String mrpPrice) {
        this.mrpPrice = mrpPrice;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getStockAvl() {
        return stockAvl;
    }

    public void setStockAvl(String stockAvl) {
        this.stockAvl = stockAvl;
    }

    public String getImageS() {
        return imageS;
    }

    public void setImageS(String imageS) {
        this.imageS = imageS;
    }
}
