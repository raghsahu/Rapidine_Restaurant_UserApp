package com.rapidine.rapidine.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.UserDataModel;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.GPSTracker;
import com.rapidine.rapidine.Utils.NetworkUtil;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.ToastClass;
import com.rapidine.rapidine.Utils.Utils;
import com.rapidine.rapidine.Utils.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private Session session;
    TextView tv_signup, tv_google_login, tv_fb_login, tv_forgot_pass;
    Button btn_login;
    LoginButton btn_fb;
    private Context context;
    EditText et_phone_number, et_password;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    // Configure Google Sign In
    public GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private int RC_SIGN_IN = 100;

    FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    //Facebook Integration====
    
    private LoginManager mLoginManager;
    private AccessTokenTracker mAccessTokenTracker;
    private String social_name = "", social_id = "", social_email = "", social_img = "";

    GPSTracker tracker;
    double longitude, latitude;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new Session(this);
        init();
        ClickListner();


        //facebook initialization
       // FacebookSdk.sdkInitialize(LoginActivity.this);
       // setupFacebook();

        /////////////////////////////////// google /////////////////////////////////////////////////

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        ////////////////////////////////////////google end ////////////////////////////////////////////

        
      /*  GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = GoogleSignIn.getClient(this, gso);*/


    }

    private void ClickListner() {
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
        tv_google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


//        tv_fb_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAccessTokenTracker.startTracking();
//                mLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_birthday"));
//
//            }
//        });
        tv_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });


    }

    private void init() {
        btn_fb=findViewById(R.id.btn_fb);
        tv_signup = findViewById(R.id.tv_signup);
        btn_login = findViewById(R.id.btn_login);
        et_phone_number = findViewById(R.id.et_phone_number);
        et_password = findViewById(R.id.et_password);
        tv_google_login = findViewById(R.id.tv_google_login);
        tv_fb_login = findViewById(R.id.tv_fb_login);
        tv_forgot_pass = findViewById(R.id.tv_forgot_pass);

        try {
            tracker = new GPSTracker(this);
            if (tracker.canGetLocation()) {
                latitude = tracker.getLatitude();
                longitude = tracker.getLongitude();
                Log.e("current_lat ", " " + latitude);
                Log.e("current_Lon ", " " + longitude);
//                address = getAddress(latitude, longitude);
//                Log.e("Address ", " " + getAddress(latitude, longitude));
            } else {
                tracker.showSettingsAlert();
            }
        } catch (Exception e) {

        }

        callbackManager = CallbackManager.Factory.create();
        btn_fb.setPermissions("email", "public_profile");

        //*********************fb login on click
        btn_fb.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {@Override
                public void onSuccess(LoginResult loginResult) {

                    System.out.println("onSuccess");
                    Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();

                    String accessToken = loginResult.getAccessToken()
                            .getToken();
                    Log.i("accessToken", accessToken);

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {@Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                Log.i("LoginActivity",response.toString());
                                try {
                                    String id = object.getString("id");
                                    try {
                                        URL profile_pic = new URL(
                                                "http://graph.facebook.com/" + id + "/picture?type=large");
                                        Log.i("profile_pic",profile_pic + "");

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                   // String name = object.getString("name");
                                     //String email = object.getString("email");
                                    // String gender = object.getString("gender");
                                   // String birthday = object.getString("birthday");

                                    social_id = object.optString("id");
                                    social_name = object.optString("first_name") + " " + object.optString("last_name");
                                    String Fname = object.optString("last_name");
                                    social_email = object.optString("email");

                                    if (NetworkUtil.isNetworkConnected(LoginActivity.this)) {
                                        try {
                                            String urlSocial = API.BASE_URL + "Google_Login";
                                            SocialLogin(urlSocial);
                                        } catch (NullPointerException e) {
                                            Toast.makeText(LoginActivity.this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields",
                            "id,name,email,gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                        Toast.makeText(LoginActivity.this, "cancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");
                        Toast.makeText(LoginActivity.this, "fail", Toast.LENGTH_SHORT).show();
                          Log.e("Loginfb_error", exception.toString());
                        // Log.e("LoginActivity", exception.getMessage());
                    }
                });


    }

    private void checkValidation() {
        String phone = et_phone_number.getText().toString();
        String pass = et_password.getText().toString();

        Validation validation = new Validation(context);

        if (!validation.isEmpty(phone)) {
            Toast.makeText(LoginActivity.this, "Phone number is empty", Toast.LENGTH_LONG).show();
            et_phone_number.requestFocus();

        } else if (!validation.isValidPassword(pass)) {
            Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_LONG).show();
            et_password.requestFocus();
        } else {

            String url = API.BASE_URL + "singIn";
            CallLoginApi(url);

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

    private void CallLoginApi(String url) {

        Utils.showDialog(LoginActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .addBodyParameter("mobile", et_phone_number.getText().toString().trim())
                .addBodyParameter("password", et_password.getText().toString().trim())
                .setTag("userLogin")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.hideProgress(mdialog);
                        Utils.dismissDialog();
                        Log.e("Login res = ", "" + jsonObject);
                        try {

                            //JSONObject jsonObject = new JSONObject(response);
                            // String status = jsonObject.getString("is_complete_profile");
                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                Log.e("Login response = ", " " + jsonObject);
                                JSONObject job = jsonObject.getJSONObject("data");
                                UserDataModel userDataModel = new UserDataModel();

                                userDataModel.id = job.getString("id");
                                userDataModel.user_name = job.getString("name");
                                userDataModel.mobile = job.getString("mobile");
                                userDataModel.email = job.getString("email");
                                userDataModel.password = job.getString("password");
                                userDataModel.lat = job.getString("lat");
                                userDataModel.lng = job.getString("lng");
                                userDataModel.address = job.getString("address");
                                userDataModel.status = job.getString("status");
                                userDataModel.image = job.getString("image");
                                userDataModel.email_status = job.getString("email_status");
                                userDataModel.mobile_status = job.getString("mobile_status");
                                userDataModel.created_at = job.getString("created_at");
                                userDataModel.fcm_id = job.getString("fcm_id");


                                session.createSession(userDataModel);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Utils.openAlertDialog(LoginActivity.this, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                    }
                });

    }

    private void signIn() {

        if (NetworkUtil.isNetworkConnected(this)) {
            try {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                //Starting intent for result
                startActivityForResult(signInIntent, RC_SIGN_IN);

            } catch (NullPointerException e) {
                ToastClass.showToast(this, getString(R.string.too_slow));
            }
        } else {
            ToastClass.showToast(this, getString(R.string.no_internet_access));
        }


        //getting the google signin intent
        //Intent signInIntent = mGoogleApiClient.getSignInIntent();
        //starting the activity for result
        //startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting

        /*if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }*/

        if (requestCode == RC_SIGN_IN) {
            // Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            result.getStatus().getStatusCode();
            handleSignInResult(result);
        }

    }


    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        Log.e("g_result", result.toString());
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            assert acct != null;
            social_name = acct.getDisplayName();
            social_email = acct.getEmail();

            if (acct.getPhotoUrl() != null) {
                social_img = acct.getPhotoUrl().toString();
            } else {
                social_img = "User has not set his Profile Image";
            }
            Log.e("social_img ", " " + social_img);
            social_id = acct.getId();


            Log.e("GoogleResult", "social tokene " + acct.getIdToken() + " Socilaid" + social_id + "------" + social_name + "------" + social_email);

            if (NetworkUtil.isNetworkConnected(this)) {
                try {
                    String url = API.BASE_URL + "Google_Login";
                    SocialLogin(url);
                } catch (NullPointerException e) {
                    ToastClass.showToast(this, getString(R.string.too_slow));
                }
            } else {
                ToastClass.showToast(this, getString(R.string.no_internet_access));
            }
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    /*private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_LONG).show();

            assert account != null;
            social_name = account.getDisplayName();
            social_email = account.getEmail();

            if (account.getPhotoUrl() != null) {
                social_img = account.getPhotoUrl().toString();
            } else {
                social_img = "User has not set his Profile Image";
            }
            Log.e("social_img ", " " + social_img);
            Log.e("social_name ", " " + social_name);
            social_id = account.getId();

            Log.e("GoogleResult", social_id + "------" + social_name + "------" + social_email);

            if (NetworkUtil.isNetworkConnected(this)) {
                try {
                    String urlsocial = API.BASE_URL + "Google_Login";

                    SocialLogin(urlsocial);
                } catch (NullPointerException e) {
                    Toast.makeText(this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();
            }

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Failed", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, TAG + " " + connectionResult, Toast.LENGTH_LONG).show();
    }

    //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; end google login ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (mAuth.getCurrentUser() != null) {
//            finish();
//        startActivity(new Intent(this, MainActivity.class));
//    }
    }


//..............................fb login ........................................

    private void setupFacebook() {
        mLoginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                Log.e("old token = ", "" + oldAccessToken);
            }
        };

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                if (loginResult.getRecentlyGrantedPermissions().contains("email")) {
                    requestObjectUser(loginResult.getAccessToken());
                } else {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(LoginActivity.this, "Error permissions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("ERROR", error.toString());
            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            requestObjectUser(AccessToken.getCurrentAccessToken());
        }
    }

    //fb
    private void requestObjectUser(final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                //if (response.getError() != null) {
                if (response == null) {
                    // handle error
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                } else {

                    try {
                        // loggedin = true;
                        String token = accessToken.getToken();

                        //get data from server
                        JSONObject data = response.getJSONObject();
                        Log.e("JSON FB ", "" + data);
                        if (data.has("picture")) {
                            social_img = data.getJSONObject("picture").getJSONObject("data").getString("url");
                            Log.e("fb url = ", social_img);
                        }

                        social_id = object.optString("id");
                        social_name = object.optString("first_name") + " " + object.optString("last_name");
                        String Fname = object.optString("last_name");
                        social_email = object.optString("email");

                        Log.e("fb response = ", "token  = " + token + "id = " + object.optString("id") + "fname = " + object.optString("first_name") + "lname = " + object.optString("last_name"));

                        if (NetworkUtil.isNetworkConnected(LoginActivity.this)) {
                            try {
                                String urlSocial = API.BASE_URL + "Google_Login";
                                SocialLogin(urlSocial);
                            } catch (NullPointerException e) {
                                Toast.makeText(LoginActivity.this, getString(R.string.too_slow), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.no_internet_access), Toast.LENGTH_LONG).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }


    //..............................fb login ........................................


    private void SocialLogin(String urlSocial) {

        Utils.showDialog(LoginActivity.this, "Loading Please Wait...");
        AndroidNetworking.post(urlSocial)
                .addBodyParameter("email", social_email)
                .addBodyParameter("name", social_name)
                .addBodyParameter("mobile", "")
                .addBodyParameter("lat", String.valueOf(latitude))
                .addBodyParameter("lng", String.valueOf(longitude))
                .addBodyParameter("social_id", social_id)
                .addBodyParameter("fcm_id", "")
                .setTag("userLogin")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.hideProgress(mdialog);
                        Utils.dismissDialog();
                        Log.e("Social res = ", "" + jsonObject);
                        try {

                            //JSONObject jsonObject = new JSONObject(response);
                            // String status = jsonObject.getString("is_complete_profile");
                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    UserDataModel userDataModel = new UserDataModel();
                                    userDataModel.id = job.getString("id");
                                    userDataModel.user_name = job.getString("name");
                                    userDataModel.mobile = job.getString("mobile");
                                    userDataModel.email = job.getString("email");
                                    userDataModel.password = job.getString("password");
                                    userDataModel.lat = job.getString("lat");
                                    userDataModel.lng = job.getString("lng");
                                    userDataModel.address = job.getString("address");
                                    userDataModel.status = job.getString("status");
                                    userDataModel.image = job.getString("image");
                                    userDataModel.email_status = job.getString("email_status");
                                    userDataModel.mobile_status = job.getString("mobile_status");
                                    userDataModel.created_at = job.getString("created_at");
                                    userDataModel.fcm_id = job.getString("fcm_id");


                                    session.createSession(userDataModel);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Utils.openAlertDialog(LoginActivity.this, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Utils.dismissDialog();
                        Log.e("error fb ", "" + error);
                    }
                });
    }



    public void onClick(View v) {
        if (v == tv_fb_login) {
            btn_fb.performClick();
        }
    }
}
