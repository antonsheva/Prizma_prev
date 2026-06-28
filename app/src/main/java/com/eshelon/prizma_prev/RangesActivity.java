package com.eshelon.prizma_prev;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class RangesActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    Vibrator vibrator;
    final ArrayList<Integer> bttnRangeIdList = new ArrayList<>();
    final ArrayList<RelativeLayout> bttnRangeViewList = new ArrayList<>();

    RelativeLayout bttnSetFrqBand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranges);
        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ranges), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    void initBttnRanges(){
        String str;
        int vId;
        for(int i = 1; i<25; i++) {
            str = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_RANGE + Integer.toString(i);
            vId = this.getResources().getIdentifier(str, "id", getPackageName());
            bttnRangeIdList.add(vId);
            RelativeLayout rl = (RelativeLayout) findViewById(vId);
            rl.setOnClickListener(this);
            bttnRangeViewList.add(rl);
        }
    }
    void initViewElements(){
        initBttnRanges();
        bttnSetFrqBand = findViewById(R.id.sRangesButtonSetFrqBand);
        bttnSetFrqBand.setOnClickListener(this);
    }
    void init(){
        context = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initViewElements();
    }


    void showPageFrqBan(){
        Intent i = new Intent(context, FrqBandActivity.class);
        startActivity(i);
    }

    void resetColorPatternButtons(){
        for(RelativeLayout rl : bttnRangeViewList){
            rl.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));
        }
    }
    void selectRange(int rangeNum){
        G_.selectRange = rangeNum;
        resetColorPatternButtons();
        bttnRangeViewList.get(rangeNum).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern_select, null));
        Log.i("MY_LOG", "range -> "+Integer.toString(rangeNum));

    }

    void vibro(){
        final VibrationEffect vibrationEffect;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK);
        } else {
            vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);
        }
        vibrator.cancel();
        vibrator.vibrate(vibrationEffect);
    }

    @Override
    public void onClick(View v) {
        vibro();
        int vId = v.getId();
        if (vId == R.id.sRangesButtonSetFrqBand)showPageFrqBan();

        String vName = getResources().getResourceName(vId);

        if(vName.contains("Patt")){
            int num;
            String strNum = vName.substring(vName.lastIndexOf("t")+1);
            try{
                num = Integer.parseInt(strNum);
            }catch (Exception e){
                num = 0;
            }
            if(num != 0)selectRange(num-1);
        }

    }
}