 package com.eshelon.prizma_prev;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eshelon.prizma_prev.object.ObjRange;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout bttnShowRangesList;
    LinearLayout bttnSuppress;
    RelativeLayout bttnPatt1;
    RelativeLayout bttnPatt2;
    RelativeLayout bttnPatt3;
    RelativeLayout bttnPatt4;

    MenuItem menuItem;
    MenuItem menuJmrList;
    MenuItem menuBtDevInfo;



    Vibrator vibrator;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("Настройки");
        menu.add("Открыть");
        menu.add("Сохранить");
        return true;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        menuItem         = menu.findItem(R.id.menuBt);
//        menuBtDevInfo    = menu.findItem(R.id.menuBtDevInfo);
//        menuJmrList      = menu.findItem(R.id.menuShowJmrList);
//        return true;
//    }

    void initViewElements(){
        bttnShowRangesList = findViewById(R.id.sMainButtonRanges);
        bttnShowRangesList.setOnClickListener(this);

        bttnSuppress = findViewById(R.id.sMainButtonSuppress);
        bttnSuppress.setOnClickListener(this);

        bttnPatt1 = findViewById(R.id.sMainButtonPatt1);
        bttnPatt1.setOnClickListener(this);

        bttnPatt2 = findViewById(R.id.sMainButtonPatt2);
        bttnPatt2.setOnClickListener(this);

        bttnPatt3 = findViewById(R.id.sMainButtonPatt3);
        bttnPatt3.setOnClickListener(this);

        bttnPatt4 = findViewById(R.id.sMainButtonPatt4);
        bttnPatt4.setOnClickListener(this);
    }
    void init(){
        context = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initViewElements();
        initRangesList();
    }

    void showPageRanges(){
        Intent i = new Intent(context, RangesActivity.class);
        startActivity(i);
    }
    void bttnSuppress(){
        if(G_.bttnSuppressState){
            G_.bttnSuppressState = false;
            bttnSuppress.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_suppress_off, null));
        }else {
            G_.bttnSuppressState = true;
            bttnSuppress.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_suppress_on, null));
        }
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

    void resetColorPatternButtons(){
        bttnPatt1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));
        bttnPatt2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));
        bttnPatt3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));
        bttnPatt4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));

    }
    void selectPattern(int bttnId){
        G_.selectPattern = bttnId;
        resetColorPatternButtons();
        switch (bttnId){
            case 1: bttnPatt1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern_select, null)); break;
            case 2: bttnPatt2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern_select, null)); break;
            case 3: bttnPatt3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern_select, null)); break;
            case 4: bttnPatt4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern_select, null)); break;
        }
    }

    void initRangesList(){
        ObjRange o;
        o = new ObjRange( 400,  600);
        G_.rangeList.add(o);
        o = new ObjRange( 600,  800);
        G_.rangeList.add(o);
        o = new ObjRange( 750,  900);
        G_.rangeList.add(o);
        o = new ObjRange( 900, 1050);
        G_.rangeList.add(o);
        o = new ObjRange(1000, 1300);
        G_.rangeList.add(o);
        o = new ObjRange(1300, 1500);
        G_.rangeList.add(o);
        o = new ObjRange(1500, 1700);
        G_.rangeList.add(o);
        o = new ObjRange(1700, 1900);
        G_.rangeList.add(o);
        o = new ObjRange(1900, 2100);
        G_.rangeList.add(o);
        o = new ObjRange(2100, 2300);
        G_.rangeList.add(o);
        o = new ObjRange(2300, 2500);
        G_.rangeList.add(o);
        o = new ObjRange(2500, 2700);
        G_.rangeList.add(o);
        o = new ObjRange(2700, 2900);
        G_.rangeList.add(o);
        o = new ObjRange(2900, 3100);
        G_.rangeList.add(o);
        o = new ObjRange(3100, 3400);
        G_.rangeList.add(o);
        o = new ObjRange(3400, 3700);
        G_.rangeList.add(o);
        o = new ObjRange(3700, 4000);
        G_.rangeList.add(o);
        o = new ObjRange(4000, 4400);
        G_.rangeList.add(o);
        o = new ObjRange(4400, 4800);
        G_.rangeList.add(o);
        o = new ObjRange(4800, 5100);
        G_.rangeList.add(o);
        o = new ObjRange(5100, 5400);
        G_.rangeList.add(o);
        o = new ObjRange(5400, 5700);
        G_.rangeList.add(o);
        o = new ObjRange(5700, 5900);
        G_.rangeList.add(o);
        o = new ObjRange(5900, 6200);
        G_.rangeList.add(o);
    }





























    public void onClick(View v) {
        vibro();

        int vId = v.getId();
        if(vId == R.id.sMainButtonRanges)showPageRanges();
        if(vId == R.id.sMainButtonSuppress)bttnSuppress();
        if(vId == R.id.sMainButtonPatt1)selectPattern(1);
        if(vId == R.id.sMainButtonPatt2)selectPattern(2);
        if(vId == R.id.sMainButtonPatt3)selectPattern(3);
        if(vId == R.id.sMainButtonPatt4)selectPattern(4);


    }
}