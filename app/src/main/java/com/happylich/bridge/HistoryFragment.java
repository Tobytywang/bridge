package com.happylich.bridge;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happylich.bridge.dummy.DummyContent;
import com.happylich.bridge.dummy.DummyContent.DummyItem;
import com.happylich.bridge.game.database.DBHelper;
import com.happylich.bridge.game.scene.Count;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HistoryFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HistoryFragment newInstance(int columnCount) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(this.getClass().getName(), "创建HistoryFragment");

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.list);
        if (mRecyclerView instanceof RecyclerView) {
            Context context = mRecyclerView.getContext();
            RecyclerView recyclerView = (RecyclerView) mRecyclerView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            // 将数据读出的数据作为参数传入

            DBHelper mDBHelper = new DBHelper(context,"history.db",null,1);
            SQLiteDatabase db = mDBHelper.getReadableDatabase();
            Cursor cursor = db.query("history", null, null, null, null, null, null);
            List<Count> counts = new ArrayList<Count>();
            Log.v(this.getClass().getName(), "读取到" + String.valueOf(cursor.getCount()) + "条数据");
            while (cursor.moveToNext()) {
                Log.v(this.getClass().getName(), "执行添加动作");
                String _id = cursor.getString(0);
                int gameType = cursor.getInt(1);
                int playerDirection = cursor.getInt(2);
                int banker = cursor.getInt(7);
                int contract = cursor.getInt(8);
                Count count = new Count(_id, gameType, playerDirection, banker, contract);
                counts.add(count);
            }

            Log.v(this.getClass().getName(), "添加了" + String.valueOf(counts.size()) + "条数据");

//            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent.ITEMS, mListener));

            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(counts, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Count item);
    }
}
