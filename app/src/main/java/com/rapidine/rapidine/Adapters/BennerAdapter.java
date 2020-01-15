package com.rapidine.rapidine.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.BennerModel;
import com.rapidine.rapidine.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BennerAdapter extends RecyclerView.Adapter<BennerAdapter.ViewHolder> {
    private List<BennerModel> bennerModels;
    private BennerModel bennerModel;
    private Context context;

    public BennerAdapter(List<BennerModel> bennerModels, Context context) {
        this.bennerModels = bennerModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (bennerModels.size() > 0) {
            bennerModel=bennerModels.get(position);

            if (!bennerModel.image.equalsIgnoreCase("")) {
                Picasso.with(context).load(API.BENNER_IMAGE_URL+bennerModel.image)
                        .into(holder.img_benner);
            }
        }
    }
    @Override
    public int getItemCount() {
        return bennerModels.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView img_benner;


        public ViewHolder(View parent) {
            super(parent);

            img_benner = parent.findViewById(R.id.iv_benner);

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
}