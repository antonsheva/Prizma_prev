package com.eshelon.prizma_prev;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eshelon.prizma_prev.objects.ObjRange;

import java.util.ArrayList;

public class NarrowBandActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    Vibrator vibrator;

    TextView txtSelectRange;
    TextView txtBandCenter1;
    TextView txtBandCenter2;
    TextView txtSuppressBand;
    TextView sFrqBandDevAddr;
    TextView sFrqBandEnDisAllTxt;
    RelativeLayout sFrqBandOnOffSuppress;

    RelativeLayout sFrqBandButtonSave;
    RelativeLayout bttnCansel;
    RelativeLayout sFrqBandButtonEnableDisableAll;
    RelativeLayout sFrqBandOnOffChnlBttn1;
    RelativeLayout sFrqBandOnOffChnlBttn2;

    TextView sFrqBandOnOffChnlTxt1;
    TextView sFrqBandOnOffChnlTxt2;



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

    boolean mSwchEnableDisable = false;
    boolean mSwchOnOffChnl1;
    boolean mSwchOnOffChnl2;


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
        if(G_.rangeList != null){
            if(G_.rangeList.size() >= G_.selectRange*2+1){
                objRange2 = G_.rangeList.get(G_.selectRange*2);
                objRange1 = G_.rangeList.get(G_.selectRange*2+1);
            }
        }

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
                updateViewElements("onProgressChanged");
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
                updateViewElements("onProgressChanged 2");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    void initJmmrData(){
        if(G_.currentJmmrNum != -1){
            if(G_.jmmr_list != null){
                if((G_.jmmr_list.get(G_.currentJmmrNum) != null)){
                    G_.currentJmmr = G_.jmmr_list.get(G_.currentJmmrNum);
                    objRange1.setRangeMask(G_.jmmr_list.get(G_.currentJmmrNum).msk1);
                    objRange2.setRangeMask(G_.jmmr_list.get(G_.currentJmmrNum).msk2);
                    String str = G_.currentJmmr.ad_esp+" ";
                    sFrqBandDevAddr.setText(str);
                    mSwchOnOffChnl1 = G_.jmmr_list.get(G_.currentJmmrNum).pwr1 == 1;
                    mSwchOnOffChnl2 = G_.jmmr_list.get(G_.currentJmmrNum).pwr2 == 1;
                }
            }
        }
    }
    void initOnOffChnlBttn(){
        if(mSwchOnOffChnl1){
            sFrqBandOnOffChnlTxt1.setText("Выкл.канал");
            sFrqBandOnOffChnlBttn1.setBackgroundResource(R.drawable.button_suppress_on);
        }else{
            sFrqBandOnOffChnlTxt1.setText("Вкл.канал");
            sFrqBandOnOffChnlBttn1.setBackgroundResource(R.drawable.button_pattern);
        }

        if(mSwchOnOffChnl2){
            sFrqBandOnOffChnlTxt2.setText("Выкл.канал");
            sFrqBandOnOffChnlBttn2.setBackgroundResource(R.drawable.button_suppress_on);
        }else{
            sFrqBandOnOffChnlTxt2.setText("Вкл.канал");
            sFrqBandOnOffChnlBttn2.setBackgroundResource(R.drawable.button_pattern);
        }
    }
    void init(){
        context = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getRangeObjects();
        initViewElements();
        initTxtData();
        initSeekBar();
        initSpinner();

        initJmmrData();
        initOnOffChnlBttn();
        updateViewElements("init");
    }

    boolean spinnerLatch1 = false;
    boolean spinnerLatch2 = false;
    void initSpinner(){
        CustomAdapter customAdapter1=new CustomAdapter(getApplicationContext(),objRange1.getViewBandList());
        spinner1.setAdapter(customAdapter1);
        CustomAdapter customAdapter2=new CustomAdapter(getApplicationContext(),objRange2.getViewBandList());
        spinner2.setAdapter(customAdapter2);


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerLatch1){
                    objRange1.setCurrentBand(position);
                    updateViewElements("onItemSelected 1");
                }else {
                    spinnerLatch1 = true;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerLatch2){
                    objRange2.setCurrentBand(position);
                    updateViewElements("onItemSelected 2");
                }else {
                    spinnerLatch2 = true;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void showSuppressBands(long mask1, long mask2){

        Log.i("MY_TEG", "mask1        -> "+mask1);


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
        for(int i=0; i<31; i++){
            swch1 = ((mask1 << i) & 0x40000000L) == 0x40000000L;
            swch2 = ((mask2 << i) & 0x40000000L) == 0x40000000L;

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

        for(int i=0; i<C_.FRQ_STEP_QTY; i++){
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
        long tmp1 = objRange1.getRangeMask();
        long tmp2 = objRange2.getRangeMask();
        RelativeLayout rl1;
        RelativeLayout rl2;

        for(int i=0; i<C_.FRQ_STEP_QTY; i++){
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


        for(int i = 0; i<C_.FRQ_STEP_QTY; i++) {
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

        if((objRange1 == null) || (objRange2 == null)){
//            Toast.makeText(this, "Что-то пошло не так :-(", Toast.LENGTH_LONG).show();
            return;
        }
        for(int i=0; i<C_.FRQ_STEP_QTY; i++){
            viewBandStepButtonTextList1.get(i).setText(objRange1.getViewBandStepList().get(i));
            viewBandStepButtonTextList2.get(i).setText(objRange2.getViewBandStepList().get(i));
        }

    }
    void initViewElements(){
        sFrqBandOnOffChnlBttn1 = findViewById(R.id.sFrqBandOnOffChnlButton1);
        sFrqBandOnOffChnlBttn1.setOnClickListener(this);
        sFrqBandOnOffChnlBttn2 = findViewById(R.id.sFrqBandOnOffChnlButton2);
        sFrqBandOnOffChnlBttn2.setOnClickListener(this);

        sFrqBandButtonEnableDisableAll = findViewById(R.id.sFrqBandOnOffAllButton);
        sFrqBandButtonEnableDisableAll.setOnClickListener(this);
        sFrqBandEnDisAllTxt = findViewById(R.id.sFrqBandEnDisAllTxt);
        sFrqBandEnDisAllTxt.setText(mSwchEnableDisable ? "Выкл. все" : "Вкл. все");

        sFrqBandOnOffChnlTxt1 = findViewById(R.id.sFrqBandOnOffChnlTxt1);
        sFrqBandOnOffChnlTxt2 = findViewById(R.id.sFrqBandOnOffChnlTxt2);

        sFrqBandOnOffSuppress = findViewById(R.id.sFrqBandOnOffSuppress);
        sFrqBandOnOffSuppress.setOnClickListener(this);

        sFrqBandDevAddr = findViewById(R.id.sFrqBandDevAddr);

        txtBandCenter1  = findViewById(R.id.txtBandCenter1)      ;
        txtBandCenter2  = findViewById(R.id.txtBandCenter2)      ;

        txtSelectRange  = findViewById(R.id.sFrqBandRange)       ;

        seekBar1        = findViewById(R.id.sFrqBandSeekBar1)    ;
        seekBar2        = findViewById(R.id.sFrqBandSeekBar2)    ;

        spinner1        = findViewById(R.id.sFrqBandFrqSpinner1) ;
        spinner2        = findViewById(R.id.sFrqBandFrqSpinner2) ;

        sFrqBandButtonSave = findViewById(R.id.sFrqBandButtonSave)  ;
        bttnCansel         = findViewById(R.id.sFrqBandButtonCansel);

        sFrqBandButtonSave.setOnClickListener(this);
        bttnCansel.setOnClickListener(this);

        initRangeSticks();
        initBandStepPanels();
    }

    void updateViewElements(String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtBandCenter1.setText(objRange1.getViewBandWidth());
                txtBandCenter2.setText(objRange2.getViewBandWidth());
                showSuppressBands(objRange1.getRangeMask(), objRange2.getRangeMask());
                updateBandStepPanels();
            }
        });
    }

    void saveDataToJmmrList(){
        if(G_.currentJmmrNum != -1){
            if(G_.jmmr_list != null){
                G_.jmmr_list.get(G_.currentJmmrNum).msk1 = objRange1.getRangeMask();
                G_.jmmr_list.get(G_.currentJmmrNum).msk2 = objRange2.getRangeMask();
            }
        }
    }
    void setRangeMask(long rangeNum, long pos){
        long mask = rangeNum == 1 ? objRange1.getRangeMask() : objRange2.getRangeMask();
        mask ^= (1<<pos);
        if(rangeNum == 1)objRange1.setRangeMask(mask);
        else             objRange2.setRangeMask(mask);
        updateViewElements("setRangeMask");
    }

    @Override
    public void onClick(View v) {
        vibro();
        int pos;
        String name = getResources().getResourceName(v.getId());
        if(name.contains("sFrqBandButtonBand_1")){
            pos = Integer.parseInt(name.substring(name.lastIndexOf("_")+1));
            setRangeMask(1, pos);
        }
        if(name.contains("sFrqBandButtonBand_2")){
            pos = Integer.parseInt(name.substring(name.lastIndexOf("_")+1));
            setRangeMask(2, pos);
        }

        if(v.getId() == R.id.sFrqBandOnOffAllButton){
            if(!mSwchEnableDisable){
                objRange1.setRangeMask(0x7FFFFFFFL);
                objRange2.setRangeMask(0x7FFFFFFFL);
                sFrqBandEnDisAllTxt.setText("Выкл.все");
            }else {
                objRange1.setRangeMask(0L);
                objRange2.setRangeMask(0L);
                sFrqBandEnDisAllTxt.setText("Вкл.все");
            }
            mSwchEnableDisable = !mSwchEnableDisable;

            updateViewElements(" ");
        }
        if(v.getId() == R.id.sFrqBandOnOffChnlButton1){
            mSwchOnOffChnl1 = !mSwchOnOffChnl1;
            G_.jmmr_list.get(G_.currentJmmrNum).pwr1 = mSwchOnOffChnl1 ? 1 : 2;
            initOnOffChnlBttn();
        }
        if(v.getId() == R.id.sFrqBandOnOffChnlButton2){
            mSwchOnOffChnl2 = !mSwchOnOffChnl2;
            G_.jmmr_list.get(G_.currentJmmrNum).pwr2 = mSwchOnOffChnl2 ? 1 : 2;
            initOnOffChnlBttn();
        }
        if(v.getId() == R.id.sFrqBandButtonSave){
            saveDataToJmmrList();
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("cmd_return", true);
            startActivity(i);
        }
        if(v.getId() == R.id.sFrqBandOnOffSuppress){
            saveDataToJmmrList();
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("cmd_suppress", true);
            i.putExtra("cmd_return", true);
            startActivity(i);
        }
        if(v.getId() == R.id.sFrqBandButtonCansel){
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("cmd_return", true);
            startActivity(i);
        }
    }


}