package com.rapidine.rapidine.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rapidine.rapidine.Activities.BillingActivity;
import com.rapidine.rapidine.Models.OrderHistoryData;
import com.rapidine.rapidine.R;

import java.util.List;

/**
 * Created by Ravindra Birla on 27/08/2019.
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private List<OrderHistoryData> orderHistoryList;
    private OrderHistoryData orderHistoryData;
    private Context context;


    public OrderHistoryAdapter(List<OrderHistoryData> orderHistoryList, Context context) {
        this.orderHistoryList = orderHistoryList;
        this.context = context;
    }

    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_history, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderHistoryAdapter.ViewHolder holder, int position) {
        if (orderHistoryList.size() > 0) {
            orderHistoryData = orderHistoryList.get(position);
            //holder.tv_size.setText(orderHistoryData.getSize());

            holder.tv_orderId.setText(orderHistoryData.order_id);
            holder.tv_table_number.setText(orderHistoryData.table_number);
            holder.tv_amount.setText(orderHistoryData.total_price);


           /* if (!orderHistoryData.image.equalsIgnoreCase(null) && !orderHistoryData.image.equalsIgnoreCase(url)) {
                Picasso.with(context).load(orderHistoryData.image).placeholder(R.drawable.cookie).into(holder.icon);
            }*/
        }
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_orderId, tv_amount, tv_table_number;
        public ImageView icon;
        public LinearLayout ll_item;

        public ViewHolder(View parent) {
            super(parent);
            ll_item = parent.findViewById(R.id.ll_item);
            tv_amount = parent.findViewById(R.id.tv_amount);
            tv_orderId = parent.findViewById(R.id.tv_orderId);
            tv_table_number = parent.findViewById(R.id.tv_table_number);

            ll_item.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_item:

                    Intent intent = new Intent(context, BillingActivity.class);
                    intent.putExtra("orderId", orderHistoryList.get(getAdapterPosition()).order_id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    break;
            }
        }
    }
}