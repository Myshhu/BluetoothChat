package com.example.myshh.bluetoothchat;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Thread rcvConnectionThread;
    AsyncTask rcvConnectionASTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnSelectDeviceClick(View v) {
        //Launch SelectDeviceActivity
    }

    public void btnListenForConnectionClick(View v) {
        //Check if Bluetooth is turned on
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBt, 100);
        }

        //Init dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Waiting for connection");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
            Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_LONG).show();
            //Cancel task when cancelled dialog
            rcvConnectionASTask.cancel(true);
        });
        //Start task
        rcvConnectionASTask = new RcvConnectionASyncTask(progressDialog);
        rcvConnectionASTask.execute();
        progressDialog.show();
    }
}
