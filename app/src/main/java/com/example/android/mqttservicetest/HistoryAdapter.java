package com.example.android.mqttservicetest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by feihu on 17/10/18.
 */

public class HistoryAdapter extends ArrayAdapter<HistoryDB>{
    private int resourceId;

    public HistoryAdapter(Context context, int textViewResourceId, List<HistoryDB> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HistoryDB historyItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView historyTopic = (TextView) view.findViewById(R.id.history_topic);
        TextView historyMessage = (TextView) view.findViewById(R.id.history_message);
        TextView historyTime = (TextView) view.findViewById(R.id.history_time);
        historyTopic.setText(historyItem.getTopic());
        historyMessage.setText(historyItem.getMessage());
        Date date = new Date(historyItem.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
        historyTime.setText(formatter.format(date));
        return view;
    }
}
