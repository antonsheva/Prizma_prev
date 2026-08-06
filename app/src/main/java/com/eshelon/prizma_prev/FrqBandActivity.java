package com.eshelon.prizma_prev;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eshelon.prizma_prev.objects.ObjRange;

import java.util.ArrayList;
import java.util.HexFormat;

public class FrqBandActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    Vibrator vibrator;

    TextView txtSelectRange;
    TextView txtBandCenter1;
    TextView txtBandCenter2;

    TextView txtSuppressBand;

    LinearLayout bttnSave;
    LinearLayout bttnCansel;

    SeekBar seekBar1;
    SeekBar seekBar2;

    Spinner spinner1;
    Spinner spinner2;


    ObjRange objRange1;
    ObjRange objRange2;
    ArrayList<LinearLayout>specterPiece1 = new ArrayList<>();
    ArrayList<LinearLayout>specterPiece2 = new ArrayList<>();
    final ArrayList<RelativeLayout> viewBandStepButtonList1 = new ArrayList<>();
    final ArrayList<Integer>bandStepButtonIdList1 = new ArrayList<>();
    final ArrayList<Integer>bandStepButtonIdList2 = new ArrayList<>();
    final ArrayList<RelativeLayout> viewBandStepButtonList2 = new ArrayList<>();
    final ArrayList<TextView> viewBandStepButtonTextList1 = new ArrayList<>();
    final ArrayList<TextView> viewBandStepButtonTextList2 = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_frq_band);
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frqBand), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

    void getRangeObjects(){
        objRange2 = G_.rangeList.get(G_.selectRange*2);
        objRange1 = G_.rangeList.get(G_.selectRange*2+1);
    }
    void initTxtData(){
        String str =    Integer.toString(G_.selectRange)+": "+
                        Integer.toString(objRange2.getStart())+" - "+Integer.toString(objRange1.getStop());
        txtSelectRange.setText(str);

        txtBandCenter1.setText(objRange1.getViewBandWidth());
        txtBandCenter2.setText(objRange2.getViewBandWidth());
    }
    void initSeekBar(){
        seekBar1.setMax(C_.FRQ_STEP_QTY);
        seekBar1.setProgress(C_.FRQ_STEP_QTY/2);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                objRange1.setFrqPosition(C_.FRQ_STEP_QTY - progress);
                updateViewElements();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2.setMax(C_.FRQ_STEP_QTY);
        seekBar2.setProgress(C_.FRQ_STEP_QTY/2);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                objRange2.setFrqPosition(C_.FRQ_STEP_QTY - progress);
                updateViewElements();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    void init(){
        context = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getRangeObjects();
        initViewElements();
        initTxtData();
        initSeekBar();
        initSpinner();
        updateBandStepPanels();
    }
    void initSpinner(){
        CustomAdapter customAdapter1=new CustomAdapter(getApplicationContext(),objRange1.getViewBandList());
        spinner1.setAdapter(customAdapter1);
        CustomAdapter customAdapter2=new CustomAdapter(getApplicationContext(),objRange2.getViewBandList());
        spinner2.setAdapter(customAdapter2);


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                objRange1.setCurrentBand(position);
                updateViewElements();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                objRange2.setCurrentBand(position);
                updateViewElements();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void showSuppressBands(int mask1, int mask2){
        LinearLayout.LayoutParams lParamsSizeParent;
        LinearLayout devBandsField = findViewById(R.id.devBandsField);
        lParamsSizeParent = (LinearLayout.LayoutParams) devBandsField.getLayoutParams();
        int hParent = lParamsSizeParent.height;
        int noActiveHeight  = hParent/3;
        int activeHeight    = hParent - hParent/10;
        int margTopNoActive = hParent-noActiveHeight - hParent /20;
        int margTopActive   = hParent /18;

        boolean swch1;
        boolean swch2;
        LinearLayout llChngParam;
        LinearLayout.LayoutParams lParams;
        for(int i=0; i<32; i++){
            swch1 = ((mask1 << i) & 0x80000000) == 0x80000000;
            swch2 = ((mask2 << i) & 0x80000000) == 0x80000000;

            llChngParam = specterPiece1.get(i);
            lParams = (LinearLayout.LayoutParams) llChngParam.getLayoutParams();

            if(!swch1){
                llChngParam.setBackgroundResource(R.drawable.range_no_active);
                lParams.height = noActiveHeight;
                lParams.topMargin = margTopNoActive;
            }
            else{
                llChngParam.setBackgroundResource(R.drawable.range_active);
                lParams.height = activeHeight;
                lParams.topMargin = margTopActive;
            }

            llChngParam = specterPiece2.get(i);
            lParams = (LinearLayout.LayoutParams) llChngParam.getLayoutParams();
            if(!swch2){
                llChngParam.setBackgroundResource(R.drawable.range_no_active);
                lParams.height = noActiveHeight;
                lParams.topMargin = margTopNoActive;
            }
            else{
                llChngParam.setBackgroundResource(R.drawable.range_active);
                lParams.height = activeHeight;
                lParams.topMargin = margTopActive;
            }
        }
    }
    void initRangeSticks(){
        LinearLayout llStick;
        String str;
        int vId;

        for(int i=0; i<32; i++){
            str = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_SPECTER +"1_"+Integer.toString(i);
            vId = this.getResources().getIdentifier(str, "id", this.getPackageName());
            llStick =  (LinearLayout) findViewById(vId);
            specterPiece1.add(llStick);

            str = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_SPECTER +"2_"+Integer.toString(i);
            vId = this.getResources().getIdentifier(str, "id", this.getPackageName());
            llStick = (LinearLayout)findViewById(vId);
            specterPiece2.add(llStick);
        }
    }

    void updateBandStepPanels(){
        int tmp1 = objRange1.getRangeMask();
        int tmp2 = objRange2.getRangeMask();
        RelativeLayout rl1;
        RelativeLayout rl2;

        for(int i=0; i<32; i++){
            rl1 = viewBandStepButtonList1.get(i);
            rl2 = viewBandStepButtonList2.get(i);


            if(((tmp1 >> i)& 0x1)>0)rl1.setBackgroundResource(R.drawable.button_pattern_active);
            else                    rl1.setBackgroundResource(R.drawable.button_pattern);

            if(((tmp2 >> i)& 0x1)>0)rl2.setBackgroundResource(R.drawable.button_pattern_active);
            else                    rl2.setBackgroundResource(R.drawable.button_pattern);
        }
    }
    void initBandStepPanels(){

        String strButton1;
        String strButton2;

        String strButtonTxt1;
        String strButtonTxt2;


        int vIdButton1;
        int vIdButton2;

        int vIdButtonTxt1;
        int vIdButtonTxt2;


        for(int i = 0; i<32; i++) {
            strButtonTxt1 = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_BAND_BUTTON_TXT+"_1_"+Integer.toString(i);
            strButtonTxt2 = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_BAND_BUTTON_TXT+"_2_"+Integer.toString(i);


            strButton1 = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_BAND_BUTTON + "_1_"+Integer.toString(i);
            strButton2 = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_BAND_BUTTON + "_2_"+Integer.toString(i);

            vIdButton1 = this.getResources().getIdentifier(strButton1, "id", getPackageName());
            vIdButton2 = this.getResources().getIdentifier(strButton2, "id", getPackageName());

            vIdButtonTxt1 = this.getResources().getIdentifier(strButtonTxt1, "id", getPackageName());
            vIdButtonTxt2 = this.getResources().getIdentifier(strButtonTxt2, "id", getPackageName());

            TextView txt1 = (TextView)findViewById(vIdButtonTxt1);
            TextView txt2 = (TextView)findViewById(vIdButtonTxt2);

            viewBandStepButtonTextList1.add(txt1);
            viewBandStepButtonTextList2.add(txt2);


            RelativeLayout rl1 = (RelativeLayout)findViewById(vIdButton1);
            RelativeLayout rl2 = (RelativeLayout)findViewById(vIdButton2);
            rl1.setOnClickListener(this);
            rl2.setOnClickListener(this);
            bandStepButtonIdList1.add(vIdButton1);
            bandStepButtonIdList2.add(vIdButton2);

            viewBandStepButtonList1.add(rl1);
            viewBandStepButtonList2.add(rl2);
        }
        for(int i=0; i<32; i++){
            viewBandStepButtonTextList1.get(i).setText(objRange1.getViewBandStepList().get(i));
            viewBandStepButtonTextList2.get(i).setText(objRange2.getViewBandStepList().get(i));
        }

    }
    void initViewElements(){
        txtBandCenter1  = findViewById(R.id.txtBandCenter1)      ;
        txtBandCenter2  = findViewById(R.id.txtBandCenter2)      ;

        txtSelectRange  = findViewById(R.id.sFrqBandRange)       ;

        seekBar1        = findViewById(R.id.sFrqBandSeekBar1)    ;
        seekBar2        = findViewById(R.id.sFrqBandSeekBar2)    ;

        spinner1        = findViewById(R.id.sFrqBandFrqSpinner1) ;
        spinner2        = findViewById(R.id.sFrqBandFrqSpinner2) ;

        bttnSave        = findViewById(R.id.sFrqBandButtonSave)  ;
        bttnCansel      = findViewById(R.id.sFrqBandButtonCansel);

        bttnSave  .setOnClickListener(this);
        bttnCansel.setOnClickListener(this);

        initRangeSticks();
        initBandStepPanels();
    }

    void updateViewElements(){
        txtBandCenter1.setText(objRange1.getViewBandWidth());
        txtBandCenter2.setText(objRange2.getViewBandWidth());
        showSuppressBands(objRange1.getRangeMask(), objRange2.getRangeMask());
        updateBandStepPanels();
    }

    void setRangeMask(int rangeNum, int pos){
        int mask = rangeNum == 1 ? objRange1.getRangeMask() : objRange2.getRangeMask();
        mask ^= (1<<pos);
        if(rangeNum == 1)objRange1.setRangeMask(mask);
        else             objRange2.setRangeMask(mask);
        updateViewElements();
    }
    @Override
    public void onClick(View v) {
        vibro();
        int pos;
        int mask;
        String name = getResources().getResourceName(v.getId());
        String s1;
        Log.i("MY_TEG", name);
        if(name.contains("sFrqBandButtonBand_1")){
            pos = Integer.parseInt(name.substring(name.lastIndexOf("_")+1));
            setRangeMask(1, pos);
        }
        if(name.contains("sFrqBandButtonBand_2")){
            pos = Integer.parseInt(name.substring(name.lastIndexOf("_")+1));
            setRangeMask(2, pos);
        }

    }
}