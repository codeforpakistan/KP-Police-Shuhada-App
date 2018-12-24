package com.example.kt.shudaapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kt.shudaapp.InterfaceClasses.LoginApi;
import com.example.kt.shudaapp.ModelClasses.VerifiModel;
import com.example.kt.shudaapp.Utils.Config;
import com.kirianov.multisim.MultiSimTelephonyManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {
    private EditText login_mobile_no_et, login_password_no_et;
    private static final int AllPermissionRC = 500;
    int sim_slot;
    HashMap<String, RequestBody> mMap = new HashMap<>();
    ConstraintLayout login_layout;
    ProgressDialog progressDialog;
    TextView register_page_link;
    SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //hideKeyboard(this);
        mSharedPreferences = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String mobile_no = mSharedPreferences.getString("mobile_no", "No Data");
        login_mobile_no_et = findViewById(R.id.login_mobile_no_et);
        login_password_no_et = findViewById(R.id.login_password_no_et);
        login_layout = findViewById(R.id.login_layout);
        register_page_link = findViewById(R.id.register_page_link);
        register_page_link.setPaintFlags(register_page_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        progressDialog = new ProgressDialog(this);
        if (!mobile_no.equals("No Data")) {
            login_mobile_no_et.setText(mobile_no);
        }else {
            login_mobile_no_et.setText("");
        }
    }

    public void gotoReg(View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });
    }

    public void gotoForget(View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this, ForgetActivity.class));

            }
        });
    }

    public void gotoHome(View view) {
        String mobile_no = login_mobile_no_et.getText().toString();
        String password = login_password_no_et.getText().toString();
        if (TextUtils.isEmpty(mobile_no)) {
            login_mobile_no_et.setError("Enter mobile no");
            login_mobile_no_et.requestFocus();
            Snackbar snackbar = Snackbar
                    .make(login_layout, "Enter mobile no", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            snackbar.show();

        } else if (TextUtils.isEmpty(password)) {
            login_password_no_et.setError("Enter mobile no");
            login_password_no_et.requestFocus();
            Snackbar snackbar = Snackbar
                    .make(login_layout, "Enter mobile no", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            snackbar.show();

        } else {
            if (isNetworkAvailable()) {
                hideKeyboard(LoginActivity.this);
                progressDialog.setTitle("Authenticating");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                postData(mobile_no, password);

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(login_layout, "No internet Connection!", Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(getResources().getColor(android.R.color.white));
                        snackbar.show();
                    }
                });

            }
        }
    }

    private void postData(final String mobile_no, final String password) {
        progressDialog.show();
        mMap.put("mobile_no", createPartFromString(mobile_no));
        mMap.put("password", createPartFromString(password));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final LoginApi loginApi = retrofit.create(LoginApi.class);
        Call<VerifiModel> call = loginApi.post_login(mMap);
        call.enqueue(new Callback<VerifiModel>() {
            @Override
            public void onResponse(Call<VerifiModel> call, final Response<VerifiModel> response) {
                if (response.isSuccessful()) {
                    Log.e("onResponse: ", response.toString());
                    Log.e("success: ", response.body().getSuccess().toString());
                    if (response.body().getSuccess() == 1) {
                        Log.e("onResponse: ", response.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                saveData(mobile_no, response.body().getSid(), response.body().getSname(), response.body().getPath(), response.body().getMember_id());
                            }
                        });

                    } else if (response.body().getSuccess() == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(login_layout, "This Mobile number does not exist!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                                TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(getResources().getColor(android.R.color.white));
                                snackbar.show();

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(login_layout, "Some thing went wrong", Snackbar.LENGTH_SHORT);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                                TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(getResources().getColor(android.R.color.white));
                                snackbar.show();

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<VerifiModel> call, Throwable t) {
                Log.e("onFailure: ", t.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(login_layout, "Some thing went wrong", Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(getResources().getColor(android.R.color.white));
                        snackbar.show();

                    }
                });
            }
        });


    }


    private void saveData(String mobile_no, String s_id, String s_name, String path, String member_id) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString("mobile_no", mobile_no);
        mEditor.putString("s_id", s_id);
        mEditor.putString("s_name", s_name);
        mEditor.putString("path", path);
        mEditor.putString("member_id", member_id);
        mEditor.apply();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @AfterPermissionGranted(AllPermissionRC)
    private void getPermissions() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this, perms)) {
           getImeiNumber();

        }else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs permission to proceed.",
                    AllPermissionRC,
                    perms);
        }
    }



    public void getImeiNumber() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
       for (int i = 0 ; i < manager.getPhoneCount(); i++){
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               //getDeviceId() is Deprecated so for android O we can use getImei() method
               mMap.put("imei"+i ,createPartFromString(manager.getImei(i)));
           }
           else {
               mMap.put("imei"+i, createPartFromString(manager.getDeviceId(i)));
           }

       }

    }

    @NonNull
    private RequestBody createPartFromString(String val) {
        return RequestBody.create(okhttp3.MultipartBody.FORM,  val);
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected())
        {
            isAvailable = true;

        }
        return isAvailable;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        /*if (requestCode == AllPermissionRC) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImeiNumber();
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }*/
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {


            // Do something after user returned from app settings screen, like showing a Toast.

        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a complaint one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // hideKeyboard(LoginActivity.this);
        getPermissions();
    }
}
