package com.example.android.mqttservicetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SubscriptionInfoActivity extends AppCompatActivity {

    private static final String TAG = "SubscriptionInfoActivit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_info);

        final EditText topicEditText = (EditText) findViewById(R.id.topic_edit_text);
        final EditText qosEditText = (EditText) findViewById(R.id.qos_edit_text);
        final Button submitButton = (Button) findViewById(R.id.submit_button);

        Toolbar toolbar = (Toolbar)findViewById(R.id.subscription_info_toolbar);
        setSupportActionBar(toolbar);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = topicEditText.getText().toString();
                String qos = qosEditText.getText().toString();
                Log.i(TAG, "onClick: EditText topic: " + topic);
                SubscriptionDB subscriptionDB = new SubscriptionDB();
                subscriptionDB.setTopic(topic);
                subscriptionDB.setQos(Integer.parseInt(qos));
                subscriptionDB.save();
                Intent intent = new Intent (SubscriptionInfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
