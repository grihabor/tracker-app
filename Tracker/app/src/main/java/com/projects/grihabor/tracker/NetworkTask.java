package com.projects.grihabor.tracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created by Gregory on 31.05.2016.
 */
class NetworkTask extends AsyncTask<ByteBuffer, Void, Integer> {
    private static final String TAG = "NetworkTask";
    static private Integer serverPort;
    static private String serverIP;

    private Handler h;
    private Socket socket;

    NetworkTask(String serverIP, Integer serverPort, Handler h){
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.h = h;
        this.socket = null;
    }

    NetworkTask(Handler h)
    {
        this.h = h;
        this.socket = null;
    }

    @Override
    protected void onPostExecute(Integer errorCode) {
        super.onPostExecute(errorCode);
        if(socket.isConnected()) {
            h.sendEmptyMessage(0);
        } else {
            h.sendEmptyMessage(errorCode);
        }
    }

    @Override
    protected Integer doInBackground(ByteBuffer[] params) {

        if(socket == null || !socket.isConnected()){
            try{
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                //ignore
            }
            socket = new Socket();
            for(int i = 0; i < 5 && !socket.isConnected(); ++i) {
                try {
                    socket.connect(new InetSocketAddress(serverIP, serverPort), 1000);
                    /*OutputStream outputStream = socket.getOutputStream();
                    byte[] buf = "android".getBytes();
                    outputStream.write(buf);*/
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(!socket.isConnected()){
                Log.d(TAG, "Failed to connect!");
                return 1;
            }
        } else {
            Log.d(TAG, "Already connected");
        }

        if(params.length == 0){
            Log.d(TAG, "No data to send...");
            return 0;
        }

        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return 2;
        }
        for (ByteBuffer buf : params) {

            byte[] buffer = buf.array();
            try {
                byte[] arr = ByteBuffer.allocate(8).putLong(buffer.length).array();
                Log.d(TAG, Integer.toString(buffer.length));
                for(int i = 0; i < 8; ++i){
                    Log.d(TAG, "arr[" + Integer.toString(i) + "] = " + Integer.toString(arr[i]));
                }
                outputStream.write(arr);
                outputStream.close();
                arr = ByteBuffer.allocate(4).putInt(480).array();
                Log.d(TAG, Integer.toString(480));
                outputStream.write(arr);
                arr = ByteBuffer.allocate(4).putInt(800).array();
                Log.d(TAG, Integer.toString(800));
                outputStream.write(arr);
                outputStream.flush();
                Log.d(TAG, "Writing buffer");
                for(int i = 0, t = 1; t > 0; ++i) {
                    t = buffer.length-i*1024;
                    outputStream.write(buffer, i*1024, (t < 1024) ? t : 1024);
                    outputStream.flush();
                    Log.d(TAG, Integer.toString(i));
                }
                Log.d(TAG, "Done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
