package com.akhilesh.bt;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Button search;
    ListView listView;
    TextView textView;
    ArrayList<String> bluetoothDevices = new ArrayList<>();
    ArrayList<String> addresses = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search);
        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,bluetoothDevices);
        listView.setAdapter(arrayAdapter);

        if(ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ACCESS_FINE_LOCATION},0);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver,intentFilter);
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Action", "Action " + action);
            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                search.setEnabled(true);
                textView.setText("finished");
            }
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
                Log.i("Action", "Device Found: " + address + " " + " rssi" + " " + rssi);
                if(!addresses.contains(address)){
                    addresses.add(address);
                    String dev;
                    if(name == null || name.equals("")){
                        dev = address + " " + rssi;
                    }
                    else {
                        dev = name + " " + address + " " + rssi;
                    }
                    bluetoothDevices.add(dev);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void searchClicked(View view) {
        textView.setText("Searching..");
        search.setEnabled(false);
        bluetoothDevices.clear();
        addresses.clear();
        bluetoothAdapter.startDiscovery();
    }
}
