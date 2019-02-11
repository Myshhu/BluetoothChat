package com.example.myshh.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class ConnectToDeviceThread extends Thread {

    private final BluetoothSocket mmSocket;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    ConnectToDeviceThread(BluetoothDevice device){
        BluetoothSocket tmp = null;
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("36cdc093-4b8e-4cbe-acc2-17fae20ce295"));
        } catch (IOException e){
            e.printStackTrace();
        }
        mmSocket = tmp;
    }

    public void run(){
        mBluetoothAdapter.cancelDiscovery();
        try {
            mmSocket.connect();
            SocketStorage.setSocket(mmSocket);
            Log.d(TAG, "ConnectToDeviceThread: Connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(){
        try {
            mmSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }



}
