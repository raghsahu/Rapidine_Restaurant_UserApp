package com.rapidine.rapidine.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Activities.LoginActivity;
import com.rapidine.rapidine.Adapters.SearchAdapter;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.RestaurentList;
import com.rapidine.rapidine.Models.SearchModel;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.GPSTracker;
import com.rapidine.rapidine.Utils.NetworkUtil;
import com.rapidine.rapidine.Utils.Utils;
import com.rapidine.rapidine.Utils.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Search extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private List<SearchModel> searchModels;
    private SearchModel searchModel;
    private SearchAdapter searchAdapter;
    private RecyclerView recycler_view_search;
    private SwipeRefreshLayout swipeToRefresh;
    private LinearLayout ll_no_data_found;
    private EditText et_search;
    Editable s;
    private Context context;
    GPSTracker tracker;
    double longitude, latitude;
    private View view;

    public Fragment_Search() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_search, container, false);

        tracker = new GPSTracker(getActivity());
        if (tracker.canGetLocation()) {
            latitude = tracker.getLatitude();
            longitude = tracker.getLongitude();
            Log.e("current_lat ", " " + latitude);
            Log.e("current_Lon ", " " + longitude);
//            address = getAddress(latitude, longitude);
        } else {
            tracker.showSettingsAlert();
        }

        initView();

        return view;
    }

    private void initView() {
        searchModels = new ArrayList<>();

        et_search = view.findViewById(R.id.et_search);
        recycler_view_search = view.findViewById(R.id.recycler_view_search);
        swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        ll_no_data_found = view.findViewById(R.id.ll_no_data_found);

        swipeToRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeToRefresh.setOnRefreshListener(this);


        if (NetworkUtil.isNetworkConnected(context)) {
            try {
                String url = API.BASE_URL + "restaurant_List?" + "lat=" + "" + "&lng=" + "";
                searchListApi(url);
            } catch (NullPointerException e) {
                Toast.makeText(context, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(context, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();


        //s = et_search.getText();
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }


    private void checkValidation() {
        String search = et_search.getText().toString();

        Validation validation = new Validation(getActivity());

        if (!validation.isEmpty(search)) {
            Toast.makeText(getActivity(), "Empty field", Toast.LENGTH_LONG).show();
            et_search.requestFocus();

        } else {

            searchModels.clear();
            String url = API.BASE_URL + "Search_restaurant";
//            search(url);

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


    private void searchListApi(String url) {
        Utils.showDialog(getActivity(), "Loading Please Wait...");
        AndroidNetworking.post(url)
                /*.addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .addHeaders("token", "1234")*/
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {
                            Log.e("Search", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    SearchModel searchModel = new SearchModel();
                                    searchModel.id = job.getString("id");
                                    searchModel.restaurant_name = job.getString("restaurant_name");
                                    searchModel.descriptin = job.getString("descriptin");
                                    searchModel.location = job.getString("location");
                                    searchModel.lat = job.getString("lat");
                                    searchModel.lng = job.getString("lng");
                                    searchModel.mobile = job.getString("mobile");
//                                    restaurentList.user_name = job.getString("user_name");
//                                    restaurentList.status = job.getString("status");
                                    searchModel.image = job.getString("image");
                                    searchModel.openTime = job.getString("openTime");
                                    searchModel.closeTime = job.getString("closeTime");
//                                    restaurentList.created_at = job.getString("created_at");


                                    searchModels.add(searchModel);
                                }
                                searchAdapter = new SearchAdapter(searchModels, getActivity());
                                RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
                                recycler_view_search.setLayoutManager(mLayoutManger);
                                recycler_view_search.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                recycler_view_search.setItemAnimator(new DefaultItemAnimator());
                                recycler_view_search.setAdapter(searchAdapter);
//
                                searchAdapter.notifyDataSetChanged();
                            }

                            //check arraylist size
                            if (searchModels.size() == 0) {
                                swipeToRefresh.setVisibility(View.GONE);
                                ll_no_data_found.setVisibility(View.VISIBLE);
                            } else {
                                swipeToRefresh.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
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

    private void filter(String s) {
        ArrayList<SearchModel> temp = new ArrayList();
        for (SearchModel smodel : searchModels) {
            //use .toLowerCase() for better matches
            if (smodel.restaurant_name.toLowerCase().startsWith(s.toLowerCase())) {
                temp.add(smodel);
            }
        }
        //update recyclerview
        searchAdapter.updateList(temp);
    }

    @Override
    public void onRefresh() {
        swipeToRefresh.setRefreshing(false);
        if (NetworkUtil.isNetworkConnected(context)) {
            try {
                searchModels.clear();
                String url = API.BASE_URL + "restaurant_List?" + "lat=" + "" + "&lng=" + "";
                searchListApi(url);
            } catch (NullPointerException e) {
                Toast.makeText(context, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(context, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}