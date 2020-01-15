package com.rapidine.rapidine.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rapidine.rapidine.Activities.MenuActivity;
import com.rapidine.rapidine.Activities.RestaurantDetail;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.RestaurentList;
import com.rapidine.rapidine.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurentListAdapter extends RecyclerView.Adapter<RestaurentListAdapter.ViewHolder> {
    private List<RestaurentList> restaurentLists;
    private RestaurentList restaurentList;
    private Context context;

    public RestaurentListAdapter(List<RestaurentList> restaurentLists, Context context) {
        this.restaurentLists = restaurentLists;
        this.context = context;
    }

    @NonNull
    @Override
    public RestaurentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurent_item_lay, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurentListAdapter.ViewHolder holder, final int position) {
        if (restaurentLists.size() > 0) {
            restaurentList = restaurentLists.get(position);
            holder.tv_res_name.setText(restaurentList.restaurant_name);
            holder.tv_res_des.setText(restaurentList.descriptin);
//            holder.tv_openclosetime.setText(restaurentList.openTime+" AM-"+restaurentList.closeTime+ "PM");
            holder.tv_openclosetime.setText(restaurentList.openTime + " - " + restaurentList.closeTime);
            holder.tv_mob.setText(restaurentList.mobile);

            if (!restaurentList.image.equalsIgnoreCase("")) {
                Picasso.with(context).load(API.REST_IMAGE_URL + restaurentList.image)
//                        .placeholder(R.drawable.tray)
                        .into(holder.iv_res_image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return restaurentLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_res_name, tv_res_des, tv_openclosetime, tv_mob;
        public ImageView iv_res_image;
        public LinearLayout ll_item;
        SharedPreferences pref;
        SharedPreferences.Editor editor;


        public ViewHolder(View parent) {
            super(parent);
            tv_res_name = parent.findViewById(R.id.tv_res_name);
            ll_item = parent.findViewById(R.id.ll_item);
            tv_res_des = parent.findViewById(R.id.tv_res_des);
            tv_openclosetime = parent.findViewById(R.id.tv_openclosetime);
            iv_res_image = parent.findViewById(R.id.iv_res_image);
            tv_mob = parent.findViewById(R.id.tv_mob);

            ll_item.setOnClickListener(this);

            pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            editor = pref.edit();
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.ll_item) {
                try {
                    restaurentList = restaurentLists.get(getAdapterPosition());
                    String resId = pref.getString("ResId", null);
                    String tableNo = pref.getString("tableNo", null);
                    if (restaurentList.id.equalsIgnoreCase(resId)) {
                        if (resId != null && tableNo != null) {
                            Intent intent = new Intent(context, MenuActivity.class);
                            intent.putExtra("scan", "scan");
                            intent.putExtra("restaurantNumber", resId);
                            intent.putExtra("tebleNo", tableNo);
                            context.startActivity(intent);
                        }
                    } else {
                        Intent i = new Intent(context, RestaurantDetail.class);
                        //To passmodel
                        i.putExtra("main", "main");
                        i.putExtra("MODEL", restaurentList);
                        context.startActivity(i);
                    }

                } catch (Exception e) {
                    Log.e("Exception resadaptor", "" + e);
                }
            }
        }
    }
}
