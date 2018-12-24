package com.example.kt.shudaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.kt.shudaapp.AdapterClass.ShaheedPakageLandscapeAdapter;
import com.example.kt.shudaapp.InterfaceClasses.PakageApi;
import com.example.kt.shudaapp.ModelClasses.ShaheedPakageModel;
import com.example.kt.shudaapp.Utils.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShaeedPakageLandscapeActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ShaheedPakageModel shaheedPakageModel;
    private List<ShaheedPakageModel.Val> list = new ArrayList<>();
    private ShaheedPakageLandscapeAdapter shaheedPakageAdapter;
    SharedPreferences sharedPreferences;
    HashMap<String, RequestBody> map = new HashMap<>();
    String s_id;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaeed_pakage_lanscape);
        sharedPreferences = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        s_id = sharedPreferences.getString("s_id", "No Data");
        swipeRefreshLayout = findViewById(R.id.package_swipe);
        mRecyclerView = findViewById(R.id.shaheed_pakage_recycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        if (isNetworkAvailable()){
            loadList(s_id);
        }else {
            swipeRefreshLayout.setRefreshing(true);
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.shaheed_pakage_layout), "No internet connection", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
            TextView textView =  snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            snackbar.show();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable()){
                    loadList(s_id);
                }else {
                    swipeRefreshLayout.setRefreshing(true);
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.shaheed_pakage_layout), "No internet connection", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                    TextView textView =  snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                    snackbar.show();
                }
            }
        });

    }

    private void loadList(String s_id) {
        swipeRefreshLayout.setRefreshing(true);
        map.put("sid", createPartFromString(s_id));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PakageApi pakageApi =  retrofit.create(PakageApi.class);
        Call<ShaheedPakageModel> call = pakageApi.post_complaint(map);
        call.enqueue(new Callback<ShaheedPakageModel>() {
            @Override
            public void onResponse(Call<ShaheedPakageModel> call, Response<ShaheedPakageModel> response) {
                if (response.isSuccessful()){
                    if (response.body().getSuccess() == 1){
                        swipeRefreshLayout.setRefreshing(false);
                        list = response.body().getVal();
                        shaheedPakageAdapter = new ShaheedPakageLandscapeAdapter(ShaeedPakageLandscapeActivity.this, list);
                        mRecyclerView.setAdapter(shaheedPakageAdapter);
                        shaheedPakageAdapter.notifyDataSetChanged();
                    }else {
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.shaheed_pakage_layout), "No record found", Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                        TextView textView =  snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(getResources().getColor(android.R.color.white));
                        snackbar.show();
                    }

                }else {
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.shaheed_pakage_layout), "Some thing went wrong", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                    TextView textView =  snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<ShaheedPakageModel> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.shaheed_pakage_layout), "Some thing went wrong", Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                TextView textView =  snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                snackbar.show();
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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.pakage_lanscape_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.potrait_mode:
                startActivity(new Intent(ShaeedPakageLandscapeActivity.this, ShaeedPakagePotraitActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
