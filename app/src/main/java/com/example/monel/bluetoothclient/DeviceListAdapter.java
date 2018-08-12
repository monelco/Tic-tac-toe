package com.example.monel.bluetoothclient;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DeviceListAdapter extends BaseAdapter {

    private static final String TAG = "DeviceListAdapter";

    private BluetoothDevice[] mBluetoothDeviceList;

    private LayoutInflater mInflater;

    private ArrayList<String> mDeviceNameList;

    private ArrayList<String> mDeviceAddressList;

    // 接続履歴のあるデバイスリスト用のコンストラクタ
    DeviceListAdapter(Context context, ArrayList<String> deviceNameList,
                      ArrayList<String> deviceAddressList) {
        mDeviceNameList = deviceNameList;
        mDeviceAddressList = deviceAddressList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    DeviceListAdapter(Context context, LinkedHashMap<String, String> devices) {
        mDeviceNameList = new ArrayList<>(devices.keySet());
        mDeviceAddressList = new ArrayList<>(devices.values());
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {
        TextView mDeviceNameView;
        TextView mDeviceAddressView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: レイアウト作成
//        Log.d(TAG, "DEBUG--:getView.position->" + position);
        ViewHolder viewHolder;
        if (convertView == null) {
//            Log.d(TAG, "DEBUG--:convertView is null");
            // inflateの引数についてはもう少し調べる必要あり
            convertView = mInflater.inflate(R.layout.device_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mDeviceNameView = convertView.findViewById(R.id.deviceName);
            viewHolder.mDeviceAddressView = convertView.findViewById(R.id.deviceAddress);
            // ここの処理の意味がよくわかっていないので調べる
            convertView.setTag(viewHolder);
        }
        else  {
//            Log.d(TAG, "DEBUG--:convertView is not null");
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mDeviceNameView.setText(mDeviceNameList.get(position));
        viewHolder.mDeviceAddressView.setText(mDeviceAddressList.get(position));

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        Log.d(TAG, "DEBUG--:getCount->" + mDeviceNameList.size());
        return mDeviceNameList.size();
    }

    private void add(String deviceAddress, String deviceName) {

    }
}
