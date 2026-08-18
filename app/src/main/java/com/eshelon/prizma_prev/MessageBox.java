package com.eshelon.prizma_prev;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.eshelon.prizma_prev.C_.DB_VERSION;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eshelon.prizma_prev.objects.ObjectProcessingData;

import java.util.Timer;
import java.util.TimerTask;


public class MessageBox implements View.OnTouchListener {

    public static final int CMD_REMOVE_DB_LINE   = 1;



    TextView       sMessageModalWindowTxt;
    RelativeLayout sMessageModalWindow;
    RelativeLayout sMessageModalWindowBttnConfirm;
    RelativeLayout sMessageModalWindowBttnCansel;




    Activity activity;
    ObjectProcessingData o;
    public MessageBox(Activity _activity) {
        activity = _activity;
    }
    public MessageBox(Activity _activity, ObjectProcessingData _o) {
        activity = _activity;
        o = _o;
    }
    void showModalWindow(String message){
        sMessageModalWindowTxt           = activity.findViewById(R.id.sMessageModalWindowTxt);
        sMessageModalWindow              = activity.findViewById(R.id.sMessageModalWindow);
        sMessageModalWindowBttnConfirm   = activity.findViewById(R.id.sMessageModalWindowBttnConfirm);
        sMessageModalWindowBttnCansel    = activity.findViewById(R.id.sMessageModalWindowBttnCansel);

        sMessageModalWindowTxt.setText(message);
        sMessageModalWindow.setVisibility(VISIBLE);
        sMessageModalWindowBttnConfirm.setOnTouchListener(this);
        sMessageModalWindowBttnCansel .setOnTouchListener(this);
    }
    void animeMessage(boolean stt, String message){
        int vis = stt ? VISIBLE : GONE;
        RelativeLayout mBox   = activity.findViewById(R.id.messageBox);
        TextView     msgTxt = activity.findViewById(R.id.messageBoxTxt);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgTxt.setText(message);
                mBox.setVisibility(vis);
            }
        });
    }
    void startHidingTimer(int tm){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                animeMessage( false, "");
            }
        },tm);
    }
    void showMessage(String message){
        int tm = message.length()*50;
        animeMessage(  true,  message);
        startHidingTimer(tm);
    }

    void processingCmd(){
        switch (o.cmd){
            case CMD_REMOVE_DB_LINE:
                Integer id = (Integer) o.object;
                Db db = new Db((Context) activity.getApplicationContext(), "dbName", null, DB_VERSION);
                db.delete(id);
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int vId = v.getId();
        if(vId == R.id.sMessageModalWindowBttnCansel){}
        if(vId == R.id.sMessageModalWindowBttnConfirm){

        }
        sMessageModalWindow.setVisibility(GONE);
        v.performClick();
        return true;
    }
}
