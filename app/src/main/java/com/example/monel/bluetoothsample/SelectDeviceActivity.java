package com.example.monel.bluetoothsample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.ACTION_NAME_CHANGED;

public class SelectDeviceActivity extends AppCompatActivity {

    private static final String TAG = "SelectDeviceActivity";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private ListView mPairedDeviceListView;

    private ListView mFoundDeviceListView;

    private BluetoothAdapter mBluetoothAdapter;

    private final int DISCOVERY_INTERVAL = 15000;

    private HandlerThread mHandlerThread;

    private boolean mIsDiscovering = false;

    private DeviceListAdapter mPairedDeviceListAdapter;

    private DeviceListAdapter mFoundDeviceListAdapter;

    private Map<String, String> mPairedDevices = new LinkedHashMap<>();

    private Map<String, String> mFoundDevices = new LinkedHashMap<>();


    private final BroadcastReceiver mDeviceFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "DEBUG--:onReceive");
            Log.d(TAG, "DEBUG--:onReceive.currentThread->" + Thread.currentThread().getName());
            String action = intent.getAction();
            String deviceName = null;
            BluetoothDevice bluetoothDevice;
            if (ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG, "DEBUG--:onReceive->ACTION_DISCOVERY_STARTED");
            }
            if (ACTION_FOUND.equals(action)) {
                Log.d(TAG, "DEBUG--:onReceive->ACTION_FOUND");
                mFoundDeviceListView.setVisibility(View.VISIBLE);
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 初めて検出されたデバイスの場合は、デバイス検出時にデバイス名が取得できていない場合があります。
                // そのときのために、デバイス名が検出された時点でリストに登録するようにします
                if ((deviceName = bluetoothDevice.getName()) != null) {
//                    mFoundDeviceNameList.add(deviceName);
//                    mFoundDeviceAddressList.add(bluetoothDevice.getAddress());
                    mFoundDevices.put(bluetoothDevice.getAddress(), deviceName);
                    Log.d(TAG, "DEBUG--:onReceive->ACTION_FOUND.deviceName is not null");
                }
            }
            if (ACTION_NAME_CHANGED.equals(action)) {
                Log.d(TAG, "DEBUG--:onReceive->ACTION_NAME_CHANGED");
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                    // 接続したことのないデバイスのみリストに登録する
//                    mFoundDeviceNameList.add(bluetoothDevice.getName());
                    Log.d(TAG, "DEBUG--:deviceName->" + bluetoothDevice.getName());
                    Log.d(TAG, "DEBUG--:deviceAddress->" + bluetoothDevice.getAddress());
//                    mFoundDeviceAddressList.add(bluetoothDevice.getAddress());
                    mFoundDevices.put(bluetoothDevice.getAddress(), bluetoothDevice.getName());
                    mFoundDeviceListAdapter.notifyDataSetChanged();
                }
            }
            if (ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "DEBUG--:onReceive->ACTION_DISCOVERY_FINISHED");
//                deviceListAdapter = new DeviceListAdapter(getApplicationContext(), mFoundDeviceNameList, mFoundDeviceAddressList);
////                mNonPairedDeviceListView.setAdapter(deviceListAdapter);
//                deviceListAdapter.notifyDataSetChanged();
                Log.d(TAG, "DEBUG--:mFoundDeviceListAdapter.notifyDataSetChanged");
                mFoundDeviceListAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);

        bindView();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "DEBUG--:Bluetooth is not supported");
            Toast.makeText(this, "Bluetooth非対応端末です。トップ画面に戻ります。", Toast.LENGTH_SHORT);
            finish();
        }
        else {
            Log.d(TAG, "DEBUG--:Bluetooth is supported");
        }

        boolean isBtEnabled = mBluetoothAdapter.isEnabled();
        if (isBtEnabled == true) {
            Log.d(TAG, "DEBUG--:Bluetooth is ON");
            displayPairedDeviceList();
            displayNonPairedDeviceList();
        }
        else {
            Log.d(TAG, "DEBUG--:Bluetooth is OFF");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    private void bindView() {
        mPairedDeviceListView = findViewById(R.id.pairedDeviceList);
        mPairedDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), (LinkedHashMap) mPairedDevices);
        mPairedDeviceListView.setAdapter(mPairedDeviceListAdapter);
        mPairedDeviceListView.setVisibility(View.GONE);

        mFoundDeviceListView = findViewById(R.id.nonPairedDeviceList);
        mFoundDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), (LinkedHashMap) mFoundDevices);
        mFoundDeviceListView.setAdapter(mFoundDeviceListAdapter);
        mFoundDeviceListView.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int ResultCode, Intent date){
        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            if(ResultCode == RESULT_OK){
                //BluetoothがONにされた場合の処理
                Log.d(TAG, "DEBUG--:Bluetooth is ON");
                displayPairedDeviceList();
                displayNonPairedDeviceList();
            }else{
                Log.d(TAG, "DEBUG--:Bluetooth is OFF");
                finish();
            }
        }
    }

    private void displayPairedDeviceList() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            mPairedDeviceListView.setVisibility(View.VISIBLE);
//            ArrayList<String> deviceNameList = new ArrayList<>();
//            ArrayList<String> deviceAddressList = new ArrayList<>();
//            Map<String, String> devices = new LinkedHashMap<>();
            for (BluetoothDevice device:pairedDevices) {
//                deviceNameList.add(device.getName());
//                deviceAddressList.add(device.getAddress());
                mPairedDevices.put(device.getAddress(), device.getName());
            }
//            DeviceListAdapter deviceListAdapter = new DeviceListAdapter(getApplicationContext(),deviceNameList, deviceAddressList);
//            DeviceListAdapter deviceListAdapter1 = new DeviceListAdapter(getApplicationContext(), (LinkedHashMap) mPairedDevices);
            mPairedDeviceListAdapter.notifyDataSetChanged();
        }
        else {
            mPairedDeviceListView.setVisibility(View.GONE);
        }
    }

    protected void onDestroy() {
        Log.d(TAG, "DEBUG--:onDestroy");
        super.onDestroy();
        stopDiscovering();
        unregisterReceiver(mDeviceFoundReceiver);
    }

    private void stopDiscovering() {
        mHandlerThread.quit();
        mIsDiscovering = false;
    }

    private void displayNonPairedDeviceList() {
        // IntentFilterとBroadcastReceiverの登録
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: パーミッションが許可されていないならダイアログを出して許可を促すようにする
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DISCOVERY_STARTED);
        filter.addAction(ACTION_FOUND);
        filter.addAction(ACTION_NAME_CHANGED);
        filter.addAction(ACTION_DISCOVERY_FINISHED);
        registerReceiver(mDeviceFoundReceiver, filter);

        mHandlerThread = new HandlerThread("discovering");
        mHandlerThread.start();

        mIsDiscovering = true;

        final Handler handler = new Handler(mHandlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "DEBUG--:currentThread->" + Thread.currentThread().getName());
                while (mIsDiscovering) {
                    try {
                        if (mBluetoothAdapter.isDiscovering()) {
                            // 検索中の場合は検出をキャンセルする
                            mBluetoothAdapter.cancelDiscovery();
                        }
                        Log.d(TAG, "DEBUG--:startDiscovery");
                        mBluetoothAdapter.startDiscovery();

                        Thread.sleep(DISCOVERY_INTERVAL);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "DEBUG--:InterruptedException is thrown");
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "DEBUG--:stop_run");
            }
        });
    }
}
