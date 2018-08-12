package com.example.monel.bluetoothclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TopMenuActivity extends AppCompatActivity {

    private static final String TAG = "TopMenuActivity";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_menu);

        Button bluetoothButton = findViewById(R.id.bluetoothButton);
        Button httpButton = findViewById(R.id.httpButton);

        bluetoothButton.setOnClickListener(bluetoothButtonClickListener);
        // TODO: HTTP通信にも対応させる
        httpButton.setEnabled(false);
    }

    // Bluetoothに接続状況は監視する必要がある
    // →Service使う？
    @Override
    protected void onActivityResult(int requestCode, int ResultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (ResultCode == RESULT_OK) {
                Log.d(TAG, "DEBUG--:Bluetooth is ON");
            }
            else {
                Log.d(TAG, "DEBUG--:Bluetooth is OFF");
                finish();
            }
        }
    }

    View.OnClickListener bluetoothButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "DEBUG--:currentThread->" + Thread.currentThread().toString());
            Intent intent = new Intent(getApplicationContext(), SelectDeviceActivity.class);
            startActivity(intent);
        }
    };
}
