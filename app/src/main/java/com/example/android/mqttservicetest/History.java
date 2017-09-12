package com.example.android.mqttservicetest;

import org.litepal.crud.DataSupport;

/**
 * Created by feihu on 2017/9/9.
 */

public class History extends DataSupport{
    private int id;
    private String message;

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
