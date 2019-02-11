package com.example.myshh.bluetoothchat;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;

public class RcvConnectionASyncTask extends AsyncTask<Object, Void, Void> {

    private final BluetoothServerSocket mmServerSocket;
    private ProgressDialog dialog;

    public RcvConnectionASyncTask(ProgressDialog dialog) {
        this.dialog = dialog;

        BluetoothServerSocket tmp = null;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BT_CHAT", UUID.fromString("36cdc093-4b8e-4cbe-acc2-17fae20ce295"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mmServerSocket = tmp;
    }

    @Override
    protected Void doInBackground(Object... voids) {
        try {
            BluetoothSocket mmSocket = mmServerSocket.accept();
            SocketStorage.setSocket(mmSocket);
            mmServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.hide();
    }
}
