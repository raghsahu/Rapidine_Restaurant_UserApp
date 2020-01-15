package com.rapidine.rapidine.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Ravindra Birla on 05,December,2018
 */
public class ToastClass {
    //Toast message
    public static void showToast(Context mcontext, String message) {
        Toast.makeText(mcontext, message, Toast.LENGTH_SHORT).show();
    }
}
