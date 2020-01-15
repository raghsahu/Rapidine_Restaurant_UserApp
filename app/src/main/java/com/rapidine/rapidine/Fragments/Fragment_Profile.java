package com.rapidine.rapidine.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rapidine.rapidine.Activities.ActivityOrderHistory;
import com.rapidine.rapidine.Activities.EditProfile;
import com.rapidine.rapidine.Activities.LoginActivity;
import com.rapidine.rapidine.Activities.PrivacyPolicy;
import com.rapidine.rapidine.Activities.SignUpActivity;
import com.rapidine.rapidine.Activities.TermsAndContition;
import com.rapidine.rapidine.Activities.payment.PaymentActivity;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.Utility;
import com.rapidine.rapidine.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_Profile extends Fragment {
    private Session session;
    CircleImageView iv_profileImage;
    TextView tv_mobile, tv_user_name, tv_email, tv_logout, tv_order_history,tv_privacy;
    String mobile, id;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    File destination;
    String filenew;
    ImageView iv_edit;
    GoogleSignInClient mGoogleSignInClient;
    LoginManager loginManager;
    private TextView tv_share;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public Fragment_Profile() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        session = new Session(getActivity());
        mobile = session.getUser().mobile;
        id = session.getUser().id;
        iv_edit = view.findViewById(R.id.iv_edit);
        tv_share = view.findViewById(R.id.tv_share);
        tv_mobile = view.findViewById(R.id.tv_mobile);
        iv_profileImage = view.findViewById(R.id.iv_profileImage);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        tv_email = view.findViewById(R.id.tv_email);
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_privacy = view.findViewById(R.id.tv_privacy);
        tv_order_history = view.findViewById(R.id.tv_order_history);
        tv_mobile.setText(mobile);

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTextUrl();
            }
        });
        tv_order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityOrderHistory.class);
                startActivity(intent);
            }
        });
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                getActivity().startActivity(intent);
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        iv_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), PrivacyPolicy.class);
                startActivity(intent);
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()).setTitle("RapiDine")
                        .setMessage("Are you sure, you want to logout this app");

                dialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        //delete table id
                        pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                        editor = pref.edit();
                        editor.remove("ResId");
                        editor.remove("tableNo");
                        editor.clear();
                        editor.commit();

                        mGoogleSignInClient.signOut();
                        LoginManager.getInstance().logOut();
                        session.logout();
                        getActivity().finish();
                    }


                });
                final AlertDialog alert = dialog.create();
                alert.show();


            }
        });
        Log.e("mobile", "" + mobile);

        String url = API.BASE_URL + "UserprofileDetails";
        getProfile(url);
        return view;
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Rapidine");
        share.putExtra(Intent.EXTRA_TEXT, "Welcome to Rapidine! You can download app from Play Store:- https://play.google.com/");
        startActivity(Intent.createChooser(share, "Share link!"));

    }

    //Get Profile api call=====================================================
    private void getProfile(String url) {
        Utils.showDialog(getActivity(), "Loading Please Wait...");
        AndroidNetworking.post(url)
//                .addPathParameter("pageNumber", "0")
//                .addQueryParameter("limit", "3")
//                .addHeaders("token", "1234")
                .addBodyParameter("id", id)

                .setTag("profile list")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {
                            Log.e("profile", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);

                                    tv_email.setText(job.getString("email"));
                                    if (tv_email.getText().equals(null)) {
                                        tv_email.setText("Email is not verify");
                                    }
                                    tv_user_name.setText(job.getString("name"));
                                    tv_mobile.setText(job.getString("mobile"));
                                    String image = job.getString("image");
                                    if (!image.equalsIgnoreCase("") && !image.equalsIgnoreCase("null")) {
                                        Picasso.with(getActivity()).load(image)
                                                .placeholder(R.drawable.user1)
                                                .into(iv_profileImage);
                                    }
                                }


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

                        Log.e("error = ", "" + error);
                    }
                });


    }

    //Select Image From Gallery and Camera============================#
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
        startActivityForResult(i, SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {

            Uri pickedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            destination = new File(cursor.getString(cursor.getColumnIndex(filePath[0])));
            cursor.close();

            if (destination != null) {
                filenew = destination.getAbsolutePath();

            } else {
                Toast.makeText(getActivity(), "something wrong", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(this, ""+destination, Toast.LENGTH_SHORT).show();
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        iv_profileImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            if (destination != null) {
                filenew = destination.getAbsolutePath();
                // Toast.makeText(getActivity(), "path is"+destination.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "something wrong", Toast.LENGTH_SHORT).show();
            }
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        iv_profileImage.setImageBitmap(thumbnail);
    }

}