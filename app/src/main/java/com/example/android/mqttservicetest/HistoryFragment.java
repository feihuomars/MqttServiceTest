package com.example.android.mqttservicetest;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    private View view;
    ListView listView;

    //ArrayList<String> historyList = new ArrayList<>();


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
        Log.i(TAG, "onCreateView: createView historyFragment");

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: start HistoryFragment");
        //HistoryDB lastHistory = DataSupport.findLast(HistoryDB.class);
        //historyList.add(lastHistory.getMessage());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getData().historyList);
        listView.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: destroy HistoryFragment ");
        getData().historyList.clear();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: attach HistoryFragment");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: create HistoryFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: resume HistoryFragment");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: pause HistoryFragment");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: stop HistoryFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: destroyView HistoryFragment");
        //getData().historyList.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: detach HistoryFragment");
    }

    private Data getData() {
        return ((Data)getActivity().getApplicationContext());
    }
}
