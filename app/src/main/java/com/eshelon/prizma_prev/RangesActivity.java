package com.eshelon.prizma_prev;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eshelon.prizma_prev.objects.JmmrState;

import java.util.ArrayList;

public class RangesActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    Vibrator vibrator;
    final ArrayList<Integer> panelRangeIdList = new ArrayList<>();
    final ArrayList<LinearLayout> panelRangeViewList = new ArrayList<>();
    final ArrayList<RelativeLayout> buttonRangeViewList = new ArrayList<>();
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
    void resetColorPanels(){
        for(LinearLayout ll : panelRangeViewList){
            ll.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.range_bacground_no_active, null));
        }
        for(RelativeLayout rl : buttonRangeViewList){
            rl.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.range_bacground_no_active, null));
        }
    }
    void setColorPanel(int num){
        if(num%2 == 0)panelRangeViewList.get(num).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.range_bacground_red, null));
        else          panelRangeViewList.get(num).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.range_bacground_blue, null));

        buttonRangeViewList.get(num*2).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));
        buttonRangeViewList.get(num*2+1).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));

        panelRangeViewList.get(num).setOnClickListener(this);
    }
    void checkActiveRange(){
        for (JmmrState jmmr : G_.jmmr_list){
            if(jmmr.dev_range > 0)setColorPanel(jmmr.dev_range - 1);
            else Log.e("MY_ERR", "Error range parameter");
        }
    }
    void initRangePanels(){
        String strPanel;
        String strButton1;
        String strButton2;

        int vIdPanel;
        int vIdButton1;
        int vIdButton2;
        for(int i = 1; i<=12; i++) {
            strPanel = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_RANGE_PANEL + Integer.toString(i);
            strButton1 = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_RANGE_BUTTON + Integer.toString(i*2-1);
            strButton2 = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_RANGE_BUTTON + Integer.toString(i*2);

            vIdPanel = this.getResources().getIdentifier(strPanel, "id", getPackageName());
            vIdButton1 = this.getResources().getIdentifier(strButton1, "id", getPackageName());
            vIdButton2 = this.getResources().getIdentifier(strButton2, "id", getPackageName());


            LinearLayout ll = (LinearLayout) findViewById(vIdPanel);
            RelativeLayout rl1 = (RelativeLayout)findViewById((vIdButton1));
            RelativeLayout rl2 = (RelativeLayout)findViewById((vIdButton2));

            panelRangeIdList.add(vIdPanel);



            panelRangeViewList.add(ll);
            buttonRangeViewList .add(rl1);
            buttonRangeViewList .add(rl2);
        }

    }
    void initViewElements(){
        initRangePanels();
        resetColorPanels();
        checkActiveRange();
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


    void selectRange(int rangeNum){
        G_.selectRange = rangeNum;
        Log.i("MY_LOG", "range -> "+Integer.toString(rangeNum));
        showPageFrqBan();
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

        if(vName.contains("PanelPatt")){
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