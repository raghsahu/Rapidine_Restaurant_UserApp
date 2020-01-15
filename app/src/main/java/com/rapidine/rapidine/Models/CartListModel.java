package com.rapidine.rapidine.Models;

import java.io.Serializable;
import java.util.HashMap;

public class CartListModel extends HashMap<String, String> implements Serializable {
    public String id;
    public String menues_id;
    public String category_id;
    public String user_id;
    public String itemName;
    public int quantity;
    public String total;
    public int ItemTotal;
    public int price;
    public String offer_price;
    public String type;
    public String image;
    public String stock_status;
}
