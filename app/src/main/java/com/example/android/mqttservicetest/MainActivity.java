package com.example.android.mqttservicetest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    public FragmentTabHost fragmentTabHost;
    FragmentManager fragmentManager;

    ArrayList<String> historyList = new ArrayList<>();


    MqttAndroidClient mqttAndroidClient;
    final String serverUri = "tcp://47.94.246.26:1883";
    String[] topics;
    Integer[] qossInteger;
    int[] qoss;

    String[] testTopics = {"warning", "warnings"};
    int[] testQoss = {0, 0};

    IMqttMessageListener topic1Listener = new IMqttMessageListener() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String recvMessage = new String(message.getPayload());
            Log.i(TAG, topic + " messageArrived: " + recvMessage);
        }
    };

    IMqttMessageListener topic2Listener = new IMqttMessageListener() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String recvMessage = new String(message.getPayload());
            Log.i(TAG, topic + " messageArrived: " + recvMessage);
        }
    };

    IMqttMessageListener topic3Listener = new IMqttMessageListener() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String recvMessage = new String(message.getPayload());
            Log.i(TAG, topic + " messageArrived: " + recvMessage);
        }
    };

    IMqttMessageListener[] iMqttMessageListeners = {topic1Listener, topic2Listener, topic3Listener};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: create MainActivity");
        LitePal.initialize(this);
        Connector.getDatabase();

        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        Bundle bundle = new Bundle();

        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("history").setIndicator("历史"), HistoryFragment.class, bundle);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("订阅").setIndicator("订阅"), SubscriptionFragment.class, bundle);

        historyList.add("warning");

//        fragmentManager = getSupportFragmentManager();
//        HistoryFragment historyFragment = new HistoryFragment();
//        Bundle bundle1 = new Bundle();
//        bundle1.putString("id", "bundle transaction");
//        historyFragment.setArguments(bundle1);
//        fragmentManager.beginTransaction().replace(android.R.id.tabcontent, historyFragment);
//        Log.i(TAG, "onCreate: " + bundle1.getString("id"));

        //预先存入数据做测试
//        DataSupport.deleteAll(HistoryDB.class);
//        SubscriptionDB subscription0 = new SubscriptionDB();
//        subscription0.setTopic("warning");
//        subscription0.setQos(0);
//        subscription0.save();
//
//        SubscriptionDB subscription1 = new SubscriptionDB();
//        subscription1.setTopic("warnings");
//        subscription1.setQos(0);
//        subscription1.save();
//
//        SubscriptionDB subscription2 = new SubscriptionDB();
//        subscription2.setTopic("test");
//        subscription2.setQos(0);
//        subscription2.save();

        List<HistoryDB> selectedList = DataSupport.findAll(HistoryDB.class);
        for (HistoryDB historyDB : selectedList){
            Log.i(TAG, "onCreate: " + historyDB.getMessage());
            ((Data)getApplicationContext()).historyList.add(0, historyDB.getMessage());     //逆序添加元素进list
        }
        //从数据库中查出订阅消息
        List<SubscriptionDB> foundSubscription = DataSupport.findAll(SubscriptionDB.class);
        for (SubscriptionDB subscriptionDB : foundSubscription) {
            ((Data)getApplicationContext()).subscriptionList.add(subscriptionDB.getTopic());
        }

        ArrayList<String> topicList = new ArrayList<>();
        ArrayList<Integer> qosList = new ArrayList<>();
        for (SubscriptionDB subscriptionDB : foundSubscription) {
            Log.i(TAG, "onCreate: topic:" + subscriptionDB.getTopic());
            Log.i(TAG, "onCreate: qos:" + subscriptionDB.getQos());
            topicList.add(subscriptionDB.getTopic());
            qosList.add(subscriptionDB.getQos());
        }
        topics = new String[topicList.size()];
        qossInteger = new Integer[qosList.size()];
        qoss = new int[qosList.size()];
        topicList.toArray(topics);
        qosList.toArray(qossInteger);
        for (int i = 0; i < qossInteger.length; i++) {
            qoss[i] = qossInteger[i].intValue();
        }
        Log.i(TAG, "onCreate: topic array:" + Arrays.toString(topics) + Arrays.toString(qossInteger));
        //((Data)getApplicationContext()).historyList.add(DataSupport.findLast(HistoryDB.class).getMessage());

        initMqtt();
    }

    private void initMqtt() {
        String clientId = MqttClient.generateClientId();
        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), serverUri,
                        clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String recvMessage = new String (message.getPayload());
                Log.i(TAG, "messageArrived: topic: " + topic + "message: " + recvMessage + "time:" + System.currentTimeMillis());

                //设置notification通知
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(MainActivity.this)
                        .setContentTitle(topic)
                        .setContentText(recvMessage)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pi)
                        .build();
                manager.notify(1, notification);

                ((Data)getApplicationContext()).historyList.add(0, recvMessage);
                HistoryDB historyDB = new HistoryDB();
                historyDB.setMessage(recvMessage);
                historyDB.save();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

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
                        mqttAndroidClient.subscribe(topics, qoss, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.i(TAG, "onSuccess: subscriptions succeed");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.i(TAG, "onFailure: subscriprions failed");
                            }
                        });
//                        mqttAndroidClient.subscribe("warning", 0, new IMqttMessageListener() {
//                            @Override
//                            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                                String recvMessage = new String (message.getPayload());
//                                Log.i(TAG, "messageArrived: " + recvMessage);
//                                //historyList.add(recvMessage);
//                                HistoryDB historyDB = new HistoryDB();
//                                historyDB.setMessage(recvMessage);
//                                historyDB.save();
//                            }
//                        });
                        
//                        mqttAndroidClient.subscribe(topics, qoss, iMqttMessageListeners);

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    HistoryDB lastHistoryDB = DataSupport.findLast(HistoryDB.class);
                    //Log.i(TAG, "database: " + lastHistoryDB.getMessage());
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
        ((Data) getApplicationContext()).subscriptionList.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: start MainActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: resume MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: pause MainActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: stop MainActivity");
        ((Data)getApplicationContext()).historyList.clear();
        ((Data) getApplicationContext()).subscriptionList.clear();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: restart MainActivity");

        List<HistoryDB> selectedList = DataSupport.findAll(HistoryDB.class);
        for (HistoryDB historyDB : selectedList){
            Log.i(TAG, "onCreate: " + historyDB.getMessage());
            ((Data)getApplicationContext()).historyList.add(historyDB.getMessage());
        }
        //从数据库中查出订阅消息
        List<SubscriptionDB> foundSubscription = DataSupport.findAll(SubscriptionDB.class);
        for (SubscriptionDB subscriptionDB : foundSubscription) {
            ((Data)getApplicationContext()).subscriptionList.add(subscriptionDB.getTopic());
        }
    }
}