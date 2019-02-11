package com.example.myshh.bluetoothchat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SelectDeviceActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;

    ListView pairedDevices;
    ListView foundDevices;

    String[] devices;
    String[] devicesMAC;
    String[] arrayFoundDevices;
    String[] arrayFoundDevicesMAC;

    String selectedMAC;

    Set<BluetoothDevice> pairedDevicesSet = new HashSet<>();
    Set<BluetoothDevice> foundDevicesSet = new HashSet<>();

    Button btnConnect;

    ConnectToDeviceThread connectToDeviceThread;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();

        pairedDevices = findViewById(R.id.lvPaired);
        foundDevices = findViewById(R.id.lvFound);

        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setEnabled(false);

        showPairedDevices();
    }

    private void showPairedDevices(){
        ListAdapter pairedAdapter;

        if (mBluetoothAdapter == null) {
            //No Bluetooth adapter
            devices = new String[1];
            devices[0] = "Bluetooth adapter not found";
        }

        if (mBluetoothAdapter != null) {
            pairedDevicesSet = mBluetoothAdapter.getBondedDevices();
        }

        if (pairedDevicesSet.size() > 0) {

            devices = new String[pairedDevicesSet.size()];
            devicesMAC = new String[pairedDevicesSet.size()];

            int i = 0;
            for (BluetoothDevice device : pairedDevicesSet) {
                String deviceName = device.getName();
                if(deviceName == null){
                    deviceName = "#nullname";
                }
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceHardwareAddress == null){
                    deviceHardwareAddress = "";
                }

                devices[i] = deviceName;
                devicesMAC[i++] = deviceHardwareAddress;
                System.out.println(deviceName + " " + deviceHardwareAddress);
            }


            pairedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices);
            pairedDevices.setAdapter(pairedAdapter);

        }

        pairedDevices.setOnItemClickListener((parent, view, position, id) -> {
            selectedMAC = devicesMAC[position];

            //Get rid of warning
            String tmp = getString(R.string.Connect_to) + selectedMAC;
            btnConnect.setText(tmp);
            btnConnect.setEnabled(true);

        });

        pairedDevices.setOnItemLongClickListener((parent, view, position, id) -> {
            String device = String.valueOf(parent.getItemAtPosition(position)) + " " + devicesMAC[position] + " " + position;
            Toast.makeText(SelectDeviceActivity.this,device,Toast.LENGTH_LONG).show();

            return false;
        });

    }

    private void refreshFoundListView(){
        ListAdapter adapter;

        if (foundDevicesSet.size() > 0) {

            arrayFoundDevices = new String[foundDevicesSet.size()];
            arrayFoundDevicesMAC = new String[foundDevicesSet.size()];

            int i = 0;
            for (BluetoothDevice device : foundDevicesSet) {
                String deviceName = device.getName();
                if(deviceName == null){
                    deviceName = "#nullname";
                }
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceHardwareAddress == null){
                    deviceHardwareAddress = "";
                }

                arrayFoundDevices[i] = deviceName;
                arrayFoundDevicesMAC[i++] = deviceHardwareAddress;
                System.out.println(deviceName + " " + deviceHardwareAddress);
            }
        } else {
            //No new devices
            arrayFoundDevices = new String[1];
            arrayFoundDevices[0] = "No devices";
            arrayFoundDevicesMAC = new String[1];
            arrayFoundDevicesMAC[0] = "0";
        }

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayFoundDevices);
            foundDevices.setAdapter(adapter);

        foundDevices.setOnItemClickListener((parent, view, position, id) -> {
            selectedMAC = arrayFoundDevicesMAC[position];

            //Get rid of warning
            String tmp = getString(R.string.Connect_to) + selectedMAC;
            btnConnect.setText(tmp);
            btnConnect.setEnabled(true);
        });

        foundDevices.setOnItemLongClickListener((parent, view, position, id) -> {
            String device = String.valueOf(parent.getItemAtPosition(position)) + " " + arrayFoundDevicesMAC[position] + " " + position;
            Toast.makeText(SelectDeviceActivity.this,device,Toast.LENGTH_LONG).show();

            return false;
        });
    }

    private BluetoothDevice findDeviceInSet(String MAC){
        for (BluetoothDevice device : pairedDevicesSet) {
            if(device.getAddress().equals(MAC)){
                return device;
            }
        }

        for (BluetoothDevice device : foundDevicesSet) {
            if(device.getAddress().equals(MAC)){
                return device;
            }
        }
        return null;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                foundDevicesSet.add(device);
                Log.d(TAG, "onReceive: added, set size: " + foundDevicesSet.size());
                refreshFoundListView();
            }
        }
    };

    public void btnDiscover(View v) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        foundDevicesSet = new HashSet<>();

        checkBTPermissions();

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, discoverDevicesIntent);
        }
    }

    public void btnConnect(View v){
        if(SocketStorage.getSocket()!=null){
            try {
                SocketStorage.getSocket().close();
                SocketStorage.setSocket(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BluetoothDevice device = findDeviceInSet(selectedMAC);
        if(device != null) {
            connectToDeviceThread = new ConnectToDeviceThread(device);
            connectToDeviceThread.start();

            while (SocketStorage.getSocket() == null);
            //Start ChatActivity
        }
    }

    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this,requestCode + " " + resultCode + " " + data,Toast.LENGTH_LONG).show();
    }

}
