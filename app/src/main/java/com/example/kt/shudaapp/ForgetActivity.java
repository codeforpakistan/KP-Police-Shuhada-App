package com.example.kt.shudaapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kt.shudaapp.InterfaceClasses.ForgetApi;
import com.example.kt.shudaapp.InterfaceClasses.LoginApi;
import com.example.kt.shudaapp.ModelClasses.RegisterModel;
import com.example.kt.shudaapp.Utils.Config;
import com.kirianov.multisim.MultiSimTelephonyManager;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgetActivity extends AppCompatActivity {
    private EditText forget_mobile_no_et;
    private static final int Request_Imei=900;
    MultiSimTelephonyManager multiSimTelephonyManager;
    int sim_slot;
    HashMap<String, RequestBody> mMap = new HashMap<>();
    ConstraintLayout login_layout;
    ProgressDialog progressDialog;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        /*mToolbar = findViewById(R.id.forget_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        forget_mobile_no_et = findViewById(R.id.forget_mobile_no_et);
        login_layout = findViewById(R.id.login_layout);
        progressDialog = new ProgressDialog(this);
        checkPhoneState();
    }



    public void proceedForget(View view) {
        String mobile_no = forget_mobile_no_et.getText().toString();
        if (TextUtils.isEmpty(mobile_no)){
            forget_mobile_no_et.setError("Enter mobile no");
            forget_mobile_no_et.requestFocus();
            Snackbar snackbar = Snackbar
                    .make(login_layout, "Enter mobile no", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            snackbar.show();

        }else {
            if (isNetworkAvailable()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideKeyboard(ForgetActivity.this);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                    }
                });
                postData(mobile_no);

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(login_layout, "No internet Connection", Snackbar.LENGTH_SHORT);
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


    private void postData(final String mobile_no) {
        mMap.put("mobile_no", createPartFromString(mobile_no));
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
        final ForgetApi forgetApi =  retrofit.create(ForgetApi.class);
        Call<RegisterModel> call = forgetApi.post_forgot(mMap);
        call.enqueue(new Callback<RegisterModel>() {
            @Override
            public void onResponse(Call<RegisterModel> call, Response<RegisterModel> response) {
                if (response.isSuccessful()){
                    if (response.body().getSuccess() == 1){
                        Log.e("onResponse: ", response.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(ForgetActivity.this)
                                        .setTitle("Request received")
                                        .setMessage("Please wait our Focal person will contact you soon after verification new password will be sent to your registered mobile no")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }

                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        });

                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(login_layout, "This Mobile number does not exist!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                                TextView textView =  snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(getResources().getColor(android.R.color.white));
                                snackbar.show();

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterModel> call, Throwable t) {
                Log.e("onFailure: ", t.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(login_layout, "Some thing went wrong", Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                        TextView textView =  snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(getResources().getColor(android.R.color.white));
                        snackbar.show();

                    }
                });
            }
        });


    }

    private void checkPhoneState() {
        if (ContextCompat.checkSelfPermission(ForgetActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},Request_Imei);
        }else {
            useInfo();


        }
    }

    public void useInfo() {
        multiSimTelephonyManager = new MultiSimTelephonyManager(this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

// get info from each slot:
                if (multiSimTelephonyManager != null) {
                    sim_slot = multiSimTelephonyManager.sizeSlots();
                    if (sim_slot == 1){
                        mMap.put("imei0",createPartFromString(multiSimTelephonyManager.getSlot(0).getImei()));
                    }else if (sim_slot == 2){
                        for(int i = 0; i < multiSimTelephonyManager.sizeSlots(); i++) {
                            mMap.put("imei"+i, createPartFromString(multiSimTelephonyManager.getSlot(i).getImei()));
                            Log.e("dual sim: ", mMap.toString());
                        }
                    }else {

                    }

                }
            }
        });

    }

    @NonNull
    private RequestBody createPartFromString(String val) {
        return RequestBody.create(okhttp3.MultipartBody.FORM,  val);
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

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Request_Imei) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                useInfo();

            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
