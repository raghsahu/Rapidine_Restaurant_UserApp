package com.rapidine.rapidine.Activities.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Activities.MainActivity;
import com.rapidine.rapidine.Activities.ThanksActivity;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.NetworkUtil;
import com.rapidine.rapidine.Utils.ToastClass;
import com.rapidine.rapidine.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_checkout, tv_PayTotal;
    private String payment = "";
    private String Total;
    private RadioButton rb_online, rb_cod;
    private String order_id;
    private String user_id;
    private String token_id = "";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Total = intent.getStringExtra("TOTAL");
            order_id = intent.getStringExtra("ORDER");
            user_id = intent.getStringExtra("USERID");
            token_id = intent.getStringExtra("TOKEN");
        }
        initView();
        clickListner();
        // tv_PayTotal.setText("Total: Rs. "+String.valueOf(Total)+"/-");

    }

    private void initView() {
        rb_online = findViewById(R.id.rb_online);
        rb_cod = findViewById(R.id.rb_cod);
        tv_checkout = findViewById(R.id.tv_checkout);
        tv_PayTotal = findViewById(R.id.tv_PayTotal);

        if (token_id != null) {
            rb_online.setChecked(true);
            payment = "stripe";
        } else {
            token_id = "";
        }
    }

    private void clickListner() {
        tv_checkout.setOnClickListener(this);
        rb_cod.setOnClickListener(this);
        rb_online.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_checkout:
                checkValidation();
                /*Intent intent = new Intent(PaymentActivity.this, ThanksActivity.class);
                startActivity(intent);
                finish();*/

                break;

            case R.id.rb_cod:
                rb_online.setChecked(false);
                if (rb_cod.isChecked()) {
                    payment = "cash";
                }
                break;
            case R.id.rb_online:
                rb_cod.setChecked(false);
                if (rb_online.isChecked()) {
                    payment = "cash";
                    //generateTokenForStripe();
                    /*Intent in = new Intent(PaymentActivity.this, ActivityStripe.class);
                    in.putExtra("TOTAL", Total);
                    in.putExtra("ORDER", order_id);
                    in.putExtra("USERID", user_id);

                    startActivity(in);*/
                }
                break;
        }

    }

    private void checkValidation() {
        if (payment.equalsIgnoreCase("")) {

            ToastClass.showToast(this, "Please select payment mode");
        } else {
            if (NetworkUtil.isNetworkConnected(this)) {
                try {
                    String url = API.BASE_URL + "add_payment";
                    CallCheckOutApi(url);
                } catch (NullPointerException e) {
                    ToastClass.showToast(this, getString(R.string.too_slow));
                }
            } else {
                ToastClass.showToast(this, getString(R.string.no_internet_access));
            }
        }
    }

    private void CallCheckOutApi(String url) {

        Utils.showDialog(this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                // .addBodyParameter("user_id", user_id)
                .addBodyParameter("order_id", order_id)
                /*.addBodyParameter("payment_method", payment)
               .addBodyParameter("payment_method", "cash")
               .addBodyParameter("total_amount", Total)
               .addBodyParameter("token", token_id)
               .addBodyParameter("currency", "usd")*/
                .setTag("checkout")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {

                            //String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                //JSONObject job = jsonObject.getJSONObject("result");
                                /*CheckOutData checkOut = new CheckOutData();
                                checkOut.id = job.getString("id");
                                checkOut.user_id = job.getString("user_id");
                                checkOut.total_amount = job.getString("total_amount");
                                checkOut.currency = job.getString("currency");
                                checkOut.transaction_id = job.getString("transaction_id");
                                checkOut.payment_method = job.getString("payment_method");
                                checkOut.order_id = job.getString("order_id");
                                checkOut.date_time = job.getString("date_time");
                                checkOut.status = job.getString("status");*/

                                ToastClass.showToast(PaymentActivity.this, msg);
                                //remove resturent id
                                pref = PaymentActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                                String resId = pref.getString("ResId", null);
                                editor = pref.edit();
                                editor.remove("ResId");
                                editor.remove("tableNo");
                                editor.clear();
                                editor.commit();


                                 /*Intent i;
                               if (payment.equalsIgnoreCase("cash")) {
                                    i = new Intent(PaymentActivity.this, MainActivity.class);
                                    startActivity(i);
                                } else {
                                    i = new Intent(PaymentActivity.this, MainActivity.class);
                                    startActivity(i);
                                }*/

                                Intent intent = new Intent(PaymentActivity.this, ThanksActivity.class);
                                intent.putExtra("resId", resId);
                                startActivity(intent);
                                finish();

                            } else {
                                //ToastClass.showToast(ActivityLogin.this, result);
                                Utils.openAlertDialog(PaymentActivity.this, result);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        ToastClass.showToast(PaymentActivity.this, error.toString());
                    }
                });


    }
}
