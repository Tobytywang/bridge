package com.happylich.bridge;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happylich.bridge.HistoryFragment.OnListFragmentInteractionListener;
import com.happylich.bridge.dummy.DummyContent.DummyItem;
import com.happylich.bridge.game.scene.Count;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Count> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<Count> items, OnListFragmentInteractionListener listener) {

        Log.v(this.getClass().getName(), "创建Adapter");
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mGameTypeView.setText(String.valueOf(mValues.get(position).getGameType()));
        holder.mPlayerDirectionView.setText(String.valueOf(mValues.get(position).getPlayerDirection()));
        holder.mBankerView.setText(String.valueOf(mValues.get(position).getBanker()));
        holder.mContractView.setText(String.valueOf(mValues.get(position).getContract()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mGameTypeView;
        public final TextView mPlayerDirectionView;
        public final TextView mBankerView;
        public final TextView mContractView;
        public Count mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mGameTypeView = (TextView) view.findViewById(R.id.game_type);
            mPlayerDirectionView = (TextView) view.findViewById(R.id.player_direction);
            mBankerView = (TextView) view.findViewById(R.id.banker);
            mContractView = (TextView) view.findViewById(R.id.contract);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mGameTypeView.getText() + "'";
        }
    }
}
