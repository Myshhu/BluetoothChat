package com.example.myshh.bluetoothchat;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ChatWindowActivity extends AppCompatActivity {

    private EditText editText;

    private InputStream mmInStream;
    private OutputStream mmOutStream;

    ArrayList<String> listMessages = new ArrayList<>();
    ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        editText = findViewById(R.id.editText);

        BluetoothSocket socket = SocketStorage.getSocket();

        String tmp = getString(R.string.Chat_with) + socket.getRemoteDevice().toString();
        TextView textView = findViewById(R.id.textView);
        textView.setText(tmp);

        ListView lvChat = findViewById(R.id.lvChat);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listMessages);
        lvChat.setAdapter(listAdapter);

        try {
            mmInStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mmOutStream = socket.getOutputStream();
            mmOutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startReceiving();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Close current connection
        if(SocketStorage.getSocket() != null){
            try {
                SocketStorage.getSocket().close();
                SocketStorage.setSocket(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnSendClick(View v){
        try {
            mmOutStream.write(editText.getText().toString().getBytes());
            listMessages.add(editText.getText().toString());
            editText.setText("");
            listAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReceiving(){
        Thread rcv = new Thread(()->{
            byte[] mmBuffer = new byte[1024];
            int bytes;

            while(true) {
                try {
                    bytes = mmInStream.read(mmBuffer);
                    String message = new String(mmBuffer, 0, bytes);
                    listMessages.add("->" + message);
                    runOnUiThread(action);

                } catch (IOException e) {
                    e.printStackTrace();
                    listMessages.add("----Connection lost----");
                    runOnUiThread(action);
                    return;
                }
            }
        });
        rcv.start();
    }

    private Runnable action = new Runnable() {
        @Override
        public void run() {
            listAdapter.notifyDataSetChanged();
        }
    };
}
