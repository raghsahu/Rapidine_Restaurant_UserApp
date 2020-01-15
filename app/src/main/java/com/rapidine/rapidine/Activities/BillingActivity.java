package com.rapidine.rapidine.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Activities.payment.PaymentActivity;
import com.rapidine.rapidine.Adapters.BillingAdapter;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.BillingLisModel;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.NetworkUtil;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.ToastClass;
import com.rapidine.rapidine.Utils.Utils;
import com.rapidine.rapidine.Utils.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BillingActivity extends AppCompatActivity {

    private List<BillingLisModel> billingLisModels;
    private BillingAdapter billingAdapter;
    private TextView tv_pay_bill, tv_item_total, tv_convence, tv_gst, tv_vat, tv_table_number, tv_discount, tv_orderId, tv_total, tv_coupon_apply;
    private ImageView iv_back;
    private EditText et_coupon_code;
    private RecyclerView recycler_view_bill;
    private Session session;
    private String UserId;
    private String orderId;
    private String item_total;
    private LinearLayout li_dis;
    private String order_status = "";
    private String table_id;
    private String rest_id;
    private LinearLayout ll_apply_coupen;

    Handler h = new Handler();
    Runnable runnable;
    int delay = 3 * 1000; //Delay for 15 seconds.  One second = 1000 milliseconds.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        session = new Session(this);
        UserId = session.getUser().id;

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            orderId = intent.getStringExtra("orderId");
        }

        initView();
        clickListner();
    }

    //initialization
    private void initView() {
        billingLisModels = new ArrayList<>();
        tv_pay_bill = findViewById(R.id.tv_pay_bill);
        ll_apply_coupen = findViewById(R.id.ll_apply_coupen);
        tv_item_total = findViewById(R.id.tv_item_total);
        tv_convence = findViewById(R.id.tv_convence);
        tv_vat = findViewById(R.id.tv_vat);
        tv_gst = findViewById(R.id.tv_gst);
        tv_discount = findViewById(R.id.tv_discount);
        tv_total = findViewById(R.id.tv_total);

        recycler_view_bill = findViewById(R.id.recycler_view_bill);
        iv_back = findViewById(R.id.iv_back);
        et_coupon_code = findViewById(R.id.et_coupon_code);
        tv_coupon_apply = findViewById(R.id.tv_coupon_apply);
        tv_orderId = findViewById(R.id.tv_orderId);
        tv_table_number = findViewById(R.id.tv_table_number);

        if (NetworkUtil.isNetworkConnected(this)) {
            try {
                String url = API.BASE_URL + "get_bill";
                getBillApi(url);
            } catch (NullPointerException e) {
                //Toast.makeText(this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else {
            //Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
        }
    }

    //onclick
    private void clickListner() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BillingActivity.this, MenuActivity.class);
                intent.putExtra("scan", "scan");
                intent.putExtra("restaurantNumber", rest_id);
                intent.putExtra("tebleNo", table_id);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                startActivity(intent);
                finish();
            }
        });

        tv_pay_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order_status.equalsIgnoreCase("Pending")) {
                    ToastClass.showToast(BillingActivity.this, "Please wait order not accepted from admin...");
                } else if (order_status.equalsIgnoreCase("Preparing") || order_status.equalsIgnoreCase("Complete")) {
                    Intent intent = new Intent(BillingActivity.this, PaymentActivity.class);
                    intent.putExtra("TOTAL", tv_total.getText().toString());
                    intent.putExtra("ORDER", orderId);
                    intent.putExtra("USERID", UserId);
                    startActivity(intent);
                }
            }
        });
        tv_coupon_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });

    }

    private void checkValidation() {
        String et_couponCode = et_coupon_code.getText().toString();

        Validation validation = new Validation(this);

        if (!validation.isEmpty(et_couponCode)) {
            Toast.makeText(BillingActivity.this, "Coupon Code is empty", Toast.LENGTH_LONG).show();
            et_coupon_code.requestFocus();

        } else {

            //hide keyboard
            Utils.hideSoftKeyboard(this);
            String url = API.BASE_URL + "Apply_Coupon";
            ApplyCouponApi(url);

//            if (NetworkUtil.isNetworkConnected(context)) {
//                try {
//
//                } catch (NullPointerException e) {
//                    Toast.makeText(LoginActivity.this,getString(R.string.too_slow),Toast.LENGTH_LONG).show();
//                }
//            } else {
//                Toast.makeText(LoginActivity.this, getString(R.string.no_internet_access),Toast.LENGTH_LONG).show();
//
//            }
        }

    }

    //get coupen api
    private void ApplyCouponApi(String url) {

        Utils.showDialog(BillingActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", UserId)
                .addBodyParameter("order_id", orderId)
                .addBodyParameter("sub_total", item_total)
                .addBodyParameter("promoCode", et_coupon_code.getText().toString().trim())

                .setTag("userLogin")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.hideProgress(mdialog);
                        Utils.dismissDialog();
                        Log.e("Coupon Code res = ", "" + jsonObject);
                        try {

                            //JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                Toast.makeText(BillingActivity.this, "Coupon Code Applied", Toast.LENGTH_LONG).show();

                            } else {
                                Utils.openAlertDialog(BillingActivity.this, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("SignUp error = ", "" + error);
                    }
                });

    }

    //get bill api
    private void getBillApi(String url) {
        //Utils.showDialog(BillingActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
//                .addBodyParameter("restaurant_id",resId)
                .addBodyParameter("user_id", UserId)
                .addBodyParameter("order_id", orderId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.dismissDialog();
                        try {
                            billingLisModels.clear();
                            Log.e("getBill", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);

                                    BillingLisModel billingLisModel = new BillingLisModel();
                                    //billingLisModel.id = job.getString("id");
                                    billingLisModel.name = job.getString("name");
                                    billingLisModel.quantity = job.getString("quantity");
                                    billingLisModel.price = job.getString("price");
                                    billingLisModel.total_price = job.getString("total_price");
                                    billingLisModel.table_number = job.getString("table_number");

                                    billingLisModels.add(billingLisModel);

                                }
                                orderId = jsonObject.getString("order_id");
                                item_total = jsonObject.getString("item_total");
                                String convenience_fee = jsonObject.getString("convenience_fee");
                                String gst = jsonObject.getString("gst");
                                String vat = jsonObject.getString("vat");
                                String promo_discount = jsonObject.getString("promo_discount");
                                String total_payble = jsonObject.getString("total_payble");
                                table_id = jsonObject.getString("table_id");
                                order_status = jsonObject.getString("order_status");
                                rest_id = jsonObject.getString("restaurant_id");

                                if (order_status.equalsIgnoreCase("Finish") || order_status.equalsIgnoreCase("Cancelled")) {
                                    tv_pay_bill.setVisibility(View.GONE);
                                    ll_apply_coupen.setVisibility(View.GONE);

                                } else {
                                    tv_pay_bill.setVisibility(View.VISIBLE);
                                    ll_apply_coupen.setVisibility(View.VISIBLE);
                                }
                                //set data on text
                                tv_orderId.setText("Order Id: " + orderId);
                                tv_table_number.setText("Table Number: " + table_id);
                                tv_item_total.setText("Rs " + item_total);
                                tv_convence.setText("Rs " + convenience_fee);

                                /*DecimalFormat df2 = new DecimalFormat("#.##");
                                Double d = Double.parseDouble(gst);
                                Log.e("double ", "" + df2.format(d) + "" + String.format("%.2f", d));*/
                                tv_gst.setText("Rs " + gst);
                                tv_vat.setText("Rs " + vat);
                                tv_discount.setText("Rs " + promo_discount);
                                tv_total.setText(total_payble);

                                //set adapter
                                billingAdapter = new BillingAdapter(billingLisModels, BillingActivity.this);
                                RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(BillingActivity.this);
                                recycler_view_bill.setLayoutManager(mLayoutManger);
                                recycler_view_bill.setLayoutManager(new LinearLayoutManager(BillingActivity.this, LinearLayoutManager.VERTICAL, false));
                                recycler_view_bill.setItemAnimator(new DefaultItemAnimator());
                                recycler_view_bill.setAdapter(billingAdapter);
//
                                billingAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                        Log.e("error = ", "" + error);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BillingActivity.this, MenuActivity.class);
        intent.putExtra("scan", "scan");
        intent.putExtra("restaurantNumber", rest_id);
        intent.putExtra("tebleNo", table_id);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        h.postDelayed(runnable = new Runnable() {
            public void run() {
                if (NetworkUtil.isNetworkConnected(BillingActivity.this)) {
                    try {
                        String url = API.BASE_URL + "get_bill";
                        getBillApi(url);
                    } catch (NullPointerException e) {
                        //Toast.makeText(BillingActivity.this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Toast.makeText(BillingActivity.this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
                }
                h.postDelayed(runnable, delay);
            }
        }, delay);
    }
}
