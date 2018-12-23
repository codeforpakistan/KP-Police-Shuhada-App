package com.example.kt.shudaapp.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kt.shudaapp.ModelClasses.DistrictShuhada;
import com.example.kt.shudaapp.R;

import java.util.ArrayList;

public class HelpDeskAdapter extends ArrayAdapter<DistrictShuhada> implements Filterable {

    public Context context;
    private ArrayList<DistrictShuhada> mOriginalValues; // Original Values
    private ArrayList<DistrictShuhada> mDisplayedValues;    // Values to be displayed
    public HelpDeskAdapter(Context context, ArrayList<DistrictShuhada> mOriginalValues ) {
        super(context, R.layout.district_item_layout, mOriginalValues);
        this.context = context;
        this.mOriginalValues = mOriginalValues;
        this.mDisplayedValues = mOriginalValues;
    }

    public class DistrictDataHolder
    {
        TextView name, number;
        RelativeLayout call_btn;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        DistrictDataHolder holder ;
        if(convertView==null)
        {
            holder = new DistrictDataHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.district_item_layout, parent, false);
            holder.name = convertView.findViewById(R.id.name);
            holder.number = convertView.findViewById(R.id.number);
            holder.call_btn = convertView.findViewById(R.id.call_btn);
            convertView.setTag(holder);
        }
        else
        {
            holder=(DistrictDataHolder) convertView.getTag();
        }

        holder.name.setText(mDisplayedValues.get(position).getDistrict_unit());
        holder.number.setText(mDisplayedValues.get(position).getNumber());
        holder.call_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mDisplayedValues.get(position).getNumber()));
                context.startActivity(intent);
            }
        });

        return convertView;

    }

    @Override
    public int getCount() {
        return mDisplayedValues.size();
    }



    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mDisplayedValues = (ArrayList<DistrictShuhada>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<DistrictShuhada> FilteredArrList = new ArrayList<DistrictShuhada>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).district_unit;
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(new DistrictShuhada(mOriginalValues.get(i).getDistrict_unit(),mOriginalValues.get(i).getNumber()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    public ArrayList<DistrictShuhada> getList(){
        return mDisplayedValues;
    }

}
