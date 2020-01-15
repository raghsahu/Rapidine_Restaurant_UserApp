package com.rapidine.rapidine.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.rapidine.rapidine.Activities.LoginActivity;
import com.rapidine.rapidine.R;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static ProgressDialog pgdialog;


    public static void openAlertDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle("Rapidine");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface , int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

       /* Resources resources = alert.getContext().getResources();
        int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
        View titleDivider = alert.getWindow().getDecorView().findViewById(titleDividerId);
        titleDivider.setBackgroundColor(Color.YELLOW);*/
        // Get the alert dialog buttons reference
        Button positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        //Button negativeButton = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
        //Button neutralButton = alert.getButton(AlertDialog.BUTTON_NEUTRAL);

        // Change the alert dialog buttons text and background color
        positiveButton.setTextColor(Color.parseColor("#FF4081"));
        //positiveButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));
    }

    //for forgot dialog
    public static void openAlertDialogForgot(final Context context, String message, final boolean value) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Rapidine");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (value) {
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    //finish();
                } else {
                    android.app.AlertDialog alert = builder.create();
                    alert.dismiss();
                }
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * This function is used to show a progress dialog
     */
    public static void showDialog(Context mContext, String strMessage)
    {

        try
        {
            if (pgdialog != null)
                if (pgdialog.isShowing())
                    pgdialog.dismiss();
            pgdialog = ProgressDialog.show(mContext,"Rapidine", strMessage, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
      This function is used to dismiss the progress dialog

     */
    public static void dismissDialog()
    {
        try
        {
            if (pgdialog.isShowing())
                pgdialog.dismiss();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public static Dialog showProgress(Context mContext) {
        Dialog mDialog = null;
        if (mContext != null) {
            mDialog = new Dialog(mContext, R.style.DialogTheme);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.custom_progress_layout);
            mDialog.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            mDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
        return  mDialog;
    }

    public static void hideProgress(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    //hide keyboard
    public static void hideSoftKeyboard(@Nullable Activity activity) {
        try {
            if (activity == null) return;
            if (activity.getCurrentFocus() == null) return;
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboardfragment(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    //hide spinner dropdown
    //Hides a spinner's drop down.
    public static void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String format12HourTime(String time, String cFormat, String dFormat) {
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat(cFormat, Locale.US);
            SimpleDateFormat displayFormat = new SimpleDateFormat(dFormat, Locale.US);
            Date dTime = parseFormat.parse(time);
            return displayFormat.format(dTime);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatDate(String date, String cFormat, String dFormat) {
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat(cFormat, Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat(dFormat, Locale.getDefault());
            Date dTime = parseFormat.parse(date);
            return displayFormat.format(dTime);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Date getDateFormat(String date, String format) {
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat(format, Locale.getDefault());

            return parseFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
