package com.binarycraft.acland.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.binarycraft.acland.R;
import com.binarycraft.acland.entity.Mouza;
import com.binarycraft.acland.entity.Union;

import java.util.Vector;

/**
 * Created by user pc on 3/28/2016.
 */
public class MouzaSpinnerAdapter extends ArrayAdapter<Mouza> {
    Context context;
    Vector<Mouza> mouzas;
    int resId;

    public MouzaSpinnerAdapter(Context context, int resource, Vector<Mouza> mouzas) {
        super(context, resource, mouzas);
        this.context = context;
        this.mouzas = mouzas;
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
        tvName.setText(mouzas.get(position).getName());
        return row;
    }
}
