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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Activities.BillingActivity;
import com.rapidine.rapidine.Activities.BillingGrosTotalActivity;
import com.rapidine.rapidine.Activities.CartActivity;
import com.rapidine.rapidine.Activities.MenuActivity;
import com.rapidine.rapidine.Api.API;
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
import java.util.HashMap;
import java.util.List;

import static com.rapidine.rapidine.Activities.CartActivity.tv_order;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartListModel> cartListModels;
    private CartListModel cartListModel;
    private Context context;
    private Session session;
    String UserId;
    String orderId;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String tableId;
    //    int ItemPrice;
    int price;

    public CartAdapter(List<CartListModel> cartListModels, Context context) {
        this.cartListModels = cartListModels;
        this.context = context;

    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_lay, parent, false);
        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();
        session = new Session(context);
        UserId = session.getUser().id;
        return new CartAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartAdapter.ViewHolder holder, final int position) {
        if (cartListModels.size() > 0) {
            cartListModel = cartListModels.get(position);

            Log.e("size", "" + cartListModels.size());
            //holder.tv_item_total_cart.setText(String.valueOf(cartListModel.price * cartListModel.quantity));
            holder.tv_item_total_cart.setText(cartListModel.total);
            holder.tv_count_number.setText(String.valueOf(cartListModel.quantity));

            /*holder.iv_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartListModel = cartListModels.get(position);
                    String Itemid = cartListModel.id;
                    int Price = cartListModel.price;
                    Log.e("Itemid", "" + Itemid);
                    Log.e("ItemPrice", "" + Price);
                    int count = Integer.parseInt(holder.tv_count_number.getText().toString());
                    if (count >= 1) {
                        count = count + 1;
                        holder.tv_count_number.setText(String.valueOf(count));
                        int itemTotal = Price * count;
                        holder.tv_item_total_cart.setText(String.valueOf(itemTotal));
                        cartListModel.quantity = count;
                        cartListModel.ItemTotal = itemTotal;

                        Log.e("ItemTotalPrice", "" + cartListModel.ItemTotal);
                    }

                }
            });

            holder.iv_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartListModel = cartListModels.get(position);
                    String Itemid = cartListModel.id;
                    int Price = cartListModel.price;
                    Log.e("Itemid", "" + Itemid);
                    Log.e("ItemPrice", "" + Price);
                    int count = Integer.parseInt(holder.tv_count_number.getText().toString());
                    if (count > 0) {
                        count = count - 1;
                        if (count == 0) {
                            String cartItemId = cartListModels.get(position).id;
                            String url = API.BASE_URL + "Delete_Cart_Menues";
                            deleteApi(url, cartItemId, UserId);
                        } else {
                            holder.tv_count_number.setText(String.valueOf(count));
                            int itemTotal = Price * count;
                            holder.tv_item_total_cart.setText(String.valueOf(itemTotal));
                            cartListModel.quantity = count;
                            cartListModel.ItemTotal = itemTotal;
                        }
                    }
                }
            });*/

            cartListModel.quantity = Integer.parseInt(holder.tv_count_number.getText().toString());
            cartListModel.ItemTotal = Integer.parseInt(holder.tv_item_total_cart.getText().toString());

            /*tv_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String orderId = pref.getString("ORDERID", null);
                    if (orderId != null) {
                        Intent intent = new Intent(context, BillingActivity.class);
                        intent.putExtra("orderId", orderId);
                        Log.e("orderId", "" + orderId);
                        context.startActivity(intent);
                    } else {
                        Utils.openAlertDialog(context, "Please Order First");
                    }
                }
            });*/
            holder.tv_cart_item_name.setText(cartListModel.itemName);
            holder.tv_cart_item_price.setText("₹ " + cartListModel.price);

            if (!cartListModel.image.equalsIgnoreCase("")) {
                Picasso.with(context).load(API.ITEMS_IMAGE_URL + cartListModel.image)
//                        .placeholder(R.drawable.tray)
                        .into(holder.iv_cart_item_image);
            }
        }

        tv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<HashMap<String, String>> items;
                Log.e("sizeArray", "" + cartListModels.size());
                if (cartListModels.size() > 0) {
                    JSONArray passArray = new JSONArray();

                    for (int i = 0; i < cartListModels.size(); i++) {
                        HashMap<String, String> map = cartListModels.get(i);
                        JSONObject jObjP = new JSONObject();

                        try {
                            Log.e("item_id", "" + ((CartListModel) map).menues_id);
                            Log.e("qty", "" + ((CartListModel) map).quantity);
                            Log.e("ItemTotal", "" + ((CartListModel) map).ItemTotal);
                            Log.e("price", "" + ((CartListModel) map).price);

                            jObjP.put("item_id", ((CartListModel) map).menues_id);
                            jObjP.put("qty", ((CartListModel) map).quantity);
                            jObjP.put("item_total_price", ((CartListModel) map).ItemTotal);
                            jObjP.put("price", ((CartListModel) map).price);
                            passArray.put(jObjP);
                            Log.e("passArray", "" + passArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    String url = API.BASE_URL + "place_order";
                    String resId = pref.getString("ResId", null);
                    tableId = pref.getString("tableNo", null);
                    if (resId != null) {
                        if (tableId != null) {
                            orederApi(url, passArray);
                        } else {
                            Utils.openAlertDialog(context, "Please select table");
                        }
                    } else {
                        Utils.openAlertDialog(context, "Please Select restaurant");
                    }
                } else ToastClass.showToast(context, "Cart is empty");
            }
        });
    }

   /* public void callFinish() {
        Toast.makeText(context, "Finisshh.....", Toast.LENGTH_SHORT).show();
        String orderId = pref.getString("ORDERID", null);
        if (orderId != null) {
            Intent intent = new Intent(context, BillingActivity.class);
            intent.putExtra("orderId", orderId);
            Log.e("orderId", "" + orderId);
            context.startActivity(intent);
        } else {
            Utils.openAlertDialog(context, "Please Order First");
        }
    }*/

    @Override
    public int getItemCount() {
        return cartListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_cart_item_name, tv_cart_item_des, tv_cart_item_price, tv_count_number, tv_item_total_cart;
        public ImageView iv_cart_item_image, iv_delete, iv_plus, iv_minus;
        public LinearLayout ll_item;
        public EditText et_description;
        private String cartItemId;
        private String url;


        public ViewHolder(View parent) {
            super(parent);
            tv_cart_item_name = parent.findViewById(R.id.tv_cart_item_name);
            tv_cart_item_price = parent.findViewById(R.id.tv_cart_item_price);
            tv_cart_item_des = parent.findViewById(R.id.tv_cart_item_des);
            iv_cart_item_image = parent.findViewById(R.id.iv_cart_item_image);
            iv_delete = parent.findViewById(R.id.iv_delete);
            iv_plus = parent.findViewById(R.id.iv_plus);
            iv_minus = parent.findViewById(R.id.iv_minus);
            tv_count_number = parent.findViewById(R.id.tv_count_number);
            tv_item_total_cart = parent.findViewById(R.id.tv_item_total_cart);

            iv_plus.setOnClickListener(this);
            iv_minus.setOnClickListener(this);
            iv_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position;
            int Price;
            int count;
            String url_qty;
            String url_remove_qty;
            switch (v.getId()) {
                case R.id.iv_delete:
                    position = getAdapterPosition();
                    cartItemId = cartListModels.get(position).id;
                    url = API.BASE_URL + "Delete_Cart_Menues";
                    deleteApi(url, cartItemId, UserId);
                    break;
                case R.id.iv_minus:
                    position = getAdapterPosition();
                    cartListModel = cartListModels.get(position);
                    Price = cartListModel.price;
                    count = Integer.parseInt(tv_count_number.getText().toString());

                    if (count > 0) {
                        count--;
                        if (count == 0) {
                            cartItemId = cartListModels.get(position).id;
                            url = API.BASE_URL + "Delete_Cart_Menues";
                            deleteApi(url, cartItemId, UserId);

                            tv_count_number.setText(String.valueOf(count));
                        } else {

                            int itemTotal = Price * count;
                            tv_item_total_cart.setText(String.valueOf(itemTotal));
                            cartListModel.quantity = count;
                            cartListModel.ItemTotal = itemTotal;
                            tv_count_number.setText(String.valueOf(count));
                        }
                        url_remove_qty = API.BASE_URL + "remove_quantity_in_cart";
                        RemoveQtyApi(url_remove_qty, UserId, cartListModel.menues_id);
                    }

                    break;

                case R.id.iv_plus:
                    position = getAdapterPosition();
                    cartListModel = cartListModels.get(position);
                    Price = cartListModel.price;
                    count = Integer.parseInt(tv_count_number.getText().toString().trim());
                    count++;

                    tv_count_number.setText(String.valueOf(count));
                    int itemTotal = Price * count;
                    tv_item_total_cart.setText(String.valueOf(itemTotal));
                    cartListModel.quantity = count;
                    cartListModel.ItemTotal = itemTotal;


                    url_qty = API.BASE_URL + "add_quantity_in_cart";
                    AddQtyApi(url_qty, UserId, cartListModel.menues_id);
                    break;
            }
        }

        private void AddQtyApi(String url_qty, String userId, String menues_id) {
            //Utils.showDialog(context, "Loading Please Wait...");
            Log.e("tvCount ", tv_count_number.getText().toString().trim());
            Log.e("menues_id ",menues_id + "userid " + userId);
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
                                Log.e("AddQtyApi Res", " " + jsonObject);
                                String message = jsonObject.getString("msg");
                                String result = jsonObject.getString("result");

                                if (result.equalsIgnoreCase("true")) {

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
            Utils.showDialog(context, "Loading Please Wait...");
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
                            Utils.dismissDialog();
                            try {
                                Log.e("RemoveQtyApi Res", " " + jsonObject);
                                String message = jsonObject.getString("msg");
                                String result = jsonObject.getString("result");

                                if (result.equalsIgnoreCase("true")) {

                                }

                            } catch (JSONException e) {
                                Utils.dismissDialog();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            Utils.dismissDialog();
                            Log.e("delete error = ", "" + error);
                        }
                    });

        }
    }


    private void orederApi(String url, JSONArray passArray) {
        Log.e("cart_tableId", "" + tableId);
        Utils.showDialog(context, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", UserId)
                .addBodyParameter("table_id", tableId)
                .addBodyParameter("passArray", String.valueOf(passArray))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        Log.e("order res", "" + jsonObject);
                        try {
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");


                            if (result.equalsIgnoreCase("true")) {
                                orderId = jsonObject.getString("order_id");
                                //editor.putString("ORDERID", orderId);
                                //editor.commit();
                                Toast.makeText(context, "Place Odere Successfully...", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context, BillingGrosTotalActivity.class);
                                intent.putExtra("orderId", orderId);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                ((Activity) context).finish();



                                /*JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
//                                    billingLisModels.add(billingLisModel);

                                }*/

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("Order error = ", "" + error);
                    }
                });
    }

    private void deleteApi(String url, String catitemId, String userid) {
        Utils.showDialog(context, "Loading Please Wait...");
        AndroidNetworking.post(url)

                .addBodyParameter("user_id", userid)
                .addBodyParameter("cart_menu_id", catitemId)

                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {
                            Log.e("delete Res", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {

                                Toast.makeText(context, "Item Deleted", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context, CartActivity.class);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }

                        } catch (JSONException e) {
                            Utils.dismissDialog();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Utils.dismissDialog();
                        Log.e("delete error = ", "" + error);
                    }
                });

    }


}

