package com.eshelon.prizma_prev;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eshelon.prizma_prev.objects.ObjRange;

public class FrqBandActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    Vibrator vibrator;

    TextView txtSelectRange;
    TextView txtCenterFrq;
    TextView txtStartRange;
    TextView txtStopRange;
    TextView txtSuppressBand;

    LinearLayout bttnSave;
    LinearLayout bttnCansel;
    SeekBar seekBar;
    Spinner spinner;

    ObjRange objRange;
    int bandWidth;
    Integer bandCenter;

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

    void setBand(){
        String str = Integer.toString(bandCenter);
        txtCenterFrq.setText(str);
        str = Integer.toString(bandCenter - bandWidth/2)+" - "+Integer.toString(bandCenter + bandWidth/2)+" Мгц";
        txtSuppressBand.setText(str);
    }
    void initTxtData(){
        objRange = G_.rangeList.get(G_.selectRange);
        bandCenter = (objRange.stop-objRange.start)/2+objRange.start;
        String str = objRange.num.toString()+": "+objRange.view;
        txtSelectRange.setText(str);
        str = bandCenter.toString()+" МГц";
        txtCenterFrq.setText(str);
        str = objRange.start.toString();
        txtStartRange.setText(str);
        str = objRange.stop.toString();
        txtStopRange.setText(str);
        str = objRange.view;
        txtSuppressBand.setText(str);
    }
    void initSeekBar(){
        seekBar.setMax(objRange.width);
        seekBar.setProgress(objRange.width/2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bandCenter = objRange.start+progress;
                setBand();
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
        initViewElements();
        initTxtData();
        initSeekBar();
        initSpinner();
    }

    void initSpinner(){
        Integer[] arrWidth = {10,20,30,40,50};
        String[] arrString = {"10","20","30","40","50"};
        bandWidth = arrWidth[0];
//        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrString);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(),arrString);
        spinner.setAdapter(customAdapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bandWidth = arrWidth[position];
                setBand();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }
    void initViewElements(){
        txtSelectRange  = findViewById(R.id.sFrqBandRange)     ;
        txtCenterFrq    = findViewById(R.id.sFrqBandCenter)    ;
        txtStartRange   = findViewById(R.id.sFrqBandRangeStart);
        txtStopRange    = findViewById(R.id.sFrqBandRangeStop) ;
        txtSuppressBand = findViewById(R.id.sFrqBandSuppress)  ;
        seekBar         = findViewById(R.id.sFrqBandSeekBar)   ;
        spinner         = findViewById(R.id.sFrqBandFrqSpinner);


        bttnSave   = findViewById(R.id.sFrqBandButtonSave)     ;
        bttnCansel = findViewById(R.id.sFrqBandButtonCansel)   ;

        bttnSave  .setOnClickListener(this);
        bttnCansel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        vibro();
    }
}