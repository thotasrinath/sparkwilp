package org.wilp.vo;

import java.io.Serializable;

/**
 * Created by thota on 2/27/17.
 * <p>
 * <p>
 * <p>
 * CREATE TABLE orders (
 * order_id serial PRIMARY KEY,
 * order_date date
 * );
 */
public class OrdersVO implements Serializable {


    private long orderId;
    //private Date orderData;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }




   /* public Date getOrderData() {
        return orderData;
    }

    public void setOrderData(Date orderData) {
        this.orderData = orderData;
    }
*/

}
