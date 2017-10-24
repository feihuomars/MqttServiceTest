package com.example.android.mqttservicetest;

import org.litepal.crud.DataSupport;

/**
 * Created by feihu on 2017/9/9.
 */

public class HistoryDB extends DataSupport {
    private int id;
    private String message;
    private String topic;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }



    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
