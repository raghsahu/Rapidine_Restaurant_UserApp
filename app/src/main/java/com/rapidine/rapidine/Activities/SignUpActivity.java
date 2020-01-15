package com.rapidine.rapidine.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.UserDataModel;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.GPSTracker;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.Utils;
import com.rapidine.rapidine.Utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    private Session session;
    GPSTracker tracker;
    double longitude, latitude;
    String address;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String[] mPermission = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    TextView tv_login,tv_terms;
    EditText et_user_name, et_user_phone, et_user_password, et_user_c_pass;
    Button btn_signup;
    CheckBox ch_terms;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        session = new Session(this);

        tracker = new GPSTracker(this);
        if (tracker.canGetLocation()) {
            latitude = tracker.getLatitude();
            longitude = tracker.getLongitude();
            Log.e("current_lat ", " " + latitude);
            Log.e("current_Lon ", " " + longitude);
//            address = getAddress(latitude, longitude);
//            Log.e("Address ", " " + getAddress(latitude, longitude));
        } else {
            tracker.showSettingsAlert();
        }
        init();
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission[0])
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[1])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[2])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[3])
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        mPermission, REQUEST_CODE_PERMISSION);

                // If any permission aboe not allowed by user, this condition will execute every tim, else your else part will work
            } else {


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//                startActivity(intent);
                checkValidation();
            }
        });
    }

    private void init() {
        tv_login = findViewById(R.id.tv_login);
        btn_signup = findViewById(R.id.btn_signup);
        et_user_name = findViewById(R.id.et_user_name);
        et_user_phone = findViewById(R.id.et_user_phone);
        et_user_password = findViewById(R.id.et_user_password);
        et_user_c_pass = findViewById(R.id.et_user_c_pass);
        ch_terms = findViewById(R.id.ch_terms);
        tv_terms = findViewById(R.id.tv_terms);

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, TermsAndContition.class);
                startActivity(intent);
            }
        });
    }


    private void checkValidation() {
        String name = et_user_name.getText().toString();
        String phone = et_user_phone.getText().toString();
        String pass = et_user_password.getText().toString();
        String confirmpass = et_user_c_pass.getText().toString();

        Validation validation = new Validation(context);

        if (!validation.isValidName(name)) {
            Toast.makeText(SignUpActivity.this, "Name is empty", Toast.LENGTH_LONG).show();
            et_user_name.requestFocus();
        } else if (!validation.isEmpty(phone)) {
            Toast.makeText(SignUpActivity.this, "Phone Number is empty", Toast.LENGTH_LONG).show();
            et_user_phone.requestFocus();
        } else if (!validation.isValidPassword(pass)) {
            Toast.makeText(SignUpActivity.this, "Password  is empty", Toast.LENGTH_LONG).show();
            et_user_password.requestFocus();
        } else if (!validation.isValidPassword(confirmpass)) {
            Toast.makeText(SignUpActivity.this, "Password  is empty", Toast.LENGTH_LONG).show();
            et_user_c_pass.requestFocus();
        }else if (ch_terms.isChecked()!=true) {
            Toast.makeText(SignUpActivity.this, "Please indicate that you accept the Terms and Conditions ", Toast.LENGTH_LONG).show();
        }
        else {
            String url = API.BASE_URL + "singUp";
            CallSignUpApi(url);

//            else if (!validation.isConfirmPassword(pass, confirmpass)) {
//                Toast.makeText(SignUpActivity.this, "Password  is not match", Toast.LENGTH_LONG).show();
//                et_user_password.requestFocus();
//            }


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

    private void CallSignUpApi(String url) {

        Utils.showDialog(SignUpActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .addBodyParameter("name", et_user_name.getText().toString().trim())
                .addBodyParameter("mobile", et_user_phone.getText().toString().trim())
                .addBodyParameter("password", et_user_password.getText().toString().trim())
                .addBodyParameter("address", "vijay nagar, indore")
                .addBodyParameter("lat", String.valueOf(latitude))
                .addBodyParameter("lng", String.valueOf(longitude))
                .setTag("userLogin")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.hideProgress(mdialog);
                        Utils.dismissDialog();
                        Log.e("Signup res = ", "" + jsonObject);
                        try {

                            //JSONObject jsonObject = new JSONObject(response);
                            // String status = jsonObject.getString("is_complete_profile");
                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                Log.e("Signup response = ", " " + jsonObject);
                                JSONObject job = jsonObject.getJSONObject("data");
                                UserDataModel userDataModel = new UserDataModel();
                                Log.e("name", "" + job.getString("name"));
                                userDataModel.id = job.getString("id");
                                userDataModel.user_name = job.getString("name");
                                userDataModel.mobile = job.getString("mobile");
                                userDataModel.password = job.getString("password");
                                userDataModel.lat = job.getString("lat");
                                userDataModel.lng = job.getString("lng");
                                userDataModel.address = job.getString("address");
                                userDataModel.status = job.getString("status");
                                userDataModel.mobile_status = job.getString("mobile_status");
                                userDataModel.created_at = job.getString("created_at");

                                session.createSession(userDataModel);

                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Utils.openAlertDialog(SignUpActivity.this, msg);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Req Code", "" + requestCode);
        System.out.println(grantResults[0] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[1] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[2] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[3] == PackageManager.PERMISSION_GRANTED);


        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length == 4 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED

            ) {


            } else {
                Toast.makeText(SignUpActivity.this, "Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }


}
