package com.rapidine.rapidine.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rapidine.rapidine.R;

public class ItemActivity extends AppCompatActivity {

    TextView tv_add_cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        tv_add_cart=findViewById(R.id.tv_add_cart);
        tv_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ItemActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });
    }
}
