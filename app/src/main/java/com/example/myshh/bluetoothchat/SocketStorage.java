package com.example.myshh.bluetoothchat;

import android.bluetooth.BluetoothSocket;

class SocketStorage {

    private static BluetoothSocket socket = null;

    static synchronized BluetoothSocket getSocket(){
        return socket;
    }

    static synchronized void setSocket(BluetoothSocket socket){
        SocketStorage.socket = socket;
    }
}
