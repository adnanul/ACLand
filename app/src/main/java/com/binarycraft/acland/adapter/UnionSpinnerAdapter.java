package com.binarycraft.acland.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.binarycraft.acland.R;
import com.binarycraft.acland.entity.Union;

import java.util.Vector;

/**
 * Created by user pc on 3/28/2016.
 */
public class UnionSpinnerAdapter extends ArrayAdapter<Union> {
    Context context;
    Vector<Union> unions;
    int resId;

    public UnionSpinnerAdapter(Context context, int resource, Vector<Union> unions) {
        super(context, resource, unions);
        this.context = context;
        this.unions = unions;
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
        tvName.setText(unions.get(position).getName());
        return row;
    }
}
