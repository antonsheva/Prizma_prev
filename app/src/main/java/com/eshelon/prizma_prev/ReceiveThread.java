package com.eshelon.prizma_prev;


import static com.eshelon.prizma_prev.C_.BT_STATE_ENABLE;
import static com.eshelon.prizma_prev.C_.CB_CODE_DISCONNECT;
import static com.eshelon.prizma_prev.C_.CB_CODE_NEW_DATA;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReceiveThread extends Thread{
    BluetoothSocket socket;
    InputStream inputStream;
    OutputStream outputStream;

    CbBtReceive cbReceive;
    private byte [] rBuff;
    public ReceiveThread(BluetoothSocket sct){
        socket = sct;

        try {
            inputStream = socket.getInputStream();
        } catch (IOException ignored) {}

        try {
            outputStream = socket.getOutputStream();
        }catch (IOException ignored){}
    }

    @Override
    public void run() {
        rBuff = new byte[128];
        while (true){
            try {
                int dataLen = inputStream.read(rBuff);
                String msg = new String(rBuff, 0, dataLen);
                G_.btReceiveData = msg;
                cbReceive.cb(CB_CODE_NEW_DATA, msg);
            }catch (IOException e){
                Log.i("MY_TEG", "error inputStream.read");
                cbReceive.cb(CB_CODE_DISCONNECT,null);
                break;
            }
            try{
                if(!socket.isConnected()){

                    cbReceive.cb(CB_CODE_DISCONNECT,null);
                }
            }catch (Exception e){
                Log.i("MY_TEG", "error check btSocket state");
            }
        }
    }

    public void sendData(byte[] data){
        try {
            outputStream.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCbReceive(CbBtReceive cb){
        cbReceive = cb;
    }
}
