package com.rapidine.rapidine.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;

import android.view.MenuItem;

import com.rapidine.rapidine.Adapters.CartAdapter;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Fragments.Fragment_Home;
import com.rapidine.rapidine.Fragments.Fragment_Profile;
import com.rapidine.rapidine.Fragments.Fragment_Scan;
import com.rapidine.rapidine.Fragments.Fragment_Search;
import com.rapidine.rapidine.Models.CartListModel;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.GPSTracker;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.Utils;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Session session;
    String UserId, url;
    String cartSize;
    TextView tv_location_toolbar, tv_cart_badge;
    ImageView addto_cart;
    List<CartListModel> cartListModels;
    BottomNavigationView navigation;

    GPSTracker tracker;
    double longitude, latitude;
    String address;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String[] mPermission = {
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new Session(this);
        UserId = session.getUser().id;

        //hide keyboard
        Utils.hideSoftKeyboard(this);

        cartListModels = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        tv_location_toolbar = findViewById(R.id.tv_location_toolbar);
        tv_cart_badge = findViewById(R.id.tv_cart_badge);
        addto_cart = findViewById(R.id.addto_cart);
        addto_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });


        url = API.BASE_URL + "Cart_List";
        cartList(url);

        loadFragment(new Fragment_Home());
//        tv_header.setText("Home");

        //getting bottom navigation view and attaching the listener
        navigation = findViewById(R.id.nav_bottomview);
        navigation.setOnNavigationItemSelectedListener(MainActivity.this);
        navigation.setItemIconTintList(null);

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


        try {
            tracker = new GPSTracker(this);
            if (tracker.canGetLocation()) {
                latitude = tracker.getLatitude();
                longitude = tracker.getLongitude();
                Log.e("current_lat ", " " + latitude);
                Log.e("current_Lon ", " " + longitude);
                address = getAddress(latitude, longitude);
                Log.e("Address ", " " + getAddress(latitude, longitude));
            } else {
                tracker.showSettingsAlert();
            }
        } catch (Exception e) {

        }

        tv_location_toolbar.setText(address);


    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

//        city = addresses.get(0).getLocality();
//        state = addresses.get(0).getAdminArea();
//        country = addresses.get(0).getCountryName();
//        postalCode = addresses.get(0).getPostalCode();
//        knownName = addresses.get(0).getFeatureName();

//        Log.e("fullAddress", "" + address);
//        Log.e("city", "" + city);
//        Log.e("state", "" + state);
//        Log.e("country", "" + country);
//        Log.e("postalCode", "" + postalCode);
//        Log.e("knownName", "" + knownName);
// Only if available else return NULL
        return address;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Req Code", "" + requestCode);
        System.out.println(grantResults[0] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[1] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[2] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[3] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[4] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[5] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[6] == PackageManager.PERMISSION_GRANTED);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length == 7 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[5] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[6] == PackageManager.PERMISSION_GRANTED

            ) {


            } else {
                Toast.makeText(MainActivity.this, "Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
//                tv_header.setText("Home");
                fragment = new Fragment_Home();
                break;

            case R.id.navigation_search:
//                tv_header.setText("Add Post");
                fragment = new Fragment_Search();
                break;

            case R.id.navigation_scan:
//                tv_header.setText("Notification");
                fragment = new Fragment_Scan();
                break;

            case R.id.navigation_profile:
//                tv_header.setText("Profile");
                fragment = new Fragment_Profile();
                break;
        }
        return loadFragment(fragment);
    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    private void cartList(String url) {
        Utils.showDialog(MainActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", UserId)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {
                            Log.e("CartList Res", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    CartListModel cartListModel = new CartListModel();
                                    cartListModel.menues_id = job.getString("menues_id");
                                    cartListModel.id = job.getString("id");
                                    cartListModel.category_id = job.getString("category_id");
                                    cartListModel.user_id = job.getString("user_id");
                                    cartListModel.itemName = job.getString("name");
                                    cartListModel.price = Integer.parseInt(job.getString("price"));
                                    cartListModel.offer_price = job.getString("offer_price");
                                    cartListModel.type = job.getString("type");
                                    cartListModel.image = job.getString("image");
                                    cartListModel.stock_status = job.getString("stock_status");

                                    cartListModels.add(cartListModel);
                                }

                                if (cartListModels.equals("")) {
                                    tv_cart_badge.setVisibility(View.GONE);
                                } else {
                                    tv_cart_badge.setVisibility(View.VISIBLE);
                                    tv_cart_badge.setText(String.valueOf(cartListModels.size()));
//                                    tv_cart_badge.setText(cartListModels.size());
                                }
                                Log.e("size", "Main Activity=" + cartListModels.size());

                            }

                        } catch (JSONException e) {
                            Utils.dismissDialog();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Utils.dismissDialog();
                        Log.e("cartList error = ", "" + error);
                    }
                });


    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
