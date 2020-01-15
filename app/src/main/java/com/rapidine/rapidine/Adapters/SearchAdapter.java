package com.rapidine.rapidine.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rapidine.rapidine.Activities.RestaurantDetail;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.RestaurentList;
import com.rapidine.rapidine.Models.SearchModel;
import com.rapidine.rapidine.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<SearchModel> searchModels;
    private SearchModel searchModel;
    private Context context;

    public SearchAdapter(List<SearchModel> searchModels, Context context) {
        this.searchModels = searchModels;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_lay, parent, false);
        return new SearchAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        if (searchModels.size() > 0) {
            searchModel = searchModels.get(position);
            holder.tv_res_name.setText(searchModel.restaurant_name);
            holder.tv_res_des.setText(searchModel.descriptin);
//            holder.tv_openclosetime.setText(restaurentList.openTime+" AM-"+restaurentList.closeTime+ "PM");
            holder.tv_openclosetime.setText(searchModel.openTime+" "+searchModel.closeTime);

            if (!searchModel.image.equalsIgnoreCase("")) {
                Picasso.with(context).load(API.REST_IMAGE_URL+searchModel.image)
//                        .placeholder(R.drawable.tray)
                        .into(holder.iv_res_image);
            }
        }
    }
    @Override
    public int getItemCount() {
        return searchModels.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_res_name,tv_res_des,tv_openclosetime;
        public ImageView iv_res_image;
        public LinearLayout ll_item_search;


        public ViewHolder(View parent) {
            super(parent);
            tv_res_name = parent.findViewById(R.id.tv_res_name);
            ll_item_search = parent.findViewById(R.id.ll_item_search);
            tv_res_des = parent.findViewById(R.id.tv_res_des);
            tv_openclosetime = parent.findViewById(R.id.tv_openclosetime);
            iv_res_image = parent.findViewById(R.id.iv_res_image);

//            ll_item = parent.findViewById(R.id.ll_item);

            ll_item_search.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            searchModel =  searchModels.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.ll_item_search:
                    searchModel =  searchModels.get(getAdapterPosition());
                    Intent i = new Intent(context, RestaurantDetail.class);
                    //To passmodel
                    i.putExtra("search", "search");
                    i.putExtra("MODEL", searchModel);
                    context.startActivity(i);
                    break;
            }
        }

    }
    public void updateList(ArrayList<SearchModel> temp) {
        searchModels = temp;
        notifyDataSetChanged();
    }

}
