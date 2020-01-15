package com.rapidine.rapidine.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rapidine.rapidine.Api.API;
import com.rapidine.rapidine.Models.UserDataModel;
import com.rapidine.rapidine.R;
import com.rapidine.rapidine.Utils.Session;
import com.rapidine.rapidine.Utils.ToastClass;
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

public class EditProfile extends AppCompatActivity {

    ImageView iv_profileImage, iv_back;
    private Session session;
    String UserId;
    TextView tv_email, tv_mobile, tv_update;
    EditText et_user_name,et_mobile;
    ImageView iv_edit;
    //Choose Image from gallery==
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    File destination;
    String filenew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        iv_profileImage = findViewById(R.id.iv_profileImage);
        tv_email = findViewById(R.id.tv_email);
        iv_back = findViewById(R.id.iv_back);
        iv_edit = findViewById(R.id.iv_edit);
        et_user_name = findViewById(R.id.et_user_name);
        et_mobile = findViewById(R.id.et_mobile);
        tv_update = findViewById(R.id.tv_update);
        tv_mobile = findViewById(R.id.tv_mobile);

        session = new Session(this);
        UserId = session.getUser().id;
        String url = API.BASE_URL + "UserprofileDetails";
        getProfile(url);

        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = API.BASE_URL + "UpdateProfile";
                editProfile(url);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getProfile(String url) {
        Utils.showDialog(EditProfile.this, "Loading Please Wait...");
        AndroidNetworking.post(url)
                .addBodyParameter("id", UserId)

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

                                    String image = job.getString("image");
                                    if (!image.equalsIgnoreCase("")) {
                                        Picasso.with(EditProfile.this).load(image)
                                                .placeholder(R.drawable.user1)
                                                .into(iv_profileImage);
                                    }
                                    tv_email.setText(job.getString("email"));
                                    if (tv_email.getText().equals(null)) {
                                        tv_email.setText("Email is not verify");
                                    }
                                    et_user_name.setText(job.getString("name"));
                                    tv_mobile.setText(job.getString("mobile"));
                                    et_mobile.setText(job.getString("mobile"));

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

    //Edit Profile Api======
    private void editProfile(String url) {
        Utils.showDialog(EditProfile.this, "Loading Please Wait...");
        AndroidNetworking.upload(url)
                .addMultipartParameter("id", UserId)
                .addMultipartParameter("name", et_user_name.getText().toString())
                .addMultipartParameter("mobile", et_mobile.getText().toString())
                .addMultipartParameter("email", tv_email.getText().toString())
                .addMultipartParameter("lat", "")
                .addMultipartParameter("lng", "")
                .addMultipartParameter("address", "")
                .addMultipartFile("image", destination)

                .setTag("edit profile list")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        try {
                            Log.e("updateprofile profile", " " + jsonObject);
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {

                                JSONObject job = jsonObject.getJSONObject("data");
                                UserDataModel userDataModel = new UserDataModel();
                                userDataModel.id = job.getString("id");
                                userDataModel.user_name = job.getString("name");
                                userDataModel.mobile = job.getString("mobile");
                                userDataModel.password = job.getString("password");
                                userDataModel.lat = job.getString("lat");
                                userDataModel.lng = job.getString("lng");
                                userDataModel.address = job.getString("address");
                                userDataModel.status = job.getString("status");
                                userDataModel.mobile_status = job.getString("mobile_status");
                                userDataModel.created_at = job.getString("created_at");
                                userDataModel.image = job.getString("image");
                                userDataModel.email_status = job.getString("email_status");

                                session.createSession(userDataModel);

                                ToastClass.showToast(EditProfile.this, message);
                                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Utils.openAlertDialog(EditProfile.this, message);
                            }

                        } catch (JSONException e) {
                            Utils.dismissDialog();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Utils.dismissDialog();
                        Log.e("error = ", "" + error);
                    }
                });


    }


    //Select Image From Gallery and Camera============================#
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(EditProfile.this);

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
            Cursor cursor = EditProfile.this.getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            destination = new File(cursor.getString(cursor.getColumnIndex(filePath[0])));
            cursor.close();

            if (destination != null) {
                filenew = destination.getAbsolutePath();

            } else {
                Toast.makeText(EditProfile.this, "something wrong", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(this, ""+destination, Toast.LENGTH_SHORT).show();
            try {
                bm = MediaStore.Images.Media.getBitmap(EditProfile.this.getContentResolver(), data.getData());
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
                Toast.makeText(EditProfile.this, "something wrong", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfile.this, MainActivity.class);
        startActivity(intent);
    }
}
