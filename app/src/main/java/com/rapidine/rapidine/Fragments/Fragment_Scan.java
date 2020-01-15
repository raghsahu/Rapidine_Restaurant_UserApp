package com.rapidine.rapidine.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.rapidine.rapidine.Activities.MenuActivity;
import com.rapidine.rapidine.Activities.RestaurantDetail;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.ToastClass;

import org.json.JSONException;
import org.json.JSONObject;

public class Fragment_Scan extends Fragment {
    Barcode barcodeResult;
    TextView tv_invalid;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public Fragment_Scan() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        tv_invalid = view.findViewById(R.id.tv_invalid);
        pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();
        startScan();

        return view;
    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(getActivity())
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
//                            result.setText(obj.getString("address"));
                            String resNo = obj.getString("restaurant_code");
                            String tableNo = obj.getString("table_no");
                            Log.e("restaurant", "" + resNo);
                            Log.e("teble_no", "" + tableNo);
                            Log.e("obj", "" + obj);
                            if (tableNo != null) {

                                //Save sharedpreference====
                                editor.putString("ResId", resNo);
                                editor.putString("tableNo", tableNo);
                                editor.commit();

                                Intent intent = new Intent(getActivity(), MenuActivity.class);
                                intent.putExtra("scan", "scan");
                                intent.putExtra("restaurantNumber", resNo);
                                intent.putExtra("tebleNo", tableNo);
                                startActivity(intent);

                                //Toast.makeText(getActivity(),"Invalid QR Code",Toast.LENGTH_LONG).show();
                            } else {
                                tv_invalid.setVisibility(View.VISIBLE);
                                ToastClass.showToast(getContext(), "QR Code not belong to this Restaurant...");
                            }

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
