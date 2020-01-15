package com.rapidine.rapidine.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Adapters.CartAdapter;
import com.rapidine.rapidine.Adapters.CartAdapter.*;
import com.rapidine.rapidine.Adapters.SearchProductAdapter;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.CartListModel;
import com.rapidine.rapidine.Models.SearchProductList;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.NetworkUtil;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    TextView tv_count, tv_discount, tv_cart_total_price;
    public TextView tv_finish;
    public static TextView tv_order;
    List<CartListModel> cartListModels;
    private CartListModel cartListModel;
    private CartAdapter cartAdapter;
    private RecyclerView recycler_view_cart;
    private SwipeRefreshLayout swipeToRefresh;
    private LinearLayout ll_no_data_found;
    ImageView iv_back, iv_minus, iv_plus;
    int count;
    String UserId;
    private Session session;
    TextView tv_item_total;
    int Total;

    private String resId;
    private String tableNo;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        session = new Session(this);
        UserId = session.getUser().id;
        Log.e("userId", "" + UserId);


        pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();

        resId = pref.getString("ResId", null);
        tableNo = pref.getString("tableNo", null);


        cartListModels = new ArrayList<>();
        recycler_view_cart = findViewById(R.id.recycler_view_cart);
        tv_order = findViewById(R.id.tv_order);
        tv_item_total = findViewById(R.id.tv_item_total);
        tv_discount = findViewById(R.id.tv_discount);
        //tv_cart_total_price = findViewById(R.id.tv_cart_total_price);
        //tv_finish = findViewById(R.id.tv_finish);
        iv_back = findViewById(R.id.iv_back);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        ll_no_data_found = findViewById(R.id.ll_no_data_found);

        swipeToRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeToRefresh.setOnRefreshListener(this);


/*        tv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });*/


       /* tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Intent intent = new Intent(CartActivity.this, BillingActivity.class);
                intent.putExtra("cartModel", cartListModel);
                //Log.e("CartActivity","ItemTotal"+cartListModel.ItemTotal);
                startActivity(intent);*//*
                cartAdapter.callFinish();
            }
        });*/
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CartActivity.this, MenuActivity.class);
                intent.putExtra("scan", "scan");
                intent.putExtra("restaurantNumber", resId);
                intent.putExtra("tebleNo", tableNo);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                startActivity(intent);
                finish();

            }
        });

        swipeToRefresh.setRefreshing(false);

        if (NetworkUtil.isNetworkConnected(this)) {
            try {
                String url = API.BASE_URL + "Cart_List";
                cartList(url);
            } catch (NullPointerException e) {
                Toast.makeText(this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
        }


        //tv_item_total.setText(cartListModel.ItemTotal);

        //String ItemsTotal = tv_item_total.getText().toString();
        // Log.e("ItemTotal", "" + ItemsTotal);
        // int Total = Integer.parseInt(ItemsTotal + tv_discount.getText().toString());
        //tv_cart_total_price.setText(String.valueOf(Total));
    }

//    public int calculateMealTotal(){
//        int mealTotal = 0;
//        for(CartListModel order : cartListModels){
//            mealTotal += order.price * order.quantity;
//        }
//        return mealTotal;
//    }


    private void cartList(String url) {
        Utils.showDialog(CartActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", UserId)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {
                            Log.e("CartList Res", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    CartListModel cartListModel = new CartListModel();
                                    cartListModel.menues_id = job.getString("menues_id");
                                    cartListModel.id = job.getString("id");
                                    cartListModel.category_id = job.getString("category_id");
                                    cartListModel.user_id = job.getString("user_id");
                                    cartListModel.itemName = job.getString("name");
                                    cartListModel.price = Integer.parseInt(job.getString("price"));
                                    cartListModel.offer_price = job.getString("offer_price");
                                    cartListModel.type = job.getString("type");
                                    cartListModel.image = job.getString("image");
                                    cartListModel.stock_status = job.getString("stock_status");
                                    cartListModel.quantity = job.getInt("quantity");
                                    cartListModel.total = job.getString("total");

                                    cartListModels.add(cartListModel);
                                }
                                cartAdapter = new CartAdapter(cartListModels, CartActivity.this);
                                RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(CartActivity.this);
                                recycler_view_cart.setLayoutManager(mLayoutManger);
                                recycler_view_cart.setLayoutManager(new LinearLayoutManager(CartActivity.this, LinearLayoutManager.VERTICAL, false));
                                recycler_view_cart.setItemAnimator(new DefaultItemAnimator());
                                recycler_view_cart.setAdapter(cartAdapter);

                                cartAdapter.notifyDataSetChanged();
                            }

                            //check arraylist size
                            if (cartListModels.size() == 0) {
                                swipeToRefresh.setVisibility(View.GONE);
                                ll_no_data_found.setVisibility(View.VISIBLE);
                            } else {
                                swipeToRefresh.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("cartList error = ", "" + error);
                    }
                });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CartActivity.this, MenuActivity.class);
        intent.putExtra("scan", "scan");
        intent.putExtra("restaurantNumber", resId);
        intent.putExtra("tebleNo", tableNo);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
        startActivity(intent);
        finish();
    }

    @Override
    public void onRefresh() {
        swipeToRefresh.setRefreshing(false);

        if (NetworkUtil.isNetworkConnected(this)) {
            try {
                cartListModels.clear();
                String url = API.BASE_URL + "Cart_List";
                cartList(url);
            } catch (NullPointerException e) {
                Toast.makeText(this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
        }
    }
}
