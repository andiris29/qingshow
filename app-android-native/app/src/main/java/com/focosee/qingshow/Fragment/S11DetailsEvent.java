package com.focosee.qingshow.Fragment;

/**
 * Created by Administrator on 2015/3/17.
 */
public class S11DetailsEvent {

    private int quantity;
    private String price;
    private String selectedItemSkuId;
    private boolean exists = false;

    public S11DetailsEvent(int quantity, String price, String selectedItemSkuId, boolean exists) {
        this.quantity = quantity;
        this.price = price;
        this.selectedItemSkuId = selectedItemSkuId;
        this.exists = exists;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getSelectedItemSkuId() {
        return selectedItemSkuId;
    }

    public boolean isExists() {
        return exists;
    }
}
