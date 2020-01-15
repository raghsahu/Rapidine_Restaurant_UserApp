package com.rapidine.rapidine.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.BillingLisModel;
import com.rapidine.rapidine.Models.CartListModel;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.ViewHolder> {
    private List<BillingLisModel> billingLisModels;
    private BillingLisModel billingLisModel;
    private Context context;


    public BillingAdapter(List<BillingLisModel> billingLisModels, Context context) {
        this.billingLisModels = billingLisModels;
        this.context = context;
    }

    @NonNull
    @Override
    public BillingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_lay, parent, false);
        return new BillingAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BillingAdapter.ViewHolder holder, int position) {
        if (billingLisModels.size() > 0) {
            billingLisModel = billingLisModels.get(position);
            holder.tv_item_name.setText(billingLisModel.name);
            holder.tv_quantity.setText(billingLisModel.quantity);
            holder.tv_price.setText(billingLisModel.price);
            holder.tv_amount.setText(billingLisModel.total_price);
        }
    }

    @Override
    public int getItemCount() {
        return billingLisModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_item_name, tv_quantity, tv_price, tv_amount;

        public ViewHolder(View parent) {
            super(parent);
            tv_item_name = parent.findViewById(R.id.tv_item_name_bill);
            tv_quantity = parent.findViewById(R.id.tv_quantity_bill);
            tv_price = parent.findViewById(R.id.tv_price_bill);
            tv_amount = parent.findViewById(R.id.tv_amount);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }

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
}

