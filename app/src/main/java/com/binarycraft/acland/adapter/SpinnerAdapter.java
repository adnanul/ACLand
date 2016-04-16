package com.binarycraft.acland.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.binarycraft.acland.R;
import com.binarycraft.acland.entity.Mouza;

import java.util.Vector;

/**
 * Created by user pc on 3/28/2016.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    Vector<String> names;
    int resId;

    public SpinnerAdapter(Context context, int resource, Vector<String> names) {
        super(context, resource, names);
        this.context = context;
        this.names = names;
        this.resId = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
        
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(resId, parent, false);
        TextView tvName = (TextView) row.findViewById(R.id.tvSpinnerItem);
        tvName.setText(names.get(position));
        return row;
    }
}
