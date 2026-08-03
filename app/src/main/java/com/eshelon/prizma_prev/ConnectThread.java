package com.eshelon.prizma_prev;

import static com.eshelon.prizma_prev.C_.BT_ACTIVE_STATE_CONNECTED;
import static com.eshelon.prizma_prev.C_.BT_ACTIVE_STATE_ENABLE;
import static com.eshelon.prizma_prev.C_.CB_CODE_CONNECT;
import static com.eshelon.prizma_prev.C_.CB_CODE_ERROR_CONNECT;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.eshelon.prizma_prev.interfaces.CB;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread{
    private Context context;
    BluetoothAdapter btAdapter;
    BluetoothDevice btDevice;
    BluetoothSocket btSocket;
    ReceiveThread receiveThread;
    CB cb;
    public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";


    public ConnectThread(Context cntxt, BluetoothAdapter adapter, BluetoothDevice device, CB callback){
        btAdapter = adapter;
        btDevice = device;
        context = cntxt;
        cb = callback;
        try {
            btSocket = btDevice.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
        } catch (IOException e) {
            Toast.makeText(context, "Error connect", Toast.LENGTH_SHORT).show();
        }
    }

    public ReceiveThread getReceiveThread() {
        return receiveThread;
    }

    @Override
    public void run() {
        int resCode = 0;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        btAdapter.cancelDiscovery();
        try {
            btSocket.connect();
            Log.d("MY_LOG", "Connected");
            receiveThread = new ReceiveThread(btSocket);
            receiveThread.start();
            G_.btActiveState = BT_ACTIVE_STATE_CONNECTED;
            resCode = CB_CODE_CONNECT;
        }catch (IOException e){
            Log.d("MY_LOG", "Not connected");
            closeConnection();
            G_.btActiveState = BT_ACTIVE_STATE_ENABLE;
            resCode = CB_CODE_ERROR_CONNECT;
        }
        cb.cb(resCode);
    }
    public void closeConnection(){
        try {
            G_.btActiveState = BT_ACTIVE_STATE_ENABLE;
            btSocket.close();
        }catch (IOException ignored){

        }
    }
}
