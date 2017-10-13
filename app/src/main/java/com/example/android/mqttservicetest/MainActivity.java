package com.example.android.mqttservicetest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    public FragmentTabHost fragmentTabHost;

    MqttAndroidClient mqttAndroidClient;
    final String serverUri = "tcp://47.94.246.26:1883";
    String[] topics;
    Integer[] qossInteger;
    int[] qoss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: create MainActivity");
        //数据库初始化
        LitePal.initialize(this);
        Connector.getDatabase();
        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navigationView.setCheckedItem(R.id.nav_call);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                return true;
            }
        });
        //fragmentTabHost相关设置
        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        Bundle bundle = new Bundle();
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("history").setIndicator("历史"), HistoryFragment.class, bundle);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("订阅").setIndicator("订阅"), SubscriptionFragment.class, bundle);

        //从数据库中查出历史消息
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
        //设置消息回调
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
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)    //使用默认的声音、震动、led效果
                        .setPriority(NotificationCompat.PRIORITY_MAX)
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

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.backup:
                Toast.makeText(this, "Backup Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "Delete Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}