package com.rapidine.rapidine.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Activities.BillingActivity;
import com.rapidine.rapidine.Adapters.BennerAdapter;
import com.rapidine.rapidine.Adapters.RestaurentListAdapter;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.BennerModel;
import com.rapidine.rapidine.Models.RestaurentList;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.GPSTracker;
import com.rapidine.rapidine.Utils.NetworkUtil;
import com.rapidine.rapidine.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Fragment_Home extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Integer> img;
    private RecyclerView recycler_view_restaurent;
    private LinearLayout ll_no_data_found;
    private List<RestaurentList> restaurentLists;
    private List<BennerModel> bennerModels;
    private BennerAdapter mAdapter;
    private RestaurentListAdapter restaurentListAdapter;
    GPSTracker tracker;
    double longitude, latitude;
    String address;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private View view;

    public Fragment_Home() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);


        tracker = new GPSTracker(getActivity());
        if (tracker.canGetLocation()) {
            latitude = tracker.getLatitude();
            longitude = tracker.getLongitude();
            Log.e("current_lat ", " " + latitude);
            Log.e("current_Lon ", " " + longitude);
//            address = getAddress(latitude, longitude);
//            tv_location_toolbar.setText(address);
            // Log.e("Address ", " " + getAddress(latitude, longitude));
        } else {
            tracker.showSettingsAlert();
        }
        initView();

        return view;
    }

    private void initView() {
        restaurentLists = new ArrayList<>();
        bennerModels = new ArrayList<>();


        recyclerView = view.findViewById(R.id.recycler_view);
        recycler_view_restaurent = view.findViewById(R.id.recycler_view_restaurent);
        ll_no_data_found = view.findViewById(R.id.ll_no_data_found);


        if (NetworkUtil.isNetworkConnected(getContext())) {
            try {
                String url = API.BASE_URL + "restaurant_List?" + "lat=" + latitude + "&lng=" + longitude;
                String urlBenner = API.BASE_URL + "bannerImage_List";
                getRestaurentList(url);
                getBennerApi(urlBenner);
            } catch (NullPointerException e) {
                Toast.makeText(getContext(), getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
        }
    }

    private void getBennerApi(String url) {
        Utils.showDialog(getActivity(), "Loading Please Wait...");
        AndroidNetworking.get(url)
//                .addPathParameter("pageNumber", "0")
//                .addQueryParameter("limit", "3")
//                .addHeaders("token", "1234")

                .setTag("Benner list")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {
                            Log.e("benner res", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    BennerModel bennerModel = new BennerModel();
                                    bennerModel.banner_id = job.getString("banner_id");
                                    bennerModel.image = job.getString("image");
                                    bennerModels.add(bennerModel);
                                }

                                mAdapter = new BennerAdapter(bennerModels, getActivity());
                                RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(mLayoutManger);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(mAdapter);

                                mAdapter.notifyDataSetChanged();
                            }

                            //check arraylist size
//                            if (catagoryList.size() == 0) {
//                                swipeRefreshLayout.setVisibility(View.GONE);
//                                l_no_record.setVisibility(View.VISIBLE);
//                            } else {
//                                swipeRefreshLayout.setVisibility(View.VISIBLE);
//                                l_no_record.setVisibility(View.GONE);
//                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                        Log.e("Benner error = ", "" + error);
                    }
                });


    }


    private void getRestaurentList(String url) {
        Utils.showDialog(getActivity(), "Loading Please Wait...");
        AndroidNetworking.get(url)
//                .addPathParameter("pageNumber", "0")
//                .addQueryParameter("limit", "3")
//                .addHeaders("token", "1234")

                .setTag("Restaurent list")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {
                            Log.e("restaurent", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);

                                    RestaurentList restaurentList = new RestaurentList();
                                    restaurentList.id = job.getString("id");
                                    restaurentList.restaurant_name = job.getString("restaurant_name");
                                    restaurentList.descriptin = job.getString("descriptin");
                                    restaurentList.location = job.getString("location");
                                    restaurentList.lat = job.getString("lat");
                                    restaurentList.lng = job.getString("lng");
                                    restaurentList.mobile = job.getString("mobile");
//                                    restaurentList.user_name = job.getString("user_name");
//                                    restaurentList.status = job.getString("status");
                                    restaurentList.image = job.getString("image");
                                    restaurentList.openTime = job.getString("openTime");
                                    restaurentList.closeTime = job.getString("closeTime");
//                                    restaurentList.created_at = job.getString("created_at");

                                    restaurentLists.add(restaurentList);
                                }

                                restaurentListAdapter = new RestaurentListAdapter(restaurentLists, getActivity());
                                RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
                                recycler_view_restaurent.setLayoutManager(mLayoutManger);
                                recycler_view_restaurent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                recycler_view_restaurent.setItemAnimator(new DefaultItemAnimator());
                                recycler_view_restaurent.setAdapter(restaurentListAdapter);

                                restaurentListAdapter.notifyDataSetChanged();
                            }

                            //check arraylist size
                            if (restaurentLists.size() == 0) {
                                recycler_view_restaurent.setVisibility(View.GONE);
                                ll_no_data_found.setVisibility(View.VISIBLE);
                            } else {
                                recycler_view_restaurent.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                        Log.e("resList error = ", "" + error);
                    }
                });
    }
}

