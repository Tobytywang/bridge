package com.happylich.bridge.game.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.happylich.bridge.R;
import com.happylich.bridge.game.player.ProxyPlayer;

import java.util.ArrayList;

/**
 * Created by lich on 2018/5/18.
 */

public class RoomAdapter extends BaseAdapter {

    private ArrayList<RoomBean> mList;
    private Context mContext;

    public RoomAdapter(ArrayList<RoomBean> list, Context context) {
        mList = list;
        mContext = context;
    }

    public void refresh(ArrayList<RoomBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new Holder();
            holder.mIP = (TextView)convertView.findViewById(R.id.ip_text);
            holder.mSTATE = (TextView)convertView.findViewById(R.id.state_text);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.mIP.setText(mList.get(getCount() - position - 1).getIP());
        holder.mSTATE.setText(mList.get(getCount() - position - 1).getState());
        return convertView;
    }

    class Holder {
        private TextView mIP;
        private TextView mSTATE;
    }
}
