package com.rapidine.rapidine.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.UserDataModel;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ThanksActivity extends AppCompatActivity {

    TextView tv_go_to_Home;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RatingBar ratingbar;
    EditText et_comments;
    private String rateValue,resId;
    String UserId;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);

        session = new Session(this);
        UserId = session.getUser().id;

        ratingbar=findViewById(R.id.ratingBar);
        et_comments=findViewById(R.id.et_comments);

        try {
            if (getIntent()!=null){
                resId=getIntent().getStringExtra("resId");
            }


        }catch (Exception e){

        }

//        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//
//                rateValue = String.valueOf(ratingbar.getRating());
//                System.out.println("Rate_is_res"+rateValue);
//            }
//        });

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThanksActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        findViewById(R.id.tv_go_to_Home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rating=String.valueOf(ratingbar.getRating());
                Log.e("ratingBar123", rating);
                String rating_url= API.BASE_URL + "post_rating_reviews";

                String Rating_des=et_comments.getText().toString();
                RatingReview(rating_url,Rating_des,rating);

            }
        });
    }

    private void RatingReview(String rating_url, String rating_des, String rating) {

        Utils.showDialog(ThanksActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(rating_url)
                .addBodyParameter("user_id", UserId)
                .addBodyParameter("restaurant_id", resId)
                .addBodyParameter("rating", rating)
                .addBodyParameter("review", rating_des)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.hideProgress(mdialog);
                        Utils.dismissDialog();
                        Log.e("Social res = ", "" + jsonObject);
                        try {

                            //JSONObject jsonObject = new JSONObject(response);
                            // String status = jsonObject.getString("is_complete_profile");
                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")){
                                Toast.makeText(ThanksActivity.this, "Thank you", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ThanksActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Utils.dismissDialog();
                        Log.e("error fb ", "" + error);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ThanksActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
