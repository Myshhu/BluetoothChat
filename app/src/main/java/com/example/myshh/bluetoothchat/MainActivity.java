package com.example.myshh.bluetoothchat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnSelectDeviceClick(View v) {
        //Launch SelectDeviceActivity
    }

    public void btnListenForConnectionClick(View v) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Waiting for connection");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_LONG).show());
        progressDialog.show();
    }
}
