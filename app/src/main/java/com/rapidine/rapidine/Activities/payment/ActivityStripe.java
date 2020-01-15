package com.rapidine.rapidine.Activities.payment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.Utils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

public class ActivityStripe extends AppCompatActivity {
    CardInputWidget mCardInputWidget;
    private ProgressDialog pdialog;
    String card_number_visa, cvv_on_card, year, date_on_visa, deposit_prize, card_name;
    int expMonth, expYear;
    private String token_id;
    EditText card_holder_name, cc, date_on_card, year_on_card, cvv;
    Button submit_card_payment, package_money;
    String total_amount, mobile, user_id, order_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);


        //get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            total_amount = intent.getStringExtra("TOTAL");
            user_id = intent.getStringExtra("USERID");
            order_id = intent.getStringExtra("ORDER");
        }

        initMethod();
    }

    private void initMethod() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        package_money = findViewById(R.id.package_money);
        submit_card_payment = findViewById(R.id.submit_card_payment);
        card_holder_name = findViewById(R.id.card_holder_name);
        cc = findViewById(R.id.cc);
        date_on_card = findViewById(R.id.date_on_card);
        year_on_card = findViewById(R.id.year_on_card);
        cvv = findViewById(R.id.cvv);

        package_money.setText(total_amount);


        submit_card_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(ActivityStripe.this);
                pay();
            }
        });


        date_on_card.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (date_on_card.getText().toString().trim().length() == 2)     //size as per your requirement
                {
                    year_on_card.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub
            }

        });

        year_on_card.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (year_on_card.getText().toString().trim().length() == 4)     //size as per your requirement
                {
                    cvv.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void pay() {
        final android.app.ProgressDialog pd = ProgressDialog.show(this, "", "Checking Card Details...", true);
        try {
            card_number_visa = cc.getText().toString();
            expMonth = Integer.parseInt(date_on_card.getText().toString());
            expYear = Integer.parseInt(year_on_card.getText().toString());
            cvv_on_card = cvv.getText().toString();

            Log.e("Value", expMonth + " , " + expYear);
            // Card card = new Card(card_number_visa, expMonth, expYear, cvv_on_card);
            Card card = new Card(card_number_visa, expMonth, expYear, cvv_on_card);
            //String stripePublicKey = KiteSDK.getInstance( this ).getStripePublicKey();
            //Stripe stripe = new Stripe(this, "pk_test_eO6b2aDEfWn38iPoVDjmEbUB");
            Stripe stripe = new Stripe(this, "");
            stripe.createToken(card,
                    new TokenCallback() {
                        // new ApiResultCallback<Token>(){
                        public void onSuccess(Token token) {
                            pd.dismiss();
                            // Send token to your server
                            System.out.println("----------------Token-------- " + token.getId());
                            token_id = token.getId();
                            Intent in = new Intent(ActivityStripe.this, PaymentActivity.class);
                            in.putExtra("TOKEN", token_id);
                            in.putExtra("TOTAL", total_amount);
                            in.putExtra("USERID", user_id);
                            in.putExtra("ORDERID", order_id);
                            in.putExtra("MOBILE", mobile);
                            startActivity(in);
                            finish();
                        }

                        public void onError(Exception error) {
                            pd.dismiss();
                            // Show localized error message
                            Toast.makeText(ActivityStripe.this, "The expiration year or the security code of your card is not valid", Toast.LENGTH_SHORT).show();
                            System.out.println("Eeeeeeeeeeeeeeerrrrr" + error.toString());
                        }
                    });
        } catch (Exception e) {
            pd.dismiss();
            e.printStackTrace();
            Toast.makeText(this, "Wrong input...", Toast.LENGTH_SHORT).show();
        }
    }
}