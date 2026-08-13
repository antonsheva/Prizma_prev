package com.eshelon.prizma_prev;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MessageBox {
    public MessageBox(Activity activity, String message) {
        showMessage(activity, true, message);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                showMessage(activity, false, message);
            }
        },500);
    }


    void showMessage(Activity activity, boolean stt, String message){
        int vis = stt ? VISIBLE : GONE;
        LinearLayout mBox   = activity.findViewById(R.id.messageBox);
        TextView     msgTxt = activity.findViewById(R.id.messageBoxTxt);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgTxt.setText(message);
                mBox.setVisibility(vis);
            }
        });
    }
}
