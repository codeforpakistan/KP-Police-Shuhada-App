package com.example.kt.shudaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.kt.shudaapp.InterfaceClasses.RegisterApi;
import com.example.kt.shudaapp.ModelClasses.RegisterModel;
import com.example.kt.shudaapp.Utils.Config;
import com.kirianov.multisim.MultiSimTelephonyManager;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {
MultiSimTelephonyManager multiSimTelephonyManager;
int sim_slot;
    HashMap<String, RequestBody> mMap = new HashMap<>();
    SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       /* multiSimTelephonyManager = new MultiSimTelephonyManager(this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                useInfo();
            }
        });*/
       new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        /*if (isNetworkAvailable()){
            mSharedPreferences = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
            String mobile_no = mSharedPreferences.getString("mobile_no", "No Data");
            if (TextUtils.isEmpty(mobile_no)){
                startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
                finish();
            }else{
                //postData(mobile_no);
            }

        }*/
    }

/*
    public void useInfo() {

        // get number of slots:
        if (multiSimTelephonyManager != null) {
            sim_slot = multiSimTelephonyManager.sizeSlots();
        }
        // get info from each slot:
        if (multiSimTelephonyManager != null) {
            if (sim_slot == 1){
                mMap.put("imei1",createPartFromString(multiSimTelephonyManager.getSlot(0).getImei()));
                mMap.put("imsi1",createPartFromString(multiSimTelephonyManager.getSlot(0).getImsi()));
            }else if (sim_slot == 2){
                for(int i = 0; i < multiSimTelephonyManager.sizeSlots(); i++) {
                    mMap.put("imei"+i, createPartFromString(multiSimTelephonyManager.getSlot(i).getImei()));
                    mMap.put("imsi"+i, createPartFromString("1212121212121212"));
                    Log.e("dual sim: ", mMap.toString());
                    Log.e("sim info", multiSimTelephonyManager.getSlot(i).getImei()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).getImsi()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).getSimSerialNumber()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).getSimState()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).getSimOperator()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).getSimOperatorName()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).getSimCountryIso()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).getNetworkOperator()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).getNetworkOperatorName()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).getNetworkCountryIso()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).getNetworkType()+"\n"+
                                    multiSimTelephonyManager.getSlot(i).isNetworkRoaming());
                }
            }else {

            }

        }
    }
*/

    private void postData(final String mobile_no) {
        mMap.put("mobile_no", createPartFromString(mobile_no));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BaseUrl)
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
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(3000);
                                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                            }
                        });

                    }else {

                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterModel> call, Throwable t) {

            }
        });


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

}
