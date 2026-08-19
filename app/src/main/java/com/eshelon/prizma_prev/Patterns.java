package com.eshelon.prizma_prev;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.eshelon.prizma_prev.C_.CMD_SELECT_PATTERN;
import static com.eshelon.prizma_prev.C_.CMD_UPDATE_JMMR_LIST_PANEL;
import static com.eshelon.prizma_prev.C_.CMD_UPDATE_PATTERN_LIST;
import static com.eshelon.prizma_prev.C_.DB_VERSION;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.eshelon.prizma_prev.adapter.PatternAdapter;
import com.eshelon.prizma_prev.interfaces.MainInterface;
import com.eshelon.prizma_prev.objects.JmmrState;
import com.eshelon.prizma_prev.objects.ObjectProcessingData;

import java.util.ArrayList;

public class Patterns{
    TextView sMainButtonPatt1Txt;
    TextView sMainButtonPatt2Txt;
    TextView sMainButtonPatt3Txt;
    TextView sMainButtonPatt4Txt;
    RelativeLayout bttnPatt1;
    RelativeLayout bttnPatt2;
    RelativeLayout bttnPatt3;
    RelativeLayout bttnPatt4;
    RelativeLayout sMessageModalWindow;
    LinearLayout sMainPatternListPanel;
    FrameLayout sMainSubBackground;
    ListView sMainPatternList;
    ArrayList<JmmrState>mPatternButtonList = new ArrayList<>();
    int mPatternSelected = 0;
    PatternAdapter patternAdapter;
    Context context;
    ArrayList<Integer> mPatternPreferenceList = new ArrayList<>();
    ArrayList<Integer> mPatternActiveList = new ArrayList<>();
    boolean mOnLongToutch;

    MainInterface mainInterface;
    public Patterns(Context _context, MainInterface _mainInterface) {
        context = _context;
        mainInterface = _mainInterface;
        init();
    }

    private void init(){
        initViewElements();
        initPatterns();
    }
    private void selectPattern(ObjectProcessingData o){
        sMainPatternListPanel.setVisibility(GONE);
        sMainSubBackground.setVisibility(GONE);
        JmmrState jmmr = (JmmrState)o.object;
        if(jmmr == null) return;
        Log.i("MY_TEG", "id -> "+jmmr.db_id+" mPatternSelected -> "+mPatternSelected);
        new Preferences(context).setPatternNum(mPatternSelected, jmmr.db_id);
        updateView();
    }
    private void initPatternAdapter(){
        ListView listView = ((Activity)context).findViewById(R.id.sMainPatternList);
        listView.setVisibility(VISIBLE);
        if(patternAdapter != null)patternAdapter = null;
        patternAdapter = new PatternAdapter(context, R.layout.pattern_list_item, G_.pattern_list, new MainInterface() {
            @Override
            public void cb(ObjectProcessingData o) {
                switch (o.cmd){
                    case CMD_SELECT_PATTERN     : selectPattern(o); break;
                    case CMD_UPDATE_PATTERN_LIST: initPatterns();   break;
                }
            }
        });
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(patternAdapter);
            }
        });
    }

    private void initPatterns(){
        Db dbHelper = new Db(context.getApplicationContext(), "dbName", null, DB_VERSION);
        dbHelper.initDb();
        G_.pattern_list = dbHelper.readDataFromDb();
        if(G_.pattern_list != null){
            for(int i=0; i<G_.pattern_list.size(); i++){
                Log.i("MY_TEG", "pattName -> "+G_.pattern_list.get(i).patt_name+"; mask1 -> "+
                        G_.pattern_list.get(i).msk1+ "; mask2 -> "+G_.pattern_list.get(i).msk2);
            }
        }
        initPatternAdapter();
    }
    void animePatternButton(int buttonId){
        resetColorPatternButtons();
        int clr = R.drawable.button_active;
        switch (buttonId){
            case 0: bttnPatt1.setBackgroundResource (clr); break;
            case 1: bttnPatt2.setBackgroundResource (clr); break;
            case 2: bttnPatt3.setBackgroundResource (clr); break;
            case 3: bttnPatt4.setBackgroundResource (clr); break;
        }
    }
    private void setPattern(int bttnId){
        Log.i("MY_TEG", "selectPattern-----");
        for(JmmrState jmmr : G_.jmmr_list){
            if(jmmr.dev_range == mPatternButtonList.get(bttnId).dev_range){
                jmmr.msk1 = mPatternButtonList.get(bttnId).msk1;
                jmmr.msk2 = mPatternButtonList.get(bttnId).msk2;
                jmmr.mc1  = mPatternButtonList.get(bttnId).mc1;
                jmmr.mc2  = mPatternButtonList.get(bttnId).mc2;
                jmmr.pwr1 = mPatternButtonList.get(bttnId).pwr1;
                jmmr.pwr2 = mPatternButtonList.get(bttnId).pwr2;
            }
        }

        ObjectProcessingData o = new ObjectProcessingData();
        o.cmd = CMD_UPDATE_JMMR_LIST_PANEL;
        mainInterface.cb(o);
    }
    private void resetColorPatternButtons(){
        bttnPatt1.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.button_unpress, null));
        bttnPatt2.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.button_unpress, null));
        bttnPatt3.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.button_unpress, null));
        bttnPatt4.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.button_unpress, null));

        if(mPatternActiveList == null) return;
        if(mPatternActiveList.isEmpty()) return;

        if(mPatternActiveList.get(0) == 1)bttnPatt1.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.button_unpress_active, null));
        if(mPatternActiveList.get(1) == 1)bttnPatt2.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.button_unpress_active, null));
        if(mPatternActiveList.get(2) == 1)bttnPatt3.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.button_unpress_active, null));
        if(mPatternActiveList.get(3) == 1)bttnPatt4.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.button_unpress_active, null));

    }
    private JmmrState getJmmrFromDb(int id){
        if(id == -1)return null;
        for(JmmrState jmmr : G_.pattern_list){
            if(jmmr.db_id == id)return jmmr;
        }
        return null;
    }
    public void updateView(){
        if((G_.jmmr_list == null)||(G_.pattern_list == null))return;
        mPatternPreferenceList = new Preferences(context).getPatternList();
        mPatternActiveList = new ArrayList<>();
        mPatternButtonList = new ArrayList<>();
        for(int i=0; i<4; i++) mPatternButtonList.add(getJmmrFromDb(mPatternPreferenceList.get(i)));
        for(int i=0; i<4; i++) mPatternActiveList.add(0);
        for(JmmrState jmmr1 : G_.jmmr_list){
            for(int i=0; i<4; i++){
                if(mPatternButtonList.get(i) != null){
                    if(mPatternButtonList.get(i).dev_range == jmmr1.dev_range){
                        mPatternActiveList.set(i, 1);

                    }

                }
            }

        }
        updatePatternTitles();
        resetColorPatternButtons();
    }
    private void initViewElements(){
        sMainPatternList      = ((Activity)context).findViewById(R.id.sMainPatternList);
        sMainPatternListPanel = ((Activity)context).findViewById(R.id.sMainPatternListPanel);

        sMainSubBackground = ((Activity)context).findViewById(R.id.sMainSubBackground);
        sMainSubBackground.setOnTouchListener(mainOnTouchListener);

        bttnPatt1 = ((Activity)context).findViewById(R.id.sMainButtonPatt1);
        bttnPatt1.setOnTouchListener(mainOnTouchListener);
        bttnPatt1.setOnLongClickListener(mainOnLongClickListener);

        bttnPatt2 = ((Activity)context).findViewById(R.id.sMainButtonPatt2);
        bttnPatt2.setOnTouchListener(mainOnTouchListener);
        bttnPatt2.setOnLongClickListener(mainOnLongClickListener);

        bttnPatt3 = ((Activity)context).findViewById(R.id.sMainButtonPatt3);
        bttnPatt3.setOnTouchListener(mainOnTouchListener);
        bttnPatt3.setOnLongClickListener(mainOnLongClickListener);

        bttnPatt4 = ((Activity)context).findViewById(R.id.sMainButtonPatt4);
        bttnPatt4.setOnTouchListener(mainOnTouchListener);
        bttnPatt4.setOnLongClickListener(mainOnLongClickListener);

        sMessageModalWindow = ((Activity)context).findViewById(R.id.sMessageModalWindow);

        sMainButtonPatt1Txt = ((Activity)context).findViewById(R.id.sMainButtonPatt1Txt);
        sMainButtonPatt2Txt = ((Activity)context).findViewById(R.id.sMainButtonPatt2Txt);
        sMainButtonPatt3Txt = ((Activity)context).findViewById(R.id.sMainButtonPatt3Txt);
        sMainButtonPatt4Txt = ((Activity)context).findViewById(R.id.sMainButtonPatt4Txt);
    }
    private void onPressButton(int vId){
        if(mOnLongToutch){
            mOnLongToutch = false;
            return;
        }
        if(vId == R.id.sMainButtonPatt1){setPatternBand(0);}
        if(vId == R.id.sMainButtonPatt2){setPatternBand(1);}
        if(vId == R.id.sMainButtonPatt3){setPatternBand(2);}
        if(vId == R.id.sMainButtonPatt4){setPatternBand(3);}
        if(vId == R.id.sMainSubBackground){
            hiddenNeedlessPanels();
        }
    }
    void hiddenNeedlessPanels(){
        sMainSubBackground.setVisibility(GONE);
        sMessageModalWindow.setVisibility(GONE);
        sMainPatternListPanel.setVisibility(GONE);
    }
    private void updatePatternTitles(){
        ArrayList<String>stringArrayList = new ArrayList<>();
        for(JmmrState jmmr : mPatternButtonList){
            if(jmmr == null)stringArrayList.add("---");
            else            stringArrayList.add(jmmr.patt_name);
        }
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sMainButtonPatt1Txt.setText(stringArrayList.get(0));
                sMainButtonPatt2Txt.setText(stringArrayList.get(1));
                sMainButtonPatt3Txt.setText(stringArrayList.get(2));
                sMainButtonPatt4Txt.setText(stringArrayList.get(3));

            }
        });
    }
    private void setPatternBand(int patt){
        if(mPatternActiveList.get(patt) == 0)return;
        if(mPatternButtonList.get(patt) == null)return;
        setPattern(patt);
        animePatternButton(patt);
    }
    private final View.OnTouchListener mainOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int vId = v.getId();
            int color  = 0;
            boolean returnVal = true;
            if(vId == R.id.sMainButtonPatt1)returnVal = false;
            if(vId == R.id.sMainButtonPatt2)returnVal = false;
            if(vId == R.id.sMainButtonPatt3)returnVal = false;
            if(vId == R.id.sMainButtonPatt4)returnVal = false;
            AnimeViewElements anime = new AnimeViewElements();

            switch (event.getAction()){
                case ACTION_DOWN : anime.onTouch((Activity) context, v, true, color); return returnVal;
                case ACTION_UP   : anime.onTouch((Activity) context, v, false, 0);onPressButton(vId); break;
            }
            v.performClick();
            return false;
        }
    };
    private final View.OnLongClickListener mainOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int vId = v.getId();
            mOnLongToutch = true;
            resetColorPatternButtons();
            sMainPatternListPanel.setVisibility(VISIBLE);
            sMainSubBackground.setVisibility(VISIBLE);
            if(vId == R.id.sMainButtonPatt1)mPatternSelected = 1;
            if(vId == R.id.sMainButtonPatt2)mPatternSelected = 2;
            if(vId == R.id.sMainButtonPatt3)mPatternSelected = 3;
            if(vId == R.id.sMainButtonPatt4)mPatternSelected = 4;
            return true;
        }
    };
}
