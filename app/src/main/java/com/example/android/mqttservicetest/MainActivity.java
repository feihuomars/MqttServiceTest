package com.example.android.mqttservicetest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    public FragmentTabHost fragmentTabHost;
    FragmentManager fragmentManager;

    ArrayList<String> historyList = new ArrayList<>();


    MqttAndroidClient mqttAndroidClient;
    final String serverUri = "tcp://47.94.246.26:1883";
    String[] topics = {"warning", "warnings"};
    int[] qoss = {0, 0};
    IMqttMessageListener[] iMqttMessageListeners = {new IMqttMessageListener() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "messageArrived: " + new String (message.getPayload()));
        }
    }, new IMqttMessageListener() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "messageArrived: " + new String (message.getPayload()));
        }
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.initialize(this);
        Connector.getDatabase();

        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        Bundle bundle = new Bundle();

        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("history").setIndicator("历史"), HistoryFragment.class, bundle);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("订阅").setIndicator("订阅"), SubscriptionFragment.class, bundle);

        historyList.add("warning");

        fragmentManager = getSupportFragmentManager();
        HistoryFragment historyFragment = new HistoryFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("id", "bundle transaction");


        historyFragment.setArguments(bundle1);
        fragmentManager.beginTransaction().replace(android.R.id.tabcontent, historyFragment);
        Log.i(TAG, "onCreate: " + bundle1.getString("id"));



        List<History> selectedList = DataSupport.findAll(History.class);
        ArrayList<String> data = new ArrayList<String>();
        for (History history: selectedList){
            Log.i(TAG, "onCreate: " + history.getMessage());
            ((Data)getApplicationContext()).historyList.add(history.getMessage());
        }

        //((Data)getApplicationContext()).historyList.add(DataSupport.findLast(History.class).getMessage());

        initMqtt();
    }

    private void initMqtt() {
        String clientId = MqttClient.generateClientId();
        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), serverUri,
                        clientId);


        try {
            mqttAndroidClient.connect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    HistoryFragment historyFragment = (HistoryFragment) getSupportFragmentManager().findFragmentByTag("history");
                    View view = historyFragment.getView();
                    final TextView textView = (TextView) view.findViewById(R.id.history_text);
                    //final ListView listView = view.findViewById(R.id.history_list_view);


                    textView.setText("success");

                    try {
                        mqttAndroidClient.subscribe("warning", 0, new IMqttMessageListener() {
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                String recvMessage = new String (message.getPayload());
                                Log.i(TAG, "messageArrived: " + recvMessage);
                                //historyList.add(recvMessage);
                                History history = new History();
                                history.setMessage(recvMessage);
                                history.save();
                            }
                        });

                        //mqttAndroidClient.subscribe(topics, qoss, iMqttMessageListeners);

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    History lastHistory = DataSupport.findLast(History.class);
                    Log.i(TAG, "database: " + lastHistory.getMessage());
                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, historyList);
                    //listView.setAdapter(adapter);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
            
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: destroy Mainactivity");
        ((Data)getApplicationContext()).historyList.clear();
    }
}