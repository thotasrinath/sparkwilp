package org.wilp.vo;

import java.io.Serializable;

/**
 * Created by thota on 2/27/17.
 * <p>
 * <p>
 * CREATE TABLE order_products (
 * order_product_id serial PRIMARY KEY,
 * order_id int,
 * product_id int,
 * FOREIGN KEY (order_id) REFERENCES orders (order_id),
 * FOREIGN KEY (product_id) REFERENCES products (product_id)
 * <p>
 * );
 */
public class OrderProductsVO implements Serializable {

    private long orderProductId;
    private OrdersVO order;
    private ProductsVO product;

    public OrdersVO getOrder() {
        return order;
    }

    public void setOrder(OrdersVO order) {
        this.order = order;
    }

    public ProductsVO getProduct() {
        return product;
    }

    public void setProduct(ProductsVO product) {
        this.product = product;
    }

    public long getOrderProductId() {
        return orderProductId;
    }

    public void setOrderProductId(long orderProductId) {
        this.orderProductId = orderProductId;
    }






}
