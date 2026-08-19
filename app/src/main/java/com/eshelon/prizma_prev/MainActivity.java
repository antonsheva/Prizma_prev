 package com.eshelon.prizma_prev;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_LONG;
import static com.eshelon.prizma_prev.C_.BT_CONNECTING_ICON_STATE_CONNECTED;
import static com.eshelon.prizma_prev.C_.BT_CONNECTING_ICON_STATE_DISABLE;
import static com.eshelon.prizma_prev.C_.BT_CONNECTING_ICON_STATE_ENABLE;
import static com.eshelon.prizma_prev.C_.BT_CONNECTING_ICON_STATE_SEARCHING;
import static com.eshelon.prizma_prev.C_.BT_STATE_CONNECTED;
import static com.eshelon.prizma_prev.C_.BT_STATE_CONNECTING;
import static com.eshelon.prizma_prev.C_.BT_STATE_DISCONNECTED;
import static com.eshelon.prizma_prev.C_.BT_UPDATE_ICON_STATE_GONE;
import static com.eshelon.prizma_prev.C_.BT_UPDATE_ICON_STATE_UPDATE;
import static com.eshelon.prizma_prev.C_.BT_UPDATE_ICON_STATE_VISIBLE;
import static com.eshelon.prizma_prev.C_.CMD_SELECT_PATTERN;
import static com.eshelon.prizma_prev.C_.CMD_UPDATE_PATTERN_LIST;
import static com.eshelon.prizma_prev.C_.DB_VERSION;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eshelon.prizma_prev.adapter.DevListAdapter;
import com.eshelon.prizma_prev.adapter.PatternAdapter;
import com.eshelon.prizma_prev.interfaces.CB;
import com.eshelon.prizma_prev.interfaces.ItemDevSelListener;

import com.eshelon.prizma_prev.interfaces.ItemPatternListener;
import com.eshelon.prizma_prev.objects.JmmrState;
import com.eshelon.prizma_prev.objects.ObjRange;
import com.eshelon.prizma_prev.objects.ObjectMsg;
import com.eshelon.prizma_prev.objects.ObjectProcessingData;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout bttnShowRangesList;
    RelativeLayout sMainButtonSave;
    LinearLayout bttnSuppress;
    RelativeLayout bttnPatt1;
    RelativeLayout bttnPatt2;
    RelativeLayout bttnPatt3;
    RelativeLayout bttnPatt4;

    TextView sMainButtonPatt1Txt;
    TextView sMainButtonPatt2Txt;
    TextView sMainButtonPatt3Txt;
    TextView sMainButtonPatt4Txt;
    LinearLayout sMainPatternListPanel;
    FrameLayout sMainSubBackground;
    ImageView btDevInfo;
    ImageView btUpdateDevList;
    ImageView btSearch;
    ListView mainLV;
    ListView sMainPatternList;
    DevListAdapter devListAdapter;
    Vibrator vibrator;
    Context context;
    Timer animeTmBtSign = new Timer();
    boolean tryToConnect = false;
    boolean mVisiblePatternList = false;
    CB onConnectCb;
    CbBtReceive cbBtReceive;
    AnimeViewElements mAnime = new AnimeViewElements();
    int mTryConnectTime = 0;
    int mNeedCloseConnection = 0;
    int mTimeBlockButton = 0;
    boolean mWaitBtresponse = false;
    int mPatternPanelCnt = 0;
    int mPatternSelected = 0;
    ArrayList<Integer>mPatternList = new ArrayList<>();
    ArrayList<Integer>mPatternActive = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        Log.i("MY_TEG", "onCreate - - MainActivity");
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onResume() {
         super.onResume();
         Log.i("MY_TEG", "onResume - - MainActivity 1");
         setBtIcon(C_.BT_ICON_ENABLE);
         initPatternsPanel();
         if (!G_.selectBtDevice.isDeviceSelected())return;
         if(G_.btActiveState == BT_STATE_CONNECTED)return;

         Log.i("MY_TEG", "onResume - - MainActivity 2");
         Log.i("MY_TEG", "MAC -> "+G_.selectBtDevice.getMac());

         G_.btActiveState = BT_STATE_CONNECTING;
         G_.btConnect = new BtConnect(this, G_.selectBtDevice.getMac(), onConnectCb);
         btConnect();
     }

    @Override
    protected void onStart() {
         super.onStart();
         Log.i("MY_TEG", "---onStart  ---");
     }

    void setAnimateBtState(){
        switch (G_.btActiveState ){
            case BT_STATE_DISCONNECTED:
                G_.animeBtConnectionIconState = BT_CONNECTING_ICON_STATE_ENABLE;
                G_.animeBtUpdateIconState = BT_UPDATE_ICON_STATE_GONE;
                break;
            case BT_STATE_CONNECTED :
                G_.animeBtConnectionIconState = BT_CONNECTING_ICON_STATE_CONNECTED;
                G_.animeBtUpdateIconState = BT_UPDATE_ICON_STATE_VISIBLE;
                break;
        }
     }
    void reInitCbFunctions(){
         cbBtReceive = null;
         onConnectCb = null;
         initCbBtReceive();
         initCbOnConnect();
     }
    void initCbFunctions(){
         initCbBtReceive();
         initCbOnConnect();
     }
    void initCbBtReceive(){
         cbBtReceive = new CbBtReceive() {
             @Override
             public void cb(int code, String data) {
                 Log.i("MY_TEG", "---- BT DATA  - --------");
                 switch (code){
                     case C_.CB_CODE_NEW_DATA   : receiveBtData(data);                           break;
                     case C_.CB_CODE_DISCONNECT :
                         Log.i("MY_TEG", "---- BT DISCONNECT  - --------");
                         G_.btActiveState = C_.CB_CODE_DISCONNECT;
                         G_.animeBtConnectionIconState = BT_CONNECTING_ICON_STATE_SEARCHING;
                         G_.animeBtUpdateIconState = BT_UPDATE_ICON_STATE_GONE;
                         Log.i("MY_TEG", " -- - tryRecoveryConnection  - cbBtReceive--------2");
                         btConnect();
                         break;
                 }
             }
         };
     }
    void initCbOnConnect(){
         onConnectCb = new CB() {
             @Override
             public void cb(int code) {
                 Log.i("MY_TEG", "onConnectCb code -> "+code+" ");
                 if(G_.btConnect == null){
                     Log.i("MY_TEG", "G_.btConnect =  null");
                     G_.animeBtConnectionIconState = BT_CONNECTING_ICON_STATE_ENABLE;
                     return;
                 }

                 
                 ReceiveThread rThrd = G_.btConnect.connectThread.getReceiveThread();
                 if(code == C_.CB_CODE_ERROR_CONNECT){

                     if(rThrd == null){
                         G_.btActiveState = BT_STATE_DISCONNECTED;
                         G_.animeBtConnectionIconState = BT_CONNECTING_ICON_STATE_SEARCHING;
                         G_.animeBtUpdateIconState = BT_UPDATE_ICON_STATE_GONE;
                         Log.i("MY_TEG", " -- - tryRecoveryConnection  - onConnectCb--------1");
                         mTryConnectTime = 2;
                         return;
                     }
                 }
                 if(code==C_.CB_CODE_CONNECT){
                     G_.btActiveState = BT_STATE_CONNECTED;
                     G_.animeBtConnectionIconState = BT_CONNECTING_ICON_STATE_CONNECTED;
                     G_.animeBtUpdateIconState = BT_UPDATE_ICON_STATE_VISIBLE;
                     Log.i("MY_TEG", " -- -  CB_CODE_CONNECT  - onConnectCb--------1");
                     if(rThrd == null){
                         btConnect();
                         return;
                     }
                     rThrd.setCbReceive(cbBtReceive);
                     G_.btWaitOnConnect = true;
                 }
             }
         };
     }

    Timer tmMonitor = new Timer();
    void initTmMonitor(){
         tmMonitor.schedule(new TimerTask() {
            @Override
            public void run() {
                if(G_.btWaitOnConnect){
                    Log.i("MY_TEG", "tmMonitor - - -");
                    G_.btWaitOnConnect = false;
                    getJmmrList();
                }

                if(mTimeBlockButton > 0)mTimeBlockButton--;

                if(mNeedCloseConnection > 0)mNeedCloseConnection--;
                if(mNeedCloseConnection == 1)closeBtConnectionFull();
                if(mTryConnectTime > 0)mTryConnectTime--;
                if(mTryConnectTime == 1)btConnect();

            }
        },300,300);
     }

    Timer tmWaitBtResponse;
    PatternAdapter patternAdapter;
    void initTmWaitBtResponse(){
        mWaitBtresponse = true;
        tmWaitBtResponse = new Timer();
        tmWaitBtResponse.schedule(new TimerTask() {
            @Override
            public void run() {
               if(G_.btHasNewData) {
                   G_.animeBtUpdateIconState = BT_UPDATE_ICON_STATE_VISIBLE;
                   initDevListAdapter();
                   if(mVisiblePatternList)initPatternAdapter();
                   if(G_.pattern_select_list != null)G_.pattern_select_list = null;
                   mPatternPanelCnt = 0;
                   G_.pattern_select_list = new ArrayList<>();
                   initPatternsPanel();
                   tmWaitBtResponse.cancel();
                   tmWaitBtResponse = null;
                   G_.btHasNewData = false;
                   mWaitBtresponse = false;
               }
            }
        },300, 100);
    };
    void getJmmrList(){
        initTmWaitBtResponse();
        G_.jmmr_list =new ArrayList<>();
        G_.animeBtUpdateIconState = BT_UPDATE_ICON_STATE_UPDATE;
        btSendCmd(C_.CMD_GET_JMMR_LIST);
    }
    void btConnect(){
        if(G_.btConnect == null){
            G_.animeBtConnectionIconState = BT_CONNECTING_ICON_STATE_ENABLE;
            Log.i("MY_TEG", "btConnect - - NULL");
            return;
        }
         G_.animeBtConnectionIconState = BT_CONNECTING_ICON_STATE_SEARCHING;
         G_.btConnect.connect();
     }
    private void btSendJmmrList(boolean needBtOff){
         if(G_.jmmr_list == null)return;
         ObjectMsg msg = new ObjectMsg();
         if(needBtOff)msg.need_bt_off = 1;
         else         msg.need_bt_off = 0;
         msg.cmd = C_.CMD_SET_JMMR_LIST;

         msg.jmmr_list = G_.jmmr_list;
         msg.jmmr_list_len = G_.jmmr_list.size();
         btSendData(msg, 0);
    }
    private void btSendData(Object o, int type){
         if(G_.btActiveState != BT_STATE_CONNECTED){
             new MessageBox(this).showMessage("Отсутствует подключение");
             return;
         }
         String jsonStr = "";
         ObjectMsg msg = (ObjectMsg)o;
         msg.addressee = G_.btDevAddr;
         try {
             jsonStr = new Gson().toJson(o);
         }catch (Exception e){
             Log.e("MY_TEG", e.toString());
             return;
         }
         String sendStr = "start___"+jsonStr+"_stop";
         int len = sendStr.length();
         int packQty = len/120;
         byte[] data = sendStr.getBytes();
         final int[] cnt = {0};
         final Timer[] tm = {new Timer()};
         final int[] pcQty = {packQty};
         final int[] qLen = {len % 120};
         tm[0].schedule(new TimerTask() {
             @Override
             public void run() {
                 int ln = (cnt[0] == pcQty[0]) ? qLen[0] : 120;
                 byte[] sendBuff = Arrays.copyOfRange(data, cnt[0] *120, cnt[0] *120+ln);
                 G_.btConnect.connectThread.getReceiveThread().sendData(sendBuff);
                 String str = new String(sendBuff, StandardCharsets.UTF_8);
                 Log.i("MY_TEG", str);
                 cnt[0]++;
                 if(cnt[0] > packQty){

                     tm[0].cancel();
                     tm[0] = null;
                 }
             }
         }, 10, 50);

     }
    private void btSendCmd(int cmd){
         ObjectMsg msg = new ObjectMsg();
         msg.cmd = cmd;
         String jsonStr = new Gson().toJson(msg);
         byte[] data = jsonStr.getBytes();
         Log.i("MY_TEG", new String(data));
         G_.btConnect.connectThread.getReceiveThread().sendData(data);
     }
    private void showToast(int toastId){
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 Toast.makeText(context, toastId, LENGTH_LONG).show();
             }
         });
     }
    private void animeBtStateIcon(){
         final boolean[] stt = {false};
         final int[] stt1 = {0};
         final int[] cnt = {0};
         animeTmBtSign.schedule(new TimerTask() {
             @Override
             public void run() {
                 cnt[0]++;
                 switch (G_.animeBtConnectionIconState){

                     case BT_CONNECTING_ICON_STATE_SEARCHING:
                         setUpdateIcon(4);
                         if(stt[0])setBtIcon(C_.BT_ICON_ENABLE);
                         else      setBtIcon(C_.BT_ICON_CONNECTED);
                         if((cnt[0]%3) == 0) stt[0] = !stt[0];
                         break;
                     case BT_CONNECTING_ICON_STATE_CONNECTED:
                         setUpdateIcon(5);
                         setBtIcon(C_.BT_ICON_CONNECTED);
                         break;

                     case BT_CONNECTING_ICON_STATE_ENABLE:
                         setUpdateIcon(4);
                         setBtIcon(C_.BT_ICON_ENABLE);
                         break;
                     case BT_CONNECTING_ICON_STATE_DISABLE:
                         setBtIcon(C_.BT_ICON_DISABLE);
                         break;
                 }

                 switch (G_.animeBtUpdateIconState){
                     case BT_UPDATE_ICON_STATE_GONE    : setUpdateIcon(4);
                     break;
                     case BT_UPDATE_ICON_STATE_VISIBLE : setUpdateIcon(5);
                     break;
                     case BT_UPDATE_ICON_STATE_UPDATE  :
                         setUpdateIcon(stt1[0]);
                         stt1[0]++;
                         stt1[0] &= 0x03;
                     break;
                 }
             }
         }, 100, 100);
     }
    private void setBtIcon(int icon){
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 switch (icon){
                     case C_.BT_ICON_DISABLE    : btSearch.setImageResource(R.drawable.bt_disable); break;
                     case C_.BT_ICON_ENABLE     : btSearch.setImageResource(R.drawable.bt_enable); break;
                     case C_.BT_ICON_CONNECTED  : btSearch.setImageResource(R.drawable.bt_connected); break;
                     case C_.BT_ICON_SCAN       : btSearch.setImageResource(R.drawable.bt_scan); break;
                 }
             }
         });
     }
    private void setUpdateIcon(int icon){
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 switch (icon){
                     case 0 : btUpdateDevList.setImageResource(R.drawable.update_arrow_0);      break;
                     case 1 : btUpdateDevList.setImageResource(R.drawable.update_arrow_45);     break;
                     case 2 : btUpdateDevList.setImageResource(R.drawable.update_arrow_90);     break;
                     case 3 : btUpdateDevList.setImageResource(R.drawable.update_arrow_135);    break;
                     case 4 : btUpdateDevList.setVisibility(GONE);                              break;
                     case 5 : btUpdateDevList.setVisibility(VISIBLE);                           break;

                 }
             }
         });
     }
    private void processingBtData(){
         Log.i("MY_TEG", G_.btData);
         Gson gson = new Gson();

         ObjectMsg msg = new ObjectMsg();

         try{
             msg = gson.fromJson(G_.btData, ObjectMsg.class);

         }catch (com.google.gson.JsonSyntaxException e){
             Log.e("MY_TEG", e.toString());
             return;
         }
         try {
             G_.btDevAddr = msg.sender;
         }catch (Exception e){
             Log.e("MY_TEG", e.toString());
             return;
         }
         G_.btData = "";
         G_.btPackQty = 0;
         G_.btDataOk = false;

         switch (msg.cmd){
             case C_.CMD_GET_JMMR_LIST:readJmmrList(msg);
         }
     }
    Timer btReceiveTm = null;
    private void readJmmrList(ObjectMsg msg){
        G_.jmmr_list = msg.jmmr_list;
        if(G_.jmmr_list == null){
            Log.i("MY_TEG", "G_.jmmr_list -> null 1");
            G_.btActiveState = BT_STATE_CONNECTED;
        return;
        }
        for(JmmrState jmmr : G_.jmmr_list){
            if(jmmr.pwr1 != 1)jmmr.pwr1 = 2;
            if(jmmr.pwr2 != 1)jmmr.pwr2 = 2;
        }
        G_.btHasNewData = true;
        G_.animeBtUpdateIconState = BT_UPDATE_ICON_STATE_VISIBLE;
     }
    void btReceivedStartPacket(String data){
        G_.btData = data;
        btReceiveTm = new Timer();
        btReceiveTm.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!G_.btDataOk){
                    Log.i("MY_TEG", "error btData");
                    Log.i("MY_TEG", G_.btData);
                    G_.btData = "";
                    G_.btPackQty = 0;
                }else {
                    processingBtData();
                }
            }
        }, 4000);
    }
    void btReceiveNextPackets(String data){
        G_.btData += data;
        if(G_.btData.contains("_stop")){
            int lastInd = G_.btData.lastIndexOf("}")+1;
            if(lastInd > 0){

                G_.btData =  G_.btData.substring(8, lastInd);
                G_.btDataOk = true;
                if(btReceiveTm != null){
                    btReceiveTm.cancel();
                    btReceiveTm = null;
                }
                processingBtData();
            }
        }
    }
    private void receiveBtData(String data){
         Log.i("MY_TEG", data);
         if(data.startsWith("start___"))btReceivedStartPacket(data);
         else                           btReceiveNextPackets(data);
     }
    void initPatternButtons(){
        bttnPatt1 = findViewById(R.id.sMainButtonPatt1);
        bttnPatt1.setOnTouchListener(mainOnTouchListener);
        bttnPatt1.setOnLongClickListener(mainOnLongClickListener);

        bttnPatt2 = findViewById(R.id.sMainButtonPatt2);
        bttnPatt2.setOnTouchListener(mainOnTouchListener);
        bttnPatt2.setOnLongClickListener(mainOnLongClickListener);

        bttnPatt3 = findViewById(R.id.sMainButtonPatt3);
        bttnPatt3.setOnTouchListener(mainOnTouchListener);
        bttnPatt3.setOnLongClickListener(mainOnLongClickListener);

        bttnPatt4 = findViewById(R.id.sMainButtonPatt4);
        bttnPatt4.setOnTouchListener(mainOnTouchListener);
        bttnPatt4.setOnLongClickListener(mainOnLongClickListener);
    }
    void initViewElements(){
        mainLV = findViewById(R.id.mainLV);

        bttnShowRangesList = findViewById(R.id.sMainButtonRanges);
        bttnShowRangesList.setOnTouchListener(mainOnTouchListener);
        sMainButtonSave = findViewById(R.id.sMainButtonSave);
        sMainButtonSave.setOnTouchListener(mainOnTouchListener);

        bttnSuppress = findViewById(R.id.sMainButtonSuppress);
        bttnSuppress.setOnTouchListener(mainOnTouchListener);


        initPatternButtons();

        btDevInfo = findViewById(R.id.btDevInfo);
        btDevInfo.setOnTouchListener(mainOnTouchListener);

        btUpdateDevList = findViewById(R.id.btUpdateDevList);
        btUpdateDevList.setOnTouchListener(mainOnTouchListener);

        btSearch = findViewById(R.id.btSearch);
        btSearch.setOnTouchListener(mainOnTouchListener);

        sMainPatternList      = findViewById(R.id.sMainPatternList);
        sMainPatternListPanel = findViewById(R.id.sMainPatternListPanel);

        sMainSubBackground = findViewById(R.id.sMainSubBackground);
        sMainSubBackground.setOnTouchListener(mainOnTouchListener);

        sMainButtonPatt1Txt = findViewById(R.id.sMainButtonPatt1Txt);
        sMainButtonPatt2Txt = findViewById(R.id.sMainButtonPatt2Txt);
        sMainButtonPatt3Txt = findViewById(R.id.sMainButtonPatt3Txt);
        sMainButtonPatt4Txt = findViewById(R.id.sMainButtonPatt4Txt);


    }
    void initJmmrListTmpVals(){

        for(int i=0; i<3; i++){
            JmmrState jmmrState = new JmmrState();
            jmmrState.ad_esp = i+1;
            jmmrState.dev_range = i*2+1;
            jmmrState.dev_type = 1;
            jmmrState.msk1 = (0xF << i);
            jmmrState.msk2 = (0xC << i*2);
            G_.jmmr_list.add(jmmrState);
        }
    }
    void initDevListAdapter(){
        Log.i("MY_TEG", "G_.jmmr_list.size -> "+G_.jmmr_list.size());

        if(devListAdapter != null)devListAdapter = null;
        devListAdapter = new DevListAdapter(this, R.layout.dev_list_item, G_.jmmr_list, new ItemDevSelListener() {
            @Override
            public void onItemDevSelClick(int pos) {
                G_.currentJmmrNum = pos;
                if(G_.jmmr_list != null) {
                    if (G_.jmmr_list.size() >= pos) {
                        G_.selectRange = G_.jmmr_list.get(pos).dev_range;
                    }
                }
                showPageNarrowband();
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainLV.setAdapter(devListAdapter);
            }
        });
    }
    void checkIntentForExtras(){
        Intent intent = getIntent();
        boolean cmd_suppress = intent.getBooleanExtra("cmd_suppress", false);
        boolean cmd_return = intent.getBooleanExtra("cmd_return", false);
        if(cmd_suppress)bttnSuppress();
//        if(!cmd_return)initDevListAdapter();


        intent.removeExtra("cmd_suppress");
        intent.removeExtra("cmd_return");

    }
    void init(){
        context = this;
        G_.animeBtConnectionIconState = BT_CONNECTING_ICON_STATE_ENABLE;
        setAnimateBtState();
        G_.currentJmmrNum = -1;
        context = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initViewElements();
        initRangesList();
        initRangesGroupList();
        animeBtStateIcon();
        initCbFunctions();
        initDevListAdapter();
        checkIntentForExtras();
        initTmMonitor();
        mPatternList = new Preferences(this).getPatternList();
        initPatterns();
    }
    void showPageRanges(){
        Intent i = new Intent(context, RangesActivity.class);
        startActivity(i);
    }
     void showPageNarrowband(){
         Intent i = new Intent(context, NarrowBandActivity.class);
         startActivity(i);
     }
    void bttnSuppress(){
        G_.selectBtDevice.setDeviceSelected(false);
        mNeedCloseConnection = 5;
        btSendJmmrList(true);
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
        bttnPatt1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_unpress, null));
        bttnPatt2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_unpress, null));
        bttnPatt3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_unpress, null));
        bttnPatt4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_unpress, null));

        if(mPatternActive == null) return;
        if(mPatternActive.isEmpty()) return;

        if(mPatternActive.get(0) == 1)bttnPatt1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_unpress_active, null));
        if(mPatternActive.get(1) == 1)bttnPatt2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_unpress_active, null));
        if(mPatternActive.get(2) == 1)bttnPatt3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_unpress_active, null));
        if(mPatternActive.get(3) == 1)bttnPatt4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_unpress_active, null));

    }
    void selectPattern(int bttnId, int color){
        if(bttnId > G_.pattern_select_list.size()-1)return;
        resetColorPatternButtons();
        int clr = color==1 ? R.drawable.button_active : R.drawable.button_unpress_active;
        switch (bttnId){
            case 0: bttnPatt1.setBackgroundResource (clr); break;
            case 1: bttnPatt2.setBackgroundResource (clr); break;
            case 2: bttnPatt3.setBackgroundResource (clr); break;
            case 3: bttnPatt4.setBackgroundResource (clr); break;
        }
    }
    void closeBtConnectionThread(){
        if(G_.btConnect!=null) {
            try {
                G_.currentJmmrNum = -1;
                G_.btConnect.connectThread.closeConnection();
            } catch (Exception e) {

            }
        }
    }
    void closeBtConnectionFull(){
        G_.btActiveState = C_.CB_CODE_DISCONNECT;
        G_.animeBtConnectionIconState = BT_CONNECTING_ICON_STATE_ENABLE;
        G_.animeBtUpdateIconState = BT_UPDATE_ICON_STATE_GONE;
        if(G_.btConnect!=null){
            try{
                G_.currentJmmrNum = -1;
                G_.btConnect.connectThread.closeConnection();

            }catch (Exception e){

            }
            try{
                G_.btConnect = null;
            }catch (Exception e){

            }
            reInitCbFunctions();
        }
     }
    private void showBtDevList(){
         closeBtConnectionFull();
         Intent i = new Intent(context, BtListActivity.class);
         startActivity(i);
     }

    void initRangesGroupList(){
         ObjRange o;
         o = new ObjRange( 400,  800);
         G_.rangeGroupList.add(o);
         o = new ObjRange( 750,  1050);
         G_.rangeGroupList.add(o);;
         o = new ObjRange(1000, 1500);
         G_.rangeGroupList.add(o);
         o = new ObjRange(1500, 1900);
         G_.rangeGroupList.add(o);
         o = new ObjRange(1900, 2300);
         G_.rangeGroupList.add(o);
         o = new ObjRange(2300, 2700);
         G_.rangeGroupList.add(o);
         o = new ObjRange(2700, 3100);
         G_.rangeGroupList.add(o);
         o = new ObjRange(3100, 3700);
         G_.rangeGroupList.add(o);
         o = new ObjRange(3700, 4400);
         G_.rangeGroupList.add(o);
         o = new ObjRange(4400, 5100);
         G_.rangeGroupList.add(o);
         o = new ObjRange(5100, 5700);
         G_.rangeGroupList.add(o);
         o = new ObjRange(5700, 6200);
         G_.rangeGroupList.add(o);

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
    void setPatternTitle(){
        if(G_.pattern_select_list == null)return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(G_.pattern_select_list.size()>0)sMainButtonPatt1Txt.setText(G_.pattern_select_list.get(0).patt_name);
                if(G_.pattern_select_list.size()>1)sMainButtonPatt2Txt.setText(G_.pattern_select_list.get(1).patt_name);
                if(G_.pattern_select_list.size()>2)sMainButtonPatt3Txt.setText(G_.pattern_select_list.get(2).patt_name);
                if(G_.pattern_select_list.size()>3)sMainButtonPatt4Txt.setText(G_.pattern_select_list.get(3).patt_name);
            }
        });
    }

    JmmrState getJmmrFromDb(int id){
        if(id == -1)return null;
        for(JmmrState jmmr : G_.pattern_list){
            if(jmmr.db_id == id)return jmmr;
        }
        return null;
    }

    void initPatternsPanel(){
        JmmrState pattern1, pattern2, pattern3, pattern4;
        resetColorPatternButtons();
        mPatternActive = new ArrayList<>();
        if((G_.jmmr_list != null)&&(G_.pattern_list != null)){
            pattern1 = getJmmrFromDb(mPatternList.get(0));
            pattern2 = getJmmrFromDb(mPatternList.get(1));
            pattern3 = getJmmrFromDb(mPatternList.get(2));
            pattern4 = getJmmrFromDb(mPatternList.get(3));
            for(int i=0; i<4; i++)mPatternActive.add(0);
            for(JmmrState jmmr1 : G_.jmmr_list){
                if(pattern1 != null)if(pattern1.dev_range == jmmr1.dev_range)mPatternActive.set(0, 1);
                if(pattern2 != null)if(pattern2.dev_range == jmmr1.dev_range)mPatternActive.set(1, 1);
                if(pattern3 != null)if(pattern3.dev_range == jmmr1.dev_range)mPatternActive.set(2, 1);
                if(pattern4 != null)if(pattern4.dev_range == jmmr1.dev_range)mPatternActive.set(3, 1);
                resetColorPatternButtons();
            }
        }
        setPatternTitle();
    }
    void initPatterns(){
        Db dbHelper = new Db(getApplicationContext(), "dbName", null, DB_VERSION);
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
    void setPatternBand(int patt){
        selectPattern(patt, 1);

    }

    void selectPattern(ObjectProcessingData o){
        JmmrState jmmr = (JmmrState)o.object;
        if(jmmr == null) return;
        Log.i("MY_TEG", "id -> "+jmmr.db_id);
        new Preferences(this).setPatternNum(mPatternSelected, jmmr.db_id);
    }
    void initPatternAdapter(){
        ListView listView = findViewById(R.id.sMainPatternList);
        listView.setVisibility(VISIBLE);
        if(patternAdapter != null)patternAdapter = null;
        patternAdapter = new PatternAdapter(this, R.layout.pattern_list_item, G_.pattern_list, new ItemPatternListener() {
            @Override
            public void cb(ObjectProcessingData o) {
                switch (o.cmd){
                    case CMD_SELECT_PATTERN     :selectPattern(o); break;
                    case CMD_UPDATE_PATTERN_LIST: initPatterns(); break;
                }
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(patternAdapter);
            }
        });

    }
    public void onClick(View v) {

        vibro();
        int vId = v.getId();
        if(vId == R.id.sMainButtonPatt1){setPatternBand(0);}// mAnime.onClick(this,v);
        if(vId == R.id.sMainButtonPatt2){setPatternBand(1);}// mAnime.onClick(this,v);
        if(vId == R.id.sMainButtonPatt3){setPatternBand(2);}// mAnime.onClick(this,v);
        if(vId == R.id.sMainButtonPatt4){setPatternBand(3);}// mAnime.onClick(this,v);
        if(vId == R.id.btUpdateDevList){
            if(mWaitBtresponse)return;
            if(G_.btActiveState == BT_STATE_CONNECTED) getJmmrList();
        }
        if(vId == R.id.btSearch)if(G_.btActiveState != BT_STATE_CONNECTED)showBtDevList();
        if(vId == R.id.sMainButtonSave){
            if(mTimeBlockButton > 0) return;
            mTimeBlockButton = 3;
            btSendJmmrList(false);  }
        if(vId == R.id.sMainButtonRanges){showPageRanges();}
        if(vId == R.id.sMainButtonSuppress){
            if(mTimeBlockButton > 0) return;
            mTimeBlockButton = 3;
            bttnSuppress();  }
    }
    boolean mOnLongToutch = false;
    void onPressPatternButton(int vId){
        if(mOnLongToutch){
            mOnLongToutch = false;
            return;
        }
        if(vId == R.id.sMainButtonPatt1){setPatternBand(0);}
        if(vId == R.id.sMainButtonPatt2){setPatternBand(1);}
        if(vId == R.id.sMainButtonPatt3){setPatternBand(2);}
        if(vId == R.id.sMainButtonPatt4){setPatternBand(3);}
    }
    void onPressButton(int vId){
        onPressPatternButton(vId);
        if(vId == R.id.btUpdateDevList){
            if(!mWaitBtresponse){
                if(G_.btActiveState == BT_STATE_CONNECTED) getJmmrList();
            }
        }
        if(vId == R.id.btSearch)if(G_.btActiveState != BT_STATE_CONNECTED)showBtDevList();
        if(vId == R.id.sMainButtonSave){
            if(mTimeBlockButton > 0) return;
            mTimeBlockButton = 3;
            btSendJmmrList(false);  }
        if(vId == R.id.sMainButtonRanges){showPageRanges();}
        if(vId == R.id.sMainButtonSuppress){
            if(mTimeBlockButton > 0) return;
            mTimeBlockButton = 3;
            bttnSuppress();  }
        if(vId == R.id.sMainSubBackground){
            sMainSubBackground.setVisibility(GONE);
            sMainPatternListPanel.setVisibility(GONE);
        }
        if(vId == R.id.sMessageModalWindowBttnConfirm){

        }

    }
    View.OnTouchListener mainOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int vId = v.getId();
            int color  = 0;
            boolean returnVal = true;
            if(vId == R.id.sMainButtonSuppress)color = 1;
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

    View.OnLongClickListener mainOnLongClickListener = new View.OnLongClickListener() {
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