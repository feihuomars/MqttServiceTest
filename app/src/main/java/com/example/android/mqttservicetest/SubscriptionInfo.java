package com.example.android.mqttservicetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SubscriptionInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_info);

        final EditText topicEditText = (EditText) findViewById(R.id.topic_edit_text);
        final EditText qosEditText = (EditText) findViewById(R.id.qos_edit_text);
        Button submitButton = (Button) findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = topicEditText.getText().toString();
                String qos = qosEditText.getText().toString();
                Intent intent = new Intent (SubscriptionInfo.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
