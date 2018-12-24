package com.example.kt.shudaapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kt.shudaapp.AdapterClass.HelpDeskAdapter;
import com.example.kt.shudaapp.ModelClasses.DistrictShuhada;

import java.util.ArrayList;

public class HelpDesk extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<DistrictShuhada> mArrayList = new ArrayList<>();
    private DistrictShuhada mDistrictShuhada;
    SearchView mSearchView;
    HelpDeskAdapter helpDeskAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_desk);
        mListView = findViewById(R.id.list);


        populateList();

    }

    private void populateList() {
        mDistrictShuhada = new DistrictShuhada("CCPO/Peshawar", "091-9210610");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("CTD", "091-9212591");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("Special branch ", "091-9216934");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("FRP/Headquarter ", "091-9210945");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO Mardan ", "0937-9230712");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO Abbottabad ", "0992-337035");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO Buner ", "0939-510032");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO Bannu ", "0928-9270045");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO Kohat ", "0922-9260117");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO Dir Lower ", "0945-9250074");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO Dir Upper ", "0944-880493");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO Nowshera ", "0923-9220102");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO Swat ", "0946-9240402");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO Kohistan ", "0998-407004");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO BATTAGRAM", "0997-310036");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO DIKHAN", "0966-712072");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO KARAK", "0927-210693");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO HANGU", "0925-623878");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO TANK", "0963-510565");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO TORGHAR", "0343-9535889");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO LAKKI MARWAT", "0969-538250");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO MANSEHRA", "0997-307523");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO SWABI", "0938-223390");
        mArrayList.add(mDistrictShuhada);

        mDistrictShuhada = new DistrictShuhada("DPO HARIPUR", "0995-627068");
        mArrayList.add(mDistrictShuhada);


        helpDeskAdapter = new HelpDeskAdapter(HelpDesk.this, mArrayList);
        mListView.setAdapter(helpDeskAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.whm_home_search);
        mSearchView = (SearchView) item.getActionView();
        changeSearchViewTextColor(mSearchView);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mArrayList.clear();
                    populateList();

                } else {
                    Filter filter = helpDeskAdapter.getFilter();
                    filter.filter(newText);
                }
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }
}
