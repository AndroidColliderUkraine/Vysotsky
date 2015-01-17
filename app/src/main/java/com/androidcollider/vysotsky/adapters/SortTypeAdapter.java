package com.androidcollider.vysotsky.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidcollider.vysotsky.R;

import java.util.ArrayList;

/**
 * Created by pseverin on 24.12.14.
 */
public class SortTypeAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater lInflater;
    public ArrayList<String> sortTypesList;
    //public ArrayList<Route> allRouteArrayList;

    public SortTypeAdapter(Context context, ArrayList<String> sortTypesList) {
        this.context = context;
        this.sortTypesList = sortTypesList;

        lInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    // the number of elements
    @Override
    public int getCount() {
        return sortTypesList.size();
    }

    // element at position
    @Override
    public Object getItem(int position) {
        return sortTypesList.get(position);
    }

    // id at position
    @Override
    public long getItemId(int position) {
        return position;
    }

    // create viewholder
    static class ViewHolder {
        public TextView tv_sort_type;
    }

    // list item_for_routes_listview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_sort_type, parent, false);

            holder = new ViewHolder();
            holder.tv_sort_type = (TextView) view.findViewById(R.id.tv_sort_type_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_sort_type.setText((String) getItem(position));

        return view;
    }



   /* public void updateRouteLists(ArrayList<Route> list){
        this.routeArrayList.clear();
        this.routeArrayList.addAll(list);
        this.allRouteArrayList.clear();
        this.allRouteArrayList.addAll(list);
    }*/
}
