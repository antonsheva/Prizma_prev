package com.eshelon.prizma_prev;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class AnimeViewElements {
    public AnimeViewElements(){

    }
    void showView(Activity activity, View view, Drawable drawable){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setBackground(drawable);
            }
        });
    }
    Timer tmButtonClick = new Timer();
    boolean swchOnClick = false;
    static Drawable drawablePrev;
    public void onClick(Activity activity, View view){
        if(swchOnClick)return;
        Drawable dClick = activity.getDrawable(R.drawable.button_onclick_grey);
        Drawable dMain = view.getBackground();
        swchOnClick = true;

        showView(activity, view, dClick);
        tmButtonClick.schedule(new TimerTask() {
            @Override
            public void run() {
                showView(activity, view, dMain);
                swchOnClick = false;
            }
        },80);
    }
    public void onTouch(Activity activity, View view, boolean stt, int color){
        Drawable drawable;

        if(stt){
            drawablePrev = view.getBackground();
            if(color == 1)drawable = activity.getDrawable(R.drawable.button_onclick_yellow);
            else          drawable = activity.getDrawable(R.drawable.button_onclick_grey);
            Log.i("MY_TEG", "onTouchDown");
        }else{
            drawable = drawablePrev;
            Log.i("MY_TEG", "onTouchUp");
        }
        showView(activity,view, drawable);
    }


}
