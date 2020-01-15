package com.rapidine.rapidine.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.Validation;

public class ForgotPassword extends AppCompatActivity {

    EditText et_mobile_number;
    TextView tv_submit;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        et_mobile_number = findViewById(R.id.et_mobile_number);
        tv_submit = findViewById(R.id.tv_submit);
        iv_back = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
    }

    private void checkValidation() {
        String et_couponCode = et_mobile_number.getText().toString();

        Validation validation = new Validation(this);

        if (!validation.isValidNo(et_couponCode)) {
            Toast.makeText(ForgotPassword.this, "Phone Number is empty", Toast.LENGTH_LONG).show();
            et_mobile_number.requestFocus();

        } else {

            String url = API.BASE_URL + "Forgot_pass";
            //forgotPassApi(url);


        }
    }

    private void forgotPassApi(String url) {

    }
}
