package com.example.android.mqttservicetest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    private View view;
    ListView listView;

    ArrayList<String> historyList = new ArrayList<>();


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        listView = (ListView) view.findViewById(R.id.history_list_view);
//        historyList.add("warning");
//        Bundle bundle1 = getArguments();
//        String bundleMessage = bundle1.getString("id");
//        Log.i(TAG, "onCreateView: " + bundleMessage);
        Log.i(TAG, "onCreateView: create historyFragment");

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: start historyFragment");
        //History lastHistory = DataSupport.findLast(History.class);
        //historyList.add(lastHistory.getMessage());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getData().historyList);
        listView.setAdapter(adapter);

    }

    private Data getData() {
        return ((Data)getActivity().getApplicationContext());
    }
}
