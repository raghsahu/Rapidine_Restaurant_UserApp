package com.rapidine.rapidine.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
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

public class SearchItemActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private List<SearchProductList> searchProductLists;
    private SearchProductList searchProductList;
    private SearchProductAdapter searchProductAdapter;
    RecyclerView recycler_view_search_item;
    private SwipeRefreshLayout swipeToRefresh;
    private LinearLayout ll_no_data_found;
    EditText et_search_item;
    Editable s;
    public static TextView tv_cart_badge_search;
    ImageView iv_back, iv_addto_cart;
    String resId;
    String UserId;
    private Session session;


    List<CartListModel> cartListModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        searchProductLists = new ArrayList<>();
        et_search_item = findViewById(R.id.et_search_item);
        iv_back = findViewById(R.id.iv_back);
        iv_addto_cart = findViewById(R.id.iv_addto_cart);
        tv_cart_badge_search = findViewById(R.id.tv_cart_badge_search);
        session = new Session(this);
        UserId = session.getUser().id;
        cartListModels = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            resId = intent.getStringExtra("restId");
            //Log.e("restId", resId);
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        iv_addto_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        SearchItemActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        recycler_view_search_item = findViewById(R.id.recycler_view_search_item);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        ll_no_data_found = findViewById(R.id.ll_no_data_found);

        swipeToRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeToRefresh.setOnRefreshListener(this);


        //s = et_search_item.getText();
        et_search_item.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });


        if (NetworkUtil.isNetworkConnected(this)) {
            try {

                String urlCartList = API.BASE_URL + "Cart_List";
                cartList(urlCartList);


                //searchProductLists.clear();
                String url = API.BASE_URL + "Item_List";
                searchProduct(url);
            } catch (NullPointerException e) {
                Toast.makeText(this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
        }

    }

    private void filter(String s) {
        ArrayList<SearchProductList> temp = new ArrayList();
        for (SearchProductList smodel : searchProductLists) {
            //use .toLowerCase() for better matches
            if (smodel.name.toLowerCase().startsWith(s.toLowerCase())) {
                temp.add(smodel);
            }
        }
        if (temp!=null){
            //update recyclerview
            try {
                searchProductAdapter.updateList(temp);
            }catch (Exception e){

            }


        }

    }


    private void searchProduct(String url) {
        Utils.showDialog(SearchItemActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                /*.addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .addHeaders("token", "1234")*/
                .addBodyParameter("restaurant_id", resId)
                .addBodyParameter("category_id", "")
                .addBodyParameter("type", "")

                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {
                            Log.e("SearchItem", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);

                                    SearchProductList searchProductList = new SearchProductList();
                                    searchProductList.id = job.getString("id");
                                    searchProductList.restaurant_id = job.getString("restaurant_id");
                                    searchProductList.category_id = job.getString("category_id");
                                    searchProductList.name = job.getString("name");
                                    searchProductList.price = job.getString("price");
                                    searchProductList.offer_price = job.getString("offer_price");
                                    searchProductList.type = job.getString("type");
                                    searchProductList.image = job.getString("image");

                                    searchProductLists.add(searchProductList);

                                }
                                searchProductAdapter = new SearchProductAdapter(searchProductLists, SearchItemActivity.this);
                                RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(SearchItemActivity.this);
                                recycler_view_search_item.setLayoutManager(mLayoutManger);
                                recycler_view_search_item.setLayoutManager(new LinearLayoutManager(SearchItemActivity.this, LinearLayoutManager.VERTICAL, false));
                                recycler_view_search_item.setItemAnimator(new DefaultItemAnimator());
                                recycler_view_search_item.setAdapter(searchProductAdapter);
//
                                searchProductAdapter.notifyDataSetChanged();
                            }

                            //check arraylist size
                            if (searchProductLists.size() == 0) {
                                swipeToRefresh.setVisibility(View.GONE);
                                ll_no_data_found.setVisibility(View.VISIBLE);
                            } else {
                                swipeToRefresh.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            Utils.dismissDialog();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Utils.dismissDialog();
                        Log.e("error = ", "" + error);
                    }
                });
    }


    private void cartList(String url) {
        cartListModels.clear();
        Utils.showDialog(SearchItemActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                /*.addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .addHeaders("token", "1234")*/
                .addBodyParameter("user_id", UserId)
//                .addBodyParameter("category_id","1")
//                .addBodyParameter("type","")

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

                                    cartListModels.add(cartListModel);


                                }

                                if (cartListModels.equals("")) {
                                    tv_cart_badge_search.setVisibility(View.GONE);
                                } else {
                                    tv_cart_badge_search.setVisibility(View.VISIBLE);
                                    tv_cart_badge_search.setText(String.valueOf(cartListModels.size()));
//                                    tv_cart_badge.setText(cartListModels.size());
                                }
                                Log.e("size", "Menu Activity=" + cartListModels.size());

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
    public void onRefresh() {
        swipeToRefresh.setRefreshing(false);

        if (NetworkUtil.isNetworkConnected(this)) {
            try {
                searchProductLists.clear();
                String url = API.BASE_URL + "Item_List";
                searchProduct(url);
            } catch (NullPointerException e) {
                Toast.makeText(this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
        }
    }
}
