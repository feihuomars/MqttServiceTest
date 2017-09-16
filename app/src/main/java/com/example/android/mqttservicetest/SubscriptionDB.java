package com.example.android.mqttservicetest;

import org.litepal.crud.DataSupport;

/**
 * Created by feihu on 2017/9/16.
 */

public class SubscriptionDB extends DataSupport{
    private String topic;
    private int qos;
    private int id;
    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
