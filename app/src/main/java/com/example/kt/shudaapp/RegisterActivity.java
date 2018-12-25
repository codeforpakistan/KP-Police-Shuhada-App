package com.example.kt.shudaapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kt.shudaapp.InterfaceClasses.RegisterApi;
import com.example.kt.shudaapp.ModelClasses.RegisterModel;
import com.example.kt.shudaapp.Utils.Config;
import com.kirianov.multisim.MultiSimTelephonyManager;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private Button register_btn;
    private EditText mobile_no_et, password_et, confirm_password;
    HashMap<String, RequestBody> mMap = new HashMap<>();
    ProgressDialog progressDialog;
    ConstraintLayout register_layout;
    String mobile_no;
    String password;
    String confirmpassword;
    private static final int AllPermissionRC = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getPermissions();
        //hideKeyboard(this);
        register_btn = findViewById(R.id.register_btn);
        mobile_no_et = findViewById(R.id.reg_mobile_no_et);
        password_et = findViewById(R.id.reg_password_no_et);
        confirm_password = findViewById(R.id.reg_password_no_et2);
        register_layout = findViewById(R.id.register_layout);
        progressDialog = new ProgressDialog(this);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mobile_no = mobile_no_et.getText().toString();
                password = password_et.getText().toString();
                confirmpassword = confirm_password.getText().toString();
                if (TextUtils.isEmpty(mobile_no)){
                    mobile_no_et.setError("Enter mobile no");
                    mobile_no_et.requestFocus();
                    Snackbar snackbar = Snackbar
                            .make(register_layout, "Enter mobile no", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                    TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                    snackbar.show();
                }else if(TextUtils.isEmpty(password)) {
                    password_et.requestFocus();
                    Snackbar snackbar = Snackbar
                            .make(register_layout, "Enter password", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                    TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                    snackbar.show();
                }else if(TextUtils.isEmpty(confirmpassword)) {
                    password_et.requestFocus();
                    Snackbar snackbar = Snackbar
                            .make(register_layout, "Confirm password", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                    TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                    snackbar.show();
                }else if (!confirmpassword.equals(password)){
                    confirm_password.requestFocus();
                    Snackbar snackbar = Snackbar
                            .make(register_layout, "Confirm password not matched", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                    TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                    snackbar.show();
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (isNetworkAvailable()) {
                                    hideKeyboard(RegisterActivity.this);
                                    progressDialog.setTitle("Registrating");
                                    progressDialog.setMessage("Please wait...");
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();
                                    postData(mobile_no, password);

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Snackbar snackbar = Snackbar.make(register_layout, "No internet Connection", Snackbar.LENGTH_SHORT);
                                        View snackBarView = snackbar.getView();
                                        snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                                        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                        textView.setTextColor(getResources().getColor(android.R.color.white));
                                        snackbar.show();
                                    }
                                });

                            }

                        }
                    });
                }



            }
        });

        //checkPhoneState();
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


    private void postData(final String mobile_no, final String password) {
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
        final RegisterApi registerApi =  retrofit.create(RegisterApi.class);
        Call<RegisterModel> call = registerApi.post_reg(mMap);
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
                                Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
                                intent.putExtra("mobile", mobile_no);
                                intent.putExtra("password", password);
                                startActivity(intent);
                            }
                        });

                    }else if (response.body().getSuccess() == 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(register_layout, "This Mobile number does not exist!", Snackbar.LENGTH_LONG);
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
                                .make(register_layout, "Some thing went wrong", Snackbar.LENGTH_SHORT);
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


}
