package com.rapidine.rapidine.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuAdapter;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.tabs.TabLayout;
import com.rapidine.rapidine.Adapters.ALLItemAdapter;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.ALLItemsList;
import com.rapidine.rapidine.Models.CartListModel;
import com.rapidine.rapidine.Models.MenuCategoryList;

import com.rapidine.rapidine.Models.RestaurentList;
import com.rapidine.rapidine.Models.SearchModel;
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

public class MenuActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ImageView iv_addto_cart, iv_back, iv_search_items,iv_contact;
    TabLayout messageTabLayout;
    Switch sw_veg;
    private Session session;
    ViewPager messageViewPager;
    String resId;
    public String tableId;
    String checkVeg = "";
    String temp = "";
    String UserId;

    private RecyclerView recycler_view_item;
    private SwipeRefreshLayout swipeToRefresh;
    private LinearLayout ll_no_data_found;
    List<MenuCategoryList> menuCategoryLists;
    List<String> catIdList;

    List<ALLItemsList> allItemsLists;
    private ALLItemsList allItemsList;
    private ALLItemAdapter allItemAdapter;

    List<ALLItemsList> allItemsLists2;
    String getcat_id;
    public static TextView tv_cart_badge;

    private String allMenuUrl = "";
    List<CartListModel> cartListModels;
    private String url;
    private MenuCategoryList menuCategoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        session = new Session(this);
        UserId = session.getUser().id;
        Log.e("UserId ", "" + UserId);

        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            if (intent.hasExtra("restaurant")) {
                resId = intent.getStringExtra("resId");
                Log.e("restId_menu", resId);
            }
            if (intent.hasExtra("scan")) {
                resId = intent.getStringExtra("restaurantNumber");
                tableId = intent.getStringExtra("tebleNo");
                Log.e("restId_menu2", "" + resId);
                Log.e("tableId_menu", "" + tableId);
            }

        }

        initView();
        clickListner();
    }


    private void initView() {

        menuCategoryLists = new ArrayList<>();
        allItemsLists = new ArrayList<>();
        allItemsLists2 = new ArrayList<>();
        catIdList = new ArrayList<String>();
        cartListModels = new ArrayList<>();

        iv_addto_cart = findViewById(R.id.iv_addto_cart);
        iv_contact = findViewById(R.id.iv_contact);
        sw_veg = findViewById(R.id.sw_veg);
        iv_search_items = findViewById(R.id.iv_search_items);
        tv_cart_badge = findViewById(R.id.tv_cart_badge);
//        tv_all = findViewById(R.id.tv_all);
        iv_back = findViewById(R.id.iv_back);
        recycler_view_item = findViewById(R.id.recycler_view_item);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        ll_no_data_found = findViewById(R.id.ll_no_data_found);
        messageTabLayout = findViewById(R.id.message_tabLayout);

        swipeToRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeToRefresh.setOnRefreshListener(this);

        try {
            if (getIntent().hasExtra("scan")) {
                if (!resId.isEmpty() && resId!=null){
                    iv_contact.setVisibility(View.VISIBLE);
                    Log.e("res_id123", resId);
                }
            }

        }catch (Exception e){

        }

/////////////////////// cart list ////////////////////////////////
        if (NetworkUtil.isNetworkConnected(this)) {
            try {
                String urlCartList = API.BASE_URL + "Cart_List";
                cartList(urlCartList);
            } catch (NullPointerException e) {
                Toast.makeText(this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();

        }

        ///////////////////////// menu list ///////////////////////

        if (menuCategoryLists != null) {
            menuCategoryLists.clear();
        }
        if (NetworkUtil.isNetworkConnected(this)) {
            try {
                url = API.BASE_URL + "menuCategory";
                getMenuCategoryList(url);
            } catch (NullPointerException e) {
                Toast.makeText(this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();

        }


        allMenuUrl = API.BASE_URL + "Item_List";
    }

    private void clickListner() {
        iv_addto_cart.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_search_items.setOnClickListener(this);
        iv_contact.setOnClickListener(this);

        iv_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(MenuActivity.this, "Please wait for waiter", Toast.LENGTH_SHORT).show();

                String rating_url= API.BASE_URL + "call_waiter_notification";
                CallForWaiter(rating_url);


            }
        });

        sw_veg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkVeg = "1";
                    temp = "1";
                    Log.e("checkVeg1", "" + checkVeg);

                    /*if (menuCategoryLists != null) {
                        menuCategoryLists.clear();
                        catIdList.clear();
                    }*/
                    getMenuCategoryList(url);

                } else {
                    checkVeg = "";
                    temp = "2";
                    Log.e("checkVeg2", "" + checkVeg);
                    /*if (menuCategoryLists != null) {
                        menuCategoryLists.clear();
                        catIdList.clear();
                    }*/
                    getMenuCategoryList(url);
                }
            }
        });

        messageTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//        if (messageTabLayout.getTabCount() == 3) {
//            messageTabLayout.setTabMode(TabLayout.MODE_FIXED);
//        } else {
//            messageTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        }

        messageTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //messageViewPager.setCurrentItem(tab.getPosition());
                //getcat_id = catIdList.get(tab.getPosition());
                allItemsLists.clear();

                Log.e("R tab.getPosition()", "" + tab.getText());
                Log.e("R tab.catIdList", "" + catIdList.get(tab.getPosition()));
                Log.e("R menuCategoryData id", "" + menuCategoryData.id);

                if (tab.getPosition() == 0) {
                    getcat_id = "";
                    if (temp.equalsIgnoreCase("2")) {
                        checkVeg = "";
                    } else checkVeg = temp;

                } else {
                    //getcat_id = catIdList.get(tab.getPosition());
                    getcat_id = String.valueOf(tab.getText());

                    // checkVeg = temp;
                }

                getItemListApi(allMenuUrl, resId, getcat_id, checkVeg);
                Log.e("checkVeg2", "" + checkVeg);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void CallForWaiter(String rating_url) {
        Utils.showDialog(MenuActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(rating_url)
                .addBodyParameter("user_id", UserId)
                .addBodyParameter("restaurant_id", resId)
                .addBodyParameter("table_number", tableId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.hideProgress(mdialog);
                        Utils.dismissDialog();
                        Log.e("waiter_call=", "" + jsonObject);
                        try {

                            //JSONObject jsonObject = new JSONObject(response);
                            // String status = jsonObject.getString("is_complete_profile");
                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")){
                                Toast.makeText(MenuActivity.this, "Please wait for waiter", Toast.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Utils.dismissDialog();
                        Log.e("error fb ", "" + error);
                    }
                });


    }

    //Category Api============
    private void getMenuCategoryList(String url) {
        Log.e("menu cat url ", "" + url);
//        Utils.showDialog(MenuActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .setTag("MenuCategory")
                .addBodyParameter("restaurant_id", resId)
                .addBodyParameter("type", checkVeg)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();

                        // Log.e("MenuCategory res= ", "" + jsonObject);
                        try {

                            Log.e("MenuCategory res", " " + jsonObject);

                            String result = jsonObject.getString("result");
                            //String msg = jsonObject.getString("msg");
                            catIdList.clear();
                            menuCategoryLists.clear();
                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    menuCategoryData = new MenuCategoryList();

                                    String id = job.getString("id");
                                    //String name = job.getString("name");

                                    catIdList.add(id);
                                    if (i >= 1) {
                                        catIdList.add(id);
                                        Log.e("id_menu", "" + id);
                                    } else {
                                        catIdList.add(0, "");
                                    }
//
                                    menuCategoryData.id = job.getString("id");
                                    menuCategoryData.name = job.getString("name");

                                    menuCategoryLists.add(menuCategoryData);
                                }


                            } else {
                                ToastClass.showToast(MenuActivity.this, "Item not in stock");
                            }

                            //add tab name from list on tablayout
                            Log.e("sizeMenu", "" + menuCategoryLists.size());
                            if (messageTabLayout != null) {
                                messageTabLayout.removeAllTabs();
                            }


                            for (int j = 0; j < menuCategoryLists.size(); j++) {


                                if (j == 0) {
                                    /*if (menuCategoryLists.size() == 0) {
                                        getcat_id = "";
                                        checkVeg = "";
                                    } else checkVeg = temp;*/

                                    messageTabLayout.addTab(messageTabLayout.newTab().setText("All"));
                                }
                                messageTabLayout.addTab(messageTabLayout.newTab().setText(menuCategoryLists.get(j).name));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("MenuCat error = ", "" + error);
                    }
                });
    }

    //Item Api==========================
    private void getItemListApi(String url, String resid, String getCat, String type) {

        Log.e("url itemList", url);
        Log.e(" resid", resid);
        Log.e(" getCat", "" + getCat + "type " + type);
        Utils.showDialog(MenuActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", UserId)
                .addBodyParameter("restaurant_id", resid)
                .addBodyParameter("category_id", getCat)
                .addBodyParameter("type", type)
                .setTag("itemsList")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.hideProgress(mdialog);
                        Utils.dismissDialog();
                        //Log.e("itemsList res= ", "" + jsonObject);
                        try {

                            Log.e("itemsList res", " " + jsonObject);
                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");
                            allItemsLists.clear();
                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    ALLItemsList allItemsList = new ALLItemsList();


                                    allItemsList.id = job.getString("id");
                                    allItemsList.restaurant_id = job.getString("restaurant_id");
                                    allItemsList.category_id = job.getString("category_id");
                                    allItemsList.name = job.getString("name");
                                    allItemsList.price = job.getString("price");
                                    allItemsList.offer_price = job.getString("offer_price");
                                    allItemsList.type = job.getString("type");
                                    allItemsList.image = job.getString("image");
                                    allItemsList.description = job.getString("description");
                                    allItemsList.stock_status = job.getString("stock_status");
                                    allItemsList.quantity = job.getString("quantity");
                                    String type = job.getString("type");


//                                    if (type.equals("1")) {
//                                        allItemsLists2.add(allItemsList);
//                                    }
                                    allItemsLists.add(allItemsList);

                                }


                            } else {
                                //ToastClass.showToast(MenuActivity.this, msg);
                            }


                            allItemAdapter = new ALLItemAdapter(allItemsLists, MenuActivity.this);
                            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(MenuActivity.this);
                            recycler_view_item.setLayoutManager(mLayoutManger);
                            recycler_view_item.setLayoutManager(new LinearLayoutManager(MenuActivity.this, LinearLayoutManager.VERTICAL, false));
                            recycler_view_item.setItemAnimator(new DefaultItemAnimator());
                            recycler_view_item.setAdapter(allItemAdapter);

                            if (allItemsLists.size() == 0) {
                                swipeToRefresh.setVisibility(View.GONE);
                                ll_no_data_found.setVisibility(View.VISIBLE);
                            } else {
                                swipeToRefresh.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
                            }

                            allItemAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("item error = ", "" + error);
                    }
                });
    }

    private void cartList(String url) {
        cartListModels.clear();
        Utils.showDialog(MenuActivity.this, "Loading Please Wait...");
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
                                    tv_cart_badge.setVisibility(View.GONE);
                                } else {
                                    tv_cart_badge.setVisibility(View.VISIBLE);
                                    tv_cart_badge.setText(String.valueOf(cartListModels.size()));
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
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.iv_addto_cart:
                intent = new Intent(
                        MenuActivity.this, CartActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_back:
                //onBackPressed();
                intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.iv_search_items:
                intent = new Intent(MenuActivity.this, SearchItemActivity.class);
                intent.putExtra("restId", resId);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {
        swipeToRefresh.setRefreshing(false);

        if (NetworkUtil.isNetworkConnected(this)) {
            try {
                allItemsLists.clear();
                getItemListApi(allMenuUrl, resId, getcat_id, checkVeg);
            } catch (NullPointerException e) {
                Toast.makeText(this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
