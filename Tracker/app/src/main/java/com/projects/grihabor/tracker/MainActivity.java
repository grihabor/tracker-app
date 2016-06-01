package com.projects.grihabor.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    Button bStart;
    TextView tvConsole;
    private static NetworkTask nt;
    OutputStream outputStream;
    private static final int SERVER_PORT = 9090;
    private static final String SERVER_IP = "192.168.0.74";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bStart = (Button) findViewById(R.id.bStart);
        tvConsole = (TextView) findViewById(R.id.tvConsole);
        bStart.setOnClickListener(this);
    }

    public void Print(String msg) {
        tvConsole.append(msg + "\n");
    }

    @Override
    public void run() {
        Print("Connected!");
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    static class ConnectionHandler extends Handler {
        MainActivity a;

        ConnectionHandler(MainActivity a)
        {
            this.a = a;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    a.runOnUiThread(a);
                    break;
                default:
                    a.Print("Failed to connect!");
                    break;
            }
        }
    }
    public boolean isConnectedToInternet(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork == null)
            return false;
        return activeNetwork.isConnectedOrConnecting();
    }
    @Override
    public void onClick(View v) {
        if(!isConnectedToInternet()){
            Print("Turn on your Wi-Fi");
            return;
        }
        Print("Connecting to " + SERVER_IP + ":" + SERVER_PORT);
        Handler h = new ConnectionHandler(this);
        nt = new NetworkTask(/*this, */SERVER_IP, SERVER_PORT, h);
        nt.execute();
    }
}
