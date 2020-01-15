package com.rapidine.rapidine.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Activities.CartActivity;
import com.rapidine.rapidine.Activities.MainActivity;
import com.rapidine.rapidine.Activities.MenuActivity;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.ALLItemsList;
import com.rapidine.rapidine.Models.CartListModel;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.ToastClass;
import com.rapidine.rapidine.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.rapidine.rapidine.Activities.MenuActivity.tv_cart_badge;

public class ALLItemAdapter extends RecyclerView.Adapter<ALLItemAdapter.ViewHolder> {
    private List<ALLItemsList> allItemsLists;
    private ALLItemsList allItemsList;
    private Context context;
    private Session session;
    String UserId;
    String catId;
    String itemId;
    String urlAddToCart;
    List<CartListModel> cartListModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String tableId;

    int count = 0;
    private String Qty;

    public ALLItemAdapter(List<ALLItemsList> allItemsLists, Context context) {
        this.allItemsLists = allItemsLists;
        this.context = context;
    }

    @NonNull
    @Override
    public ALLItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_item_layout, parent, false);
        session = new Session(context);
        UserId = session.getUser().id;
        cartListModels = new ArrayList<>();
        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();

        return new ALLItemAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ALLItemAdapter.ViewHolder holder, final int position) {
        if (allItemsLists.size() > 0) {
            allItemsList = allItemsLists.get(position);
            holder.tv_item_name.setText(allItemsList.name);
            holder.tv_price.setText("₹ " + allItemsList.price);
            holder.tv_description.setText(allItemsList.description);


            try {
                String resId = pref.getString("ResId", null);
                tableId = pref.getString("tableNo", null);


                if (resId != null) {
                    if (resId.equals(allItemsList.restaurant_id)) {
                        if (tableId.equalsIgnoreCase(null)) {
                            holder.tv_add_cart.setVisibility(View.GONE);
                        } else {
                            //holder.tv_add_cart.setVisibility(View.VISIBLE);

                            //stock status
                            if (allItemsList.stock_status.equalsIgnoreCase("1")) {
                                holder.tv_stock.setVisibility(View.VISIBLE);
                                holder.tv_add_cart.setVisibility(View.GONE);
                                holder.ll_item_add.setVisibility(View.GONE);
                            } else {
                                holder.tv_stock.setVisibility(View.GONE);

                                if (!allItemsList.quantity.equalsIgnoreCase("") && !allItemsList.quantity.equalsIgnoreCase("null")) {
                                    holder.ll_item_add.setVisibility(View.VISIBLE);
                                    holder.tv_add_cart.setVisibility(View.GONE);
                                    holder.tv_count_number.setText(allItemsList.quantity);
                                } else {
                                    holder.tv_add_cart.setVisibility(View.VISIBLE);
                                    holder.ll_item_add.setVisibility(View.GONE);
                                }
                            }
                        }
                    } else {
                        holder.tv_add_cart.setVisibility(View.GONE);
                    }

                } else {
                    holder.tv_add_cart.setVisibility(View.GONE);
                }


                if (allItemsList.stock_status.equalsIgnoreCase("1") || !tableId.equalsIgnoreCase(null)) {
                    holder.rl_stock.setVisibility(View.VISIBLE);
                } else holder.rl_stock.setVisibility(View.GONE);

            } catch (Exception e) {

            }


            if (!allItemsList.image.equalsIgnoreCase("")) {
                Picasso.with(context).load(API.ITEMS_IMAGE_URL + allItemsList.image)
//                        .placeholder(R.drawable.tray)
                        .into(holder.iv_item);
            }
        }
    }

    @Override
    public int getItemCount() {
        return allItemsLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_item;
        private TextView tv_item_name, tv_price, tv_description, tv_stock;
        private RelativeLayout rl_stock;
        private TextView tv_count_number;
        private ImageView iv_minus, iv_plus;
        private LinearLayout ll_item_add;
        private TextView tv_add_cart;

        private ViewHolder(View parent) {
            super(parent);
            iv_item = parent.findViewById(R.id.iv_item);
            tv_price = parent.findViewById(R.id.tv_price);
            tv_description = parent.findViewById(R.id.tv_description);
            tv_item_name = parent.findViewById(R.id.tv_item_name);
            tv_add_cart = parent.findViewById(R.id.tv_add_cart);
            tv_stock = parent.findViewById(R.id.tv_stock);
            rl_stock = parent.findViewById(R.id.rl_stock);
            ll_item_add = parent.findViewById(R.id.ll_item_add);
            tv_count_number = parent.findViewById(R.id.tv_count_number);
            iv_minus = parent.findViewById(R.id.iv_minus);
            iv_plus = parent.findViewById(R.id.iv_plus);

            tv_add_cart.setOnClickListener(this);
            iv_plus.setOnClickListener(this);
            iv_minus.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String url_qty;
            String url_remove_qty;
            int position;
            switch (v.getId()) {
                case R.id.tv_add_cart:
                    position = getAdapterPosition();
                    if (allItemsLists.get(position).quantity.equalsIgnoreCase("")) {
                        ll_item_add.setVisibility(View.VISIBLE);
                        tv_add_cart.setVisibility(View.GONE);

                    }

                    catId = allItemsLists.get(position).category_id;
                    itemId = allItemsLists.get(position).id;
                    urlAddToCart = API.BASE_URL + "Add_to_Cart";
                    addToCart(urlAddToCart, UserId, itemId, catId);
                    Log.e("catId_add", "" + catId);
                    Log.e("itemId_add", "" + itemId);

                    /*if (allItemsLists.get(getAdapterPosition()).quantity.equalsIgnoreCase("")) {
                        ll_item_add.setVisibility(View.VISIBLE);
                        tv_add_cart.setVisibility(View.GONE);
                    }*/

                    break;
                case R.id.iv_minus:
                    position = getAdapterPosition();
                    allItemsList = allItemsLists.get(position);

                    count = Integer.parseInt(tv_count_number.getText().toString().trim());
                    if (count > 0) {
                        count--;
                        if (count == 0) {
                            tv_add_cart.setVisibility(View.VISIBLE);
                            ll_item_add.setVisibility(View.GONE);
                            Qty = String.valueOf(count);
                            tv_count_number.setText(Qty);
                        } else {
                            Qty = String.valueOf(count);
                            tv_count_number.setText(Qty);

                        }
                        url_remove_qty = API.BASE_URL + "remove_quantity_in_cart";
                        RemoveQtyApi(url_remove_qty, UserId, allItemsList.id);
                        if (count == 0) {
                            Intent intent = new Intent(context, MenuActivity.class);

                            intent.putExtra("scan", "scan");
                            intent.putExtra("restaurantNumber", allItemsList.restaurant_id);
                            intent.putExtra("tebleNo", tableId);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    }
                    break;

                case R.id.iv_plus:
                    position = getAdapterPosition();
                    allItemsList = allItemsLists.get(position);
                    count = Integer.parseInt(tv_count_number.getText().toString().trim());
                    count++;
                    Qty = String.valueOf(count);
                    tv_count_number.setText(Qty);

                    url_qty = API.BASE_URL + "add_quantity_in_cart";
                    AddQtyApi(url_qty, UserId, allItemsList.id);
                    break;
            }
        }

        private void AddQtyApi(String url_qty, String userId, String menues_id) {
            //Utils.showDialog(context, "Loading Please Wait...");
            Log.e("tvCount ", tv_count_number.getText().toString().trim());
            AndroidNetworking.post(url_qty)

                    .addBodyParameter("user_id", userId)
                    .addBodyParameter("menues_id", menues_id)
                    .addBodyParameter("quantity", tv_count_number.getText().toString().trim())
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            //Utils.dismissDialog();
                            try {
                                Log.e("AddRemoveQtyApi Res", " " + jsonObject);
                                String message = jsonObject.getString("msg");
                                String result = jsonObject.getString("result");

                                if (result.equalsIgnoreCase("true")) {
                                    String url = API.BASE_URL + "Cart_List";
                                    cartList(url);
                                    /*Toast.makeText(context, "Item Deleted", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(context, CartActivity.class);
                                    context.startActivity(intent);
                                    ((Activity) context).finish();*/
                                }

                            } catch (JSONException e) {
                                // Utils.dismissDialog();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            // Utils.dismissDialog();
                            Log.e("delete error = ", "" + error);
                        }
                    });

        }

        private void RemoveQtyApi(String url_remove_qty, String userId, String menues_id) {
            //Utils.showDialog(context, "Loading Please Wait...");
            Log.e("tvCount ", tv_count_number.getText().toString().trim());
            AndroidNetworking.post(url_remove_qty)

                    .addBodyParameter("user_id", userId)
                    .addBodyParameter("menues_id", menues_id)
                    .addBodyParameter("quantity", tv_count_number.getText().toString().trim())
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            //Utils.dismissDialog();
                            try {
                                Log.e("AddRemoveQtyApi Res", " " + jsonObject);
                                String message = jsonObject.getString("msg");
                                String result = jsonObject.getString("result");

                                if (result.equalsIgnoreCase("true")) {
                                    String url = API.BASE_URL + "Cart_List";
                                    cartList(url);
                                }

                            } catch (JSONException e) {
                                // Utils.dismissDialog();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            // Utils.dismissDialog();
                            Log.e("delete error = ", "" + error);
                        }
                    });

        }
    }

    private void addToCart(String url, String userid, String itemid, String catid) {
        Utils.showDialog(context, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", userid)
                .addBodyParameter("menues_id", itemid)
                .addBodyParameter("category_id", catid)
                .setTag("Add to Cart")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.hideProgress(mdialog);
                        Utils.dismissDialog();
                        //Log.e("itemsList res= ", "" + jsonObject);
                        try {
                            Log.e("addToCart res", " " + jsonObject);
                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                catId = "";
                                itemId = "";


                                String url = API.BASE_URL + "Cart_List";
                                cartList(url);
                            } else {
                                //Utils.openAlertDialog(context, msg);
                                ToastClass.showToast(context, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("addToCart error = ", "" + error);
                    }
                });
    }

    private void cartList(String url) {
        cartListModels.clear();
        //Utils.showDialog(context, "Loading Please Wait...");
        AndroidNetworking.post(url)
                /*.addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .addHeaders("token", "1234")*/
                .addBodyParameter("user_id", UserId)

                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        // Utils.dismissDialog();
                        try {
                            Log.e("CartList Res", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");
                            cartListModels.clear();
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
                            }
                            if (cartListModels.size() > 0) {
                                tv_cart_badge.setVisibility(View.VISIBLE);
                                tv_cart_badge.setText(String.valueOf(cartListModels.size()));
                            } else {
                                tv_cart_badge.setVisibility(View.GONE);
//                                    tv_cart_badge.setText(cartListModels.size());
                            }
                            Log.e("size", "Menu size=" + cartListModels.size());


                        } catch (JSONException e) {
                            // Utils.dismissDialog();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // Utils.dismissDialog();
                        Log.e("cartList error = ", "" + error);
                    }
                });


    }
}