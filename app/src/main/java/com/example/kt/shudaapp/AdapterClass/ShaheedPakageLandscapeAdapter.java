package com.example.kt.shudaapp.AdapterClass;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kt.shudaapp.ModelClasses.ShaheedPakageModel;
import com.example.kt.shudaapp.R;

import java.util.List;

public class ShaheedPakageLandscapeAdapter extends RecyclerView.Adapter<ShaheedPakageLandscapeAdapter.MyViewHolder> {
    private Context mContext;
    private List<ShaheedPakageModel.Val> mList;
    public ShaheedPakageLandscapeAdapter(Context context, List<ShaheedPakageModel.Val> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the layout file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shaheed_pakage_item_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#EDEDED"));
            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
        }
        holder.name_tv.setText(mList.get(position).getSname());
        holder.relation_tv.setText(mList.get(position).getRelations());
        holder.pkg_type_tv.setText(mList.get(position).getPackageName());
        holder.details_tv.setText(mList.get(position).getPackageDetails());
        if (mList.get(position).getPackageStatus().equals("1")){
            holder.status_tv.setText("Yes");
        }else {
            holder.status_tv.setText("No");
        }






    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name_tv, relation_tv, pkg_type_tv, details_tv, status_tv;
        public MyViewHolder(View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.member_name);
            relation_tv = itemView.findViewById(R.id.relation);
            pkg_type_tv = itemView.findViewById(R.id.pakage);
            details_tv = itemView.findViewById(R.id.details);
            status_tv = itemView.findViewById(R.id.status);
        }
    }
}
