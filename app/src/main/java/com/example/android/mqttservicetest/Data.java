package com.example.android.mqttservicetest;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by feihu on 2017/9/10.
 */

public class Data extends Application{
    public ArrayList<HistoryDB> historyList = new ArrayList<>();
    public ArrayList<String> subscriptionList = new ArrayList<>();
}
