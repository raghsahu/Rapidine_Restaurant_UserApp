package com.rapidine.rapidine.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import com.google.gson.Gson;
import com.rapidine.rapidine.Activities.LoginActivity;
import com.rapidine.rapidine.Models.UserDataModel;

public class Session extends Object {

    private static final String TAG = Session.class.getSimpleName();
    private static final String PREF_NAME = "Rapidine_pref";
    private static final String PREF_NAME2 = "Rapidine_pref2";
    private static final String IS_LOGGEDIN = "isLoggedIn";
    private static final String FAV = "fav";
    private static final String Mobile = "mobile";
    private static final String Email = "email";
    private static final String Token_Id = "token";
    private static SharedPreferences sharedPreferences;
    private Context _context;
    private SharedPreferences Rapidine_pref, Rapidine_pref2;
    private SharedPreferences.Editor editor, editor2, editor3;

    public Session(Context context) {
        this._context = context;
        Rapidine_pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = Rapidine_pref.edit();
        editor.apply();

        Rapidine_pref2 = _context.getSharedPreferences(PREF_NAME2, Context.MODE_PRIVATE);
        editor2 = Rapidine_pref2.edit();
        editor2.apply();
    }

    public void createSession(UserDataModel userDataModel) {
        Gson gson = new Gson();
        String json = gson.toJson(userDataModel);
        editor.putString("user", json);
        //editor.putBoolean(IS_LOGGEDIN, true);
        editor.putBoolean("IsLogin", true);
        editor.apply();
    }

    public UserDataModel getUser() {
        Gson gson = new Gson();
        String str = Rapidine_pref.getString("user", "");
        if (str.isEmpty())
            return null;
        return gson.fromJson(str, UserDataModel.class);
    }

    public void setMobile(String mobile, String email) {

        editor2.putString(Mobile, mobile);
        editor2.putString(Email, email);
        editor2.apply();
        editor2.commit();
    }

    public String getMobile() {
        return Rapidine_pref2.getString(Mobile, "");

    }

    public String getEmail() {
        return Rapidine_pref2.getString(Email, "");
    }


    public void saveToken(String token) {

        editor2.putString(Token_Id, token);
        editor2.apply();
        editor2.commit();
    }

    public String getTokenId() {
        return Rapidine_pref2.getString(Token_Id, "");
    }



    /*public void setPassword(String password) {
        editor2.putString("userpassword", password);
        this.editor2.apply();
    }

    public void setReminderState(b ooleanisset) {
        editor2.putBoolean("ReminderState", isset);
        this.editor2.apply();
    }


    public void setEmailPhone(String emailPhone) {
        editor2.putString("emailPhone", emailPhone);
        this.editor2.apply();
    }

    public String getEmailPhone() {
        return mypref2.getString("emailPhone", "");
    }

    public boolean getReminderState() {
        return mypref2.getBoolean("ReminderState", true);
    }


    public String getPassword() {
        return mypref2.getString("userPassword", "");
    }

    public void rememberMe(String user, String password) {
        editor2.putString("email", getEmailPhone());
        editor2.putString("pass", getPassword());
        editor2.apply();
    }*/

    public void logout() {
        editor.clear();
        editor.apply();
        Intent showLogin = new Intent(_context, LoginActivity.class);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(showLogin);
    }

    public boolean isLoggedIn() {
        //return mypref.getBoolean(IS_LOGGEDIN, false);
        return Rapidine_pref.getBoolean("IsLogin", false);
    }


    public void rememberMe(String email, String password) {
        editor2.putString("rem_email", email);
        editor2.putString("rem_password", password);
        editor2.apply();
    }

    public String getRemEmail() {
        return Rapidine_pref2.getString("rem_email", "");
    }

    public String getRemPassword() {
        return Rapidine_pref2.getString("rem_password", "");
    }

    public void setPriceRangerValue(String product_name, String quantity, String minValue, String maxValue) {
        editor.putString("product_name", product_name);
        editor.putString("quantity", quantity);
        editor.putString("minPrice", minValue);
        editor.putString("maxPrice", maxValue);
        editor.apply();
    }

    public String getMinValue() {
        return Rapidine_pref.getString("minPrice", "0");
    }

    public String getMaxValue() {
        return Rapidine_pref.getString("maxPrice", "500");
    }

    public String getProductName() {
        return Rapidine_pref.getString("product_name", "");
    }

    public String getProductQuantity() {
        return Rapidine_pref.getString("quantity", "");
    }
}
