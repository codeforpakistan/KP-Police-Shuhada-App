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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kt.shudaapp.InterfaceClasses.VerifiApi;
import com.example.kt.shudaapp.ModelClasses.VerifiModel;
import com.example.kt.shudaapp.Utils.Config;
import com.goodiebag.pinview.Pinview;
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

public class VerificationActivity extends AppCompatActivity {
    private RelativeLayout verifi_btn;
    private Pinview pinview;
    ProgressDialog progressDialog;
    String mobile_no, password;
    HashMap<String, RequestBody> mMap = new HashMap<>();
    LinearLayout verification_layout;
    SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static final int Request_Imei=300;
    TelephonyManager tm;
    String imei, pin_code;
    MultiSimTelephonyManager multiSimTelephonyManager;
    int sim_slot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        Intent intent = getIntent();
        mobile_no = intent.getStringExtra("mobile");
        password = intent.getStringExtra("password");
        mSharedPreferences = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        pinview = findViewById(R.id.pinview);
        progressDialog = new ProgressDialog(this);
        verification_layout = findViewById(R.id.verification_layout);

        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(final Pinview pinview, boolean fromUser) {
                //Make api calls here or what not
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (isNetworkAvailable()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideKeyboard(VerificationActivity.this);
                                    progressDialog.setMessage("Verifying...");
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();
                                    pin_code = pinview.getValue();
                                    postData(mobile_no, pin_code, password);
                                }
                            });

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Snackbar snackbar = Snackbar
                                            .make(verification_layout, "No internet Connection", Snackbar.LENGTH_SHORT);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                                    TextView textView =  snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                    textView.setTextColor(getResources().getColor(android.R.color.white));
                                    snackbar.show();
                                }
                            });

                        }

                    }
                });


            }
        });

        checkPhoneState();

    }

    public void useInfo() {

        // get number of slots:
        if (multiSimTelephonyManager != null) {
            sim_slot = multiSimTelephonyManager.sizeSlots();
        }

        // get info from each slot:
        if (multiSimTelephonyManager != null) {
            if (sim_slot == 1){
                mMap.put("imei1",createPartFromString(multiSimTelephonyManager.getSlot(0).getImei()));
            }else if (sim_slot == 2){
                for(int i = 0; i < multiSimTelephonyManager.sizeSlots(); i++) {
                    mMap.put("imei"+i, createPartFromString(multiSimTelephonyManager.getSlot(i).getImei()));

                }
            }else {

            }

        }
    }

    private void postData(final String mobile_no, String pin_code, String password) {
        mMap.put("mobile_no", createPartFromString(mobile_no));
        mMap.put("pincode", createPartFromString(pin_code));
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
        VerifiApi verifiApi =  retrofit.create(VerifiApi.class);
        Call<VerifiModel> call = verifiApi.post_verifi(mMap);
        call.enqueue(new Callback<VerifiModel>() {
            @Override
            public void onResponse(Call<VerifiModel> call, final Response<VerifiModel> response) {
                if (response.isSuccessful()){
                    if (response.body().getSuccess() == 1){
                        Log.e("onResponse: ", response.toString());
                        Log.e("s_id: ", response.body().getSid());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                saveData(mobile_no, response.body().getSid(), response.body().getSname(), response.body().getPath(), response.body().getMember_id());

                            }
                        });

                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(verification_layout, "Some thing went wrong", Snackbar.LENGTH_SHORT);
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
            public void onFailure(Call<VerifiModel> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(verification_layout, "Some thing went wrong", Snackbar.LENGTH_SHORT);
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

    private void saveData(String mobile_no, String s_id, String s_name, String path, String member_id) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString("mobile_no", mobile_no);
        mEditor.putString("s_id", s_id);
        mEditor.putString("s_name", s_name);
        mEditor.putString("path", path);
        mEditor.putString("member_id", member_id);
        mEditor.commit();
        Intent intent = new Intent(VerificationActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
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

    private void checkPhoneState() {
        if (ContextCompat.checkSelfPermission(VerificationActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},Request_Imei);
        }else {
            multiSimTelephonyManager = new MultiSimTelephonyManager(this, new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    useInfo();
                }
            });

        }
        /*if (ContextCompat.checkSelfPermission(VerificationActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},Request_Imei);
        }else {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                imei = tm.getImei();
                mMap.put("imei", createPartFromString(imei));
            }else {
                imei = tm.getDeviceId();
                mMap.put("imei", createPartFromString(imei));
            }
        }*/
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Request_Imei) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                multiSimTelephonyManager = new MultiSimTelephonyManager(this, new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        useInfo();
                    }
                });
                /*tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    imei = tm.getImei();
                    mMap.put("imei", createPartFromString(imei));
                }else {
                    imei = tm.getDeviceId();
                    mMap.put("imei", createPartFromString(imei));
                }*/
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
