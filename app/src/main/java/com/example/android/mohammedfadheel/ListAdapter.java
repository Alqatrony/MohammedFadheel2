package com.example.android.mohammedfadheel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    LayoutInflater inflater;
    private List<Data> Datalist = null;
    private ArrayList<Data> arrayList;
    Context context;

    public ListAdapter (Activity context, List<Data> openSite) {
        this.context = context;
        this.Datalist = openSite;
        inflater = LayoutInflater.from(context);
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(openSite);
    }

    @Override
    public int getCount(){
        return Datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return Datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        View Item = inflater.inflate(R.layout.sura_list_item, null, true);

        LinearLayout LL = (LinearLayout) Item.findViewById(R.id.Li);

        LL.setBackgroundColor(Color.parseColor("#170c4c"));

        TextView txtTitle = (TextView) Item.findViewById(R.id.title);

        txtTitle.setText(Datalist.get(position).getSubject());

        Item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(context,Playscreen.class);
                i.putExtra("name", Datalist.get(position).getSubject());
                i.putExtra("url", Datalist.get(position).getLink());
                context.startActivity(i);
            }
        });

        if (position % 2 == 0){
            LL.setBackgroundColor(Color.parseColor("#038001"));
            txtTitle.setTextColor(Color.parseColor("#ffffff"));
        }

        return Item;
    }
}
