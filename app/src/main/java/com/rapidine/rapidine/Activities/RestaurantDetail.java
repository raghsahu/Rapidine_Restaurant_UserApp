package com.rapidine.rapidine.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.RestaurentList;
import com.rapidine.rapidine.Models.SearchModel;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.GPSTracker;
import com.rapidine.rapidine.Utils.ToastClass;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class RestaurantDetail extends AppCompatActivity {
    public static final String BARCODE_KEY = "BARCODE";
    private Barcode barcodeResult;
    private TextView result;
    ImageView header_cover_image;
    TextView tv_location, tv_res_name, tv_OpenCloseTime, tv_menu, tv_scanQR;
    String table_number;
    String resId;
    String Name, Location, Opentime, CloseTime;
    private RestaurentList restaurentList;
    private SearchModel searchModel;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private TextView tv_mob;
    GPSTracker tracker;
    double longitude, latitude;
    private String destinationLatitude;
    private String destinationLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        tracker = new GPSTracker(getApplicationContext());
        if (tracker.canGetLocation()) {
            latitude = tracker.getLatitude();
            longitude = tracker.getLongitude();
            Log.e("current_lat ", " " + latitude);
            Log.e("current_Lon ", " " + longitude);

        } else {
            tracker.showSettingsAlert();
        }

        pref = RestaurantDetail.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra("main")) {
                restaurentList = (RestaurentList) intent.getSerializableExtra("MODEL");
                destinationLatitude = restaurentList.lat;
                destinationLongitude = restaurentList.lng;
                Log.e("rest_lat ", "lat =" + restaurentList.lat + "lng =" + restaurentList.lng);
            }
            if (intent.hasExtra("search")) {
                searchModel = (SearchModel) intent.getSerializableExtra("MODEL");

                destinationLatitude = searchModel.lat;
                destinationLongitude = searchModel.lng;
            }


        }

        initView();
        clickListner();

        try {


            //check valid data
            if (restaurentList != null) {
                resId = restaurentList.id;
                if (!restaurentList.image.equalsIgnoreCase("")) {
                    Picasso.with(this).load(API.REST_IMAGE_URL + restaurentList.image)
                            .into(header_cover_image);
                }
                tv_res_name.setText(restaurentList.restaurant_name);
                tv_location.setText(restaurentList.location);
                tv_mob.setText(restaurentList.mobile);
                tv_OpenCloseTime.setText(restaurentList.openTime + " - " + restaurentList.closeTime);
            }
            if (searchModel != null) {
                resId = searchModel.id;
                if (!searchModel.image.equalsIgnoreCase("")) {
                    Picasso.with(this).load(API.REST_IMAGE_URL + searchModel.image)
                            .into(header_cover_image);
                }
                tv_res_name.setText(searchModel.restaurant_name);
                tv_location.setText(searchModel.location);
                tv_mob.setText(searchModel.mobile);
                tv_OpenCloseTime.setText(searchModel.openTime + " - " + searchModel.closeTime);
            }
        } catch (Exception e) {
            Log.e("restaurentDetail", "" + e);
        }

        if (savedInstanceState != null) {
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if (restoredBarcode != null) {
                result.setText(restoredBarcode.rawValue);
                barcodeResult = restoredBarcode;
            }
        }

    }


    private void initView() {
        header_cover_image = findViewById(R.id.header_cover_image);
        tv_res_name = findViewById(R.id.tv_res_name);
        tv_location = findViewById(R.id.tv_location);
        tv_OpenCloseTime = findViewById(R.id.tv_OpenCloseTime);
        tv_menu = findViewById(R.id.tv_menu);
        tv_scanQR = findViewById(R.id.tv_scanQR);
        tv_mob = findViewById(R.id.tv_mob);
    }


    private void clickListner() {
        tv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantDetail.this, MenuActivity.class);
                intent.putExtra("restaurant", "restaurant");
                intent.putExtra("resId", resId);
                startActivity(intent);
            }
        });

        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + destinationLatitude + "," + destinationLongitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        tv_scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
    }


    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(RestaurantDetail.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
                        //result.setText(barcode.rawValue);
                        Log.e("scan res", "" + barcode.rawValue);
                        try {
                            JSONObject obj = new JSONObject(barcode.rawValue);

//                            result.setText(obj.getString("name"));
//                            result.setText(obj.getString("address"));]
                            String resNo = obj.getString("restaurant_code");
                            String tableNo = obj.getString("table_no");
                            Log.e("restaurant", "" + resNo);
                            Log.e("table_no", "" + tableNo);

                            if (resId.equalsIgnoreCase(resNo)) {
                                editor.putString("ResId", resNo);
                                editor.putString("tableNo", tableNo);
                                editor.commit();

                                Intent intent = new Intent(RestaurantDetail.this, MenuActivity.class);
                                intent.putExtra("scan", "scan");
                                intent.putExtra("restaurantNumber", resNo);
                                intent.putExtra("tebleNo", tableNo);
                                startActivity(intent);
                            } else
                                ToastClass.showToast(RestaurantDetail.this, "QR Code not belong to this Restaurant...");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //setting values to textviews

                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

}
