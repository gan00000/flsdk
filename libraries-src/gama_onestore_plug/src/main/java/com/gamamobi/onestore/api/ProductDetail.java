//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gamamobi.onestore.api;

public class ProductDetail {
    private String productId;
    private String type;
    private String price;
    private String title;

    private ProductDetail() {
    }

    private ProductDetail(ProductDetail other) {
        this.productId = other.productId;
        this.type = other.type;
        this.price = other.price;
        this.title = other.title;
    }

    public static ProductDetail.Builder builder() {
        return new ProductDetail.Builder();
    }

    public String getProductId() {
        return this.productId;
    }

    public String getType() {
        return this.type;
    }

    public String getPrice() {
        return this.price;
    }

    public String getTitle() {
        return this.title;
    }

    public String toString() {
        return "\nproductId:" + this.productId + "\ntype " + this.type + "\nprice " + this.price + "\ntitle " + this.title;
    }

    public static class Builder {
        final ProductDetail productDetail = new ProductDetail();

        public Builder() {
        }

        public ProductDetail.Builder productId(String productId) {
            this.productDetail.productId = productId;
            return this;
        }

        public ProductDetail.Builder type(String type) {
            this.productDetail.type = type;
            return this;
        }

        public ProductDetail.Builder price(String price) {
            this.productDetail.price = price;
            return this;
        }

        public ProductDetail.Builder title(String title) {
            this.productDetail.title = title;
            return this;
        }

        public ProductDetail build() {
            return new ProductDetail(this.productDetail);
        }
    }
}
