package org.wilp.vo;

import java.io.Serializable;

/**
 * Created by thota on 2/27/17.
 * <p>
 * <p>
 * CREATE TABLE products (
 * product_id serial PRIMARY KEY,
 * product_name varchar(200) NOT NULL,
 * sku varchar(80),
 * price float(2)
 * );
 */
public class ProductsVO implements Serializable {


    private long productId;
    private String productName;
    private String sku;
    private float price;


    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }


}
