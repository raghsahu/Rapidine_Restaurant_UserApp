package com.rapidine.rapidine.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Adapters.BillingAdapter;
import com.rapidine.rapidine.Adapters.OrderHistoryAdapter;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.BillingLisModel;
import com.rapidine.rapidine.Models.OrderHistoryData;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.NetworkUtil;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.ToastClass;
import com.rapidine.rapidine.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityOrderHistory extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private OrderHistoryAdapter mAdapter;
    private RecyclerView recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout l_no_record;
    private List<OrderHistoryData> orderHistoryList;
    private Session session;
    private String UserId;
    private ImageView iv_back;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        session = new Session(this);
        UserId = session.getUser().id;
        initView();
    }

    private void initView() {
        orderHistoryList = new ArrayList<>();
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);


        l_no_record = findViewById(R.id.no_record);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (NetworkUtil.isNetworkConnected(this)) {
            try {
                url = API.BASE_URL + "get_order_history";
                Log.e("get_order_hist URL = ", url);
                getOrderaHistoryList(url);
            } catch (NullPointerException e) {
                ToastClass.showToast(this, getString(R.string.too_slow));
            }
        } else {
            ToastClass.showToast(this, getString(R.string.no_internet_access));
        }


        mAdapter = new OrderHistoryAdapter(orderHistoryList, this);
        @SuppressLint("WrongConstant") RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(mLayoutManger);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(mAdapter);
    }

    //get orderHistory api
    private void getOrderaHistoryList(String url) {
        Utils.showDialog(ActivityOrderHistory.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
//                .addBodyParameter("restaurant_id",resId)
                .addBodyParameter("user_id", UserId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {
                            String result = jsonObject.getString("result");
                            String message = jsonObject.getString("msg");

                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    OrderHistoryData historyData = new OrderHistoryData();
                                    historyData.user_id = job.getString("user_id");
                                    historyData.order_id = job.getString("order_id");
                                    historyData.time = job.getString("time");
                                    historyData.date = job.getString("date");
                                    historyData.table_number = job.getString("table_number");
                                    historyData.total_price = job.getString("total_price");


                                    orderHistoryList.add(historyData);
                                }
                            }

                            //check arraylist size
                            if (orderHistoryList.size() == 0) {
                                swipeRefreshLayout.setVisibility(View.GONE);
                                l_no_record.setVisibility(View.VISIBLE);
                            } else {
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                                l_no_record.setVisibility(View.GONE);
                            }

                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                        Log.e("error = ", "" + error);
                    }
                });
    }

    @Override
    public void onRefresh() {
        orderHistoryList.clear();
        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
        getOrderaHistoryList(url);
    }
}
