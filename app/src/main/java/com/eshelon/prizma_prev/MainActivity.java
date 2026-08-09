 package com.eshelon.prizma_prev;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
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


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eshelon.prizma_prev.adapter.DevListAdapter;
import com.eshelon.prizma_prev.interfaces.CB;
import com.eshelon.prizma_prev.interfaces.ItemDevSelListener;

import com.eshelon.prizma_prev.objects.JmmrState;
import com.eshelon.prizma_prev.objects.ObjRange;
import com.eshelon.prizma_prev.objects.ObjectMsg;
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

    ImageView btDevInfo;
    ImageView btUpdateDevList;
    ImageView btSearch;
    ListView mainLV;
    DevListAdapter devListAdapter;
    Vibrator vibrator;
    Context context;
    Timer animeTmBtSign = new Timer();
    boolean tryToConnect = false;
    int mAnimeBtConnectionIconState = 0;
    int mAnimeBtUpdateIconState     = 0;
    AnimeViewElements mAnime = new AnimeViewElements();

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
     protected void onStart() {
         super.onStart();
     }

     void setAnimateBtState(){
        switch (G_.btActiveState ){
            case BT_STATE_DISCONNECTED:
                mAnimeBtConnectionIconState = BT_CONNECTING_ICON_STATE_ENABLE;
                mAnimeBtUpdateIconState = BT_UPDATE_ICON_STATE_GONE;
                break;
            case BT_STATE_CONNECTED :
                mAnimeBtConnectionIconState = BT_CONNECTING_ICON_STATE_CONNECTED;
                mAnimeBtUpdateIconState = BT_UPDATE_ICON_STATE_VISIBLE;
                break;
        }
     }
     CB onConnectCb = new CB() {
         @Override
         public void cb(int code) {
             Log.i("MY_TEG", "onConnectCb code -> "+code+" ");
             ReceiveThread rThrd = G_.btConnect.connectThread.getReceiveThread();
             if(code == C_.CB_CODE_ERROR_CONNECT){
                 if(rThrd == null){
                     G_.btActiveState = BT_STATE_DISCONNECTED;
                     mAnimeBtConnectionIconState = BT_CONNECTING_ICON_STATE_SEARCHING;
                     mAnimeBtUpdateIconState = BT_UPDATE_ICON_STATE_GONE;
                     Log.i("MY_TEG", " -- - tryRecoveryConnection  - onConnectCb--------1");
                     tryRecoveryConnection();
                     return;
                 }
             }
             if(code==C_.CB_CODE_CONNECT){
                 G_.btActiveState = BT_STATE_CONNECTED;
                 mAnimeBtConnectionIconState = BT_CONNECTING_ICON_STATE_CONNECTED;
                 mAnimeBtUpdateIconState = BT_UPDATE_ICON_STATE_VISIBLE;
                 Log.i("MY_TEG", " -- -  CB_CODE_CONNECT  - onConnectCb--------1");
                 if(rThrd == null){
                     tryRecoveryConnection();
                     return;
                 }
                 rThrd.setCbReceive(cbBtReceive);
                 getJmmrList();

             }
         }
     };

     void getJmmrList(){
         if(G_.jmmr_list != null){
             G_.jmmr_list.clear();
             Log.i("MY_TEG", "G_.jmmr_list -> clear");
         }else{
             Log.i("MY_TEG", "G_.jmmr_list -> null");
             G_.jmmr_list =new ArrayList<>();
         }
         mAnimeBtUpdateIconState = BT_UPDATE_ICON_STATE_UPDATE;
         btSendCmd(C_.CMD_GET_JMMR_LIST);
     }
     void btConnect(){
         mAnimeBtConnectionIconState = BT_CONNECTING_ICON_STATE_SEARCHING;
         G_.btConnect.connect();
     }
     @Override
     protected void onResume() {
         super.onResume();
         setBtIcon(C_.BT_ICON_ENABLE);
         if (!G_.selectBtDevice.isDeviceSelected())return;
         if(G_.btActiveState == BT_STATE_CONNECTED)return;

         Log.i("MY_TEG", "onResume - - MainActivity");
         G_.btActiveState = BT_STATE_CONNECTING;
         G_.btConnect = new BtConnect(this, G_.selectBtDevice.getMac(), onConnectCb);
         btConnect();
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
         if(G_.btActiveState != BT_STATE_CONNECTED)return;
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

                     /**
                      * TODO
                      */
//                     runOnUiThread(new Runnable() {
//                         @Override
//                         public void run() {
//                             btnLoad.setEnabled(true);
//                         }
//                     });

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
     void tryRecoveryConnection(){
         btConnect();
     }
     CbBtReceive cbBtReceive = new CbBtReceive() {
         @Override
         public void cb(int code, String data) {
             Log.i("MY_TEG", "---- BT DATA  - --------");
             switch (code){
                 case C_.CB_CODE_NEW_DATA   : receiveBtData(data);                           break;
                 case C_.CB_CODE_DISCONNECT :
                     Log.i("MY_TEG", "---- BT DISCONNECT  - --------");
                     G_.btActiveState = C_.CB_CODE_DISCONNECT;
                     mAnimeBtConnectionIconState = BT_CONNECTING_ICON_STATE_SEARCHING;
                     mAnimeBtUpdateIconState = BT_UPDATE_ICON_STATE_GONE;
                     Log.i("MY_TEG", " -- - tryRecoveryConnection  - cbBtReceive--------2");
                     tryRecoveryConnection();
                 break;
             }
         }
     };
     private void showToast(int toastId){
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 Toast.makeText(context, toastId, Toast.LENGTH_LONG).show();
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
                 switch (mAnimeBtConnectionIconState){

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

                 switch (mAnimeBtUpdateIconState){
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
        viewUpdateDevList();
        mAnimeBtUpdateIconState = BT_UPDATE_ICON_STATE_VISIBLE;
     }
    private void viewUpdateDevList(){
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 initDevListAdapter();
             }
         });
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
    void initViewElements(){
        mainLV = findViewById(R.id.mainLV);

        bttnShowRangesList = findViewById(R.id.sMainButtonRanges);
        bttnShowRangesList.setOnClickListener(this);
        sMainButtonSave = findViewById(R.id.sMainButtonSave);
        sMainButtonSave.setOnClickListener(this);

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

        btDevInfo = findViewById(R.id.btDevInfo);
        btDevInfo.setOnClickListener(this);

        btUpdateDevList = findViewById(R.id.btUpdateDevList);
        btUpdateDevList.setOnClickListener(this);

        btSearch = findViewById(R.id.btSearch);
        btSearch.setOnClickListener(this);
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
        if(devListAdapter != null)devListAdapter = null;
        devListAdapter = new DevListAdapter(this, R.layout.dev_list_item, G_.jmmr_list, new ItemDevSelListener() {
            @Override
            public void onItemDevSelClick(int pos) {
                G_.currentJmmrNum = pos;
                if(G_.jmmr_list != null){
                    if(G_.jmmr_list.size() >= pos){
                        G_.selectRange = G_.jmmr_list.get(pos).dev_range;
                    }
                }
                showPageNarrowband();
            }
        });
        mainLV.setAdapter(devListAdapter);
    }

    void checkIntentForExtras(){
        Intent intent = getIntent();
        boolean cmd_suppress = intent.getBooleanExtra("cmd_suppress", false);
        if(cmd_suppress)bttnSuppress();
        intent.removeExtra("cmd_suppress");
    }
    void init(){
        mAnimeBtConnectionIconState = BT_CONNECTING_ICON_STATE_ENABLE;
        setAnimateBtState();
        G_.currentJmmrNum = -1;
        context = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initViewElements();
        initRangesList();
        initRangesGroupList();
        animeBtStateIcon();
        initDevListAdapter();
        checkIntentForExtras();
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
        bttnPatt1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));
        bttnPatt2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));
        bttnPatt3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));
        bttnPatt4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pattern, null));

    }
    void selectPattern(int bttnId){
        G_.selectPattern = bttnId;
        resetColorPatternButtons();
        switch (bttnId){
            case 1: bttnPatt1.setBackgroundResource (R.drawable.button_pattern_select); break;
            case 2: bttnPatt2.setBackgroundResource (R.drawable.button_pattern_select); break;
            case 3: bttnPatt3.setBackgroundResource (R.drawable.button_pattern_select); break;
            case 4: bttnPatt4.setBackgroundResource (R.drawable.button_pattern_select); break;
        }
    }
     private void showBtDevList(){
         if(G_.btConnect!=null){
             try{
                 G_.currentJmmrNum = -1;
                 G_.btConnect.connectThread.closeConnection();
             }catch (Exception e){

             }
         }
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


    Timer tmButtonClick = new Timer();
    static boolean swchOnClick = false;

    public void onClick(View v) {
        boolean anime = false;
        vibro();

        int vId = v.getId();
        if(vId == R.id.sMainButtonPatt1){selectPattern(1); mAnime.onClick(this,v); }
        if(vId == R.id.sMainButtonPatt2){selectPattern(2); mAnime.onClick(this,v); }
        if(vId == R.id.sMainButtonPatt3){selectPattern(3); mAnime.onClick(this,v); }
        if(vId == R.id.sMainButtonPatt4){selectPattern(4); mAnime.onClick(this,v); }


        if(vId == R.id.btUpdateDevList)if(G_.btActiveState == BT_STATE_CONNECTED) getJmmrList();
        if(vId == R.id.btSearch)if(G_.btActiveState != BT_STATE_CONNECTED)showBtDevList();

        if(vId == R.id.sMainButtonSave){btSendJmmrList(false);  mAnime.onClick(this,v);}
        if(vId == R.id.sMainButtonRanges){showPageRanges();  mAnime.onClick(this,v);}
        if(vId == R.id.sMainButtonSuppress){bttnSuppress();  mAnime.onClick(this,v);}


    }
}