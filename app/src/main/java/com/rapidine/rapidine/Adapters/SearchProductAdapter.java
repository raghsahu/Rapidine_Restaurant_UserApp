package com.rapidine.rapidine.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.CartListModel;
import com.rapidine.rapidine.Models.SearchProductList;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.rapidine.rapidine.Activities.SearchItemActivity.tv_cart_badge_search;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ViewHolder> {
    private List<SearchProductList> searchProductLists;
    private SearchProductList searchProductList;
    private Session session;
    String UserId;
    String catId;
    String itemId;
    String urlAddToCart;
    private Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String tableId;

    List<CartListModel> cartListModels;
    public SearchProductAdapter(List<SearchProductList> searchProductLists, Context context) {
        this.searchProductLists = searchProductLists;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_product_item, parent, false);
        session = new Session(context);
        UserId = session.getUser().id;
        cartListModels = new ArrayList<>();
        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();
        return new SearchProductAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchProductAdapter.ViewHolder holder, final int position) {
        if (searchProductLists.size() > 0) {
            searchProductList = searchProductLists.get(position);
            holder.tv_item_name.setText(searchProductList.name);
            holder.tv_price.setText("₹ "+searchProductList.price);


            if (!searchProductList.image.equalsIgnoreCase("")) {
                Picasso.with(context).load(API.ITEMS_IMAGE_URL+searchProductList.image)
//                        .placeholder(R.drawable.tray)
                        .into(holder.iv_res_image_pro);
            }

            try {
                String resId = pref.getString("ResId", null);
                tableId = pref.getString("tableNo", null);
                if (resId != null) {
                    if (resId.equals(searchProductList.restaurant_id)) {
                        if (tableId.equals(null)) {
                            holder.tv_add_cart.setVisibility(View.GONE);
                        } else {
                            holder.tv_add_cart.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.tv_add_cart.setVisibility(View.GONE);
                    }

                } else {
                    holder.tv_add_cart.setVisibility(View.GONE);
                }
            } catch (Exception e) {

            }

            holder.tv_add_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    catId = searchProductLists.get(position).category_id;
                    itemId = searchProductLists.get(position).id;
                    urlAddToCart = API.BASE_URL + "Add_to_Cart";
                    addToCart(urlAddToCart, UserId, itemId, catId);
                    Log.e("catId_add", "" + catId);
                    Log.e("itemId_add", "" + itemId);
                }
            });

        }
    }
    @Override
    public int getItemCount() {
        return searchProductLists.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_item_name,tv_res_des,tv_price,tv_add_cart;
        public ImageView iv_res_image_pro;
        public LinearLayout ll_item;


        public ViewHolder(View parent) {
            super(parent);
            tv_item_name = parent.findViewById(R.id.tv_item_name);
            tv_price = parent.findViewById(R.id.tv_price);
//           ll_item = parent.findViewById(R.id.ll_item);
            tv_res_des = parent.findViewById(R.id.tv_res_des);
            tv_add_cart = parent.findViewById(R.id.tv_add_cart);
            iv_res_image_pro = parent.findViewById(R.id.iv_res_image_pro);

//            ll_item = parent.findViewById(R.id.ll_item);

            // ll_item.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
//            categoryData =  categaoryList.get(getAdapterPosition());
//            switch (v.getId()) {
//                case R.id.ll_item:
//                    categoryData =  categaoryList.get(getAdapterPosition());
//                    Intent i = new Intent(context, ActivityDrListClient.class);
//                    //To passmodel
//                    i.putExtra("MODEL", categoryData);
//                    context.startActivity(i);
//                    break;
//            }

        }

    }

    private void addToCart(String url, String userid, String itemid, String catid) {
        Utils.showDialog(context, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", userid)
                .addBodyParameter("menues_id", itemid)
                .addBodyParameter("category_id", catid)
                .setTag("Add to Cart")
                .setPriority(Priority.MEDIUM)
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
                                Toast.makeText(context, "Succefully Added", Toast.LENGTH_LONG).show();
                                catId = "";
                                itemId = "";
                                String url = API.BASE_URL + "Cart_List";
                                cartList(url);
                            } else {
                                Utils.openAlertDialog(context, msg);
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
        Utils.showDialog(context, "Loading Please Wait...");
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


    public void updateList(ArrayList<SearchProductList> temp) {
        searchProductLists = temp;
        notifyDataSetChanged();
    }

}
