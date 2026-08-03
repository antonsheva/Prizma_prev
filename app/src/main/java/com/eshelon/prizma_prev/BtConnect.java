package com.eshelon.prizma_prev;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.Toast;

import com.eshelon.prizma_prev.interfaces.CB;

public class BtConnect {
    private Context context;
    BluetoothAdapter btAdapter;
    BluetoothDevice  btDevice;
    String mac;
    CB cb;
    public ConnectThread connectThread;

    public BtConnect(Context cntxt, String macAddress, CB callback){
        cb = callback;
        context = cntxt;
        mac = macAddress;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void connect(){
        if(!btAdapter.isEnabled()){
            Toast.makeText(context, "Need enable bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mac.isEmpty()){
            Toast.makeText(context, "MAC error", Toast.LENGTH_SHORT).show();
            return;
        }

        btDevice = btAdapter.getRemoteDevice(mac);
        if(btDevice == null){
            Toast.makeText(context, "Error get ", Toast.LENGTH_SHORT).show();
            return;
        }

        connectThread = new ConnectThread(context, btAdapter, btDevice, cb);
        connectThread.start();
    }

}
