 package com.eshelon.prizma_prev;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.eshelon.prizma_prev.C_.BT_ACTIVE_STATE_CONNECTED;
import static com.eshelon.prizma_prev.C_.BT_ACTIVE_STATE_CONNECTING;
import static com.eshelon.prizma_prev.C_.BT_ACTIVE_STATE_ENABLE;
import static com.eshelon.prizma_prev.C_.BT_ACTIVE_STATE_SEARCHING;


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
import com.eshelon.prizma_prev.interfaces.ItemDevSelListener;

import com.eshelon.prizma_prev.objects.JmmrState;
import com.eshelon.prizma_prev.objects.ObjRange;
import com.eshelon.prizma_prev.objects.ObjectMsg;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

 public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout bttnShowRangesList;
    LinearLayout bttnSuppress;
    RelativeLayout bttnPatt1;
    RelativeLayout bttnPatt2;
    RelativeLayout bttnPatt3;
    RelativeLayout bttnPatt4;



     ImageView btDevInfo;
     ImageView btDevList;
     ImageView btSearch;
     ListView mainLV;

     DevListAdapter devListAdapter;
     BtConnect btConnect = null;

    Vibrator vibrator;
    Context context;
    Timer animeTmBtSign = new Timer();
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
     protected void onStart() {
         super.onStart();
     }

     @Override
     protected void onResume() {
         super.onResume();
         setBtIcon(C_.BT_ICON_ENABLE);
         if (!G_.selectBtDevice.isDeviceSelected())return;
         if(G_.btActiveState == BT_ACTIVE_STATE_CONNECTED)return;

         G_.btActiveState = BT_ACTIVE_STATE_CONNECTING;
         btConnect = new BtConnect(this, G_.selectBtDevice.getMac(), code -> {
             ReceiveThread rThrd = btConnect.connectThread.getReceiveThread();
             if(rThrd == null){
                 showToast(R.string.error_bt_connect);
                 Log.i("MY_TEG", "Error getReceiveThread");
                 G_.btActiveState = BT_ACTIVE_STATE_ENABLE;
                 return;
             }
             if(code==C_.CB_CODE_CONNECT){
                 G_.btActiveState = BT_ACTIVE_STATE_CONNECTED;

             }
             else{
                 G_.btActiveState = BT_ACTIVE_STATE_ENABLE;

             }
             rThrd.setCbReceive(cbBtReceive);
             if(G_.jmmr_list != null){
                 G_.jmmr_list.clear();
                 Log.i("MY_TEG", "G_.jmmr_list -> clear");
             }else{
                 Log.i("MY_TEG", "G_.jmmr_list -> null");
             }
             btSendCmd(C_.CMD_GET_JMMR_LIST);
             setVisibleMenuJmmrList();

         });
         btConnect.connect();
     }
     private void btSendJmmrList(){
         if(G_.jmmr_list == null)return;
         ObjectMsg msg = new ObjectMsg();
         msg.cmd = C_.CMD_SET_JMMR_LIST;

         msg.jmmr_list = G_.jmmr_list;
         msg.jmmr_list_len = G_.jmmr_list.size();
         btSendData(msg, 0);
     }
     private void btSendData(Object o, int type){
         if(G_.btActiveState != BT_ACTIVE_STATE_CONNECTED)return;
         String jsonStr = "";
         ObjectMsg msg = (ObjectMsg)o;
         msg.ad_esp = G_.btDevAddr;
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
                 btConnect.connectThread.getReceiveThread().sendData(sendBuff);
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
         btConnect.connectThread.getReceiveThread().sendData(data);
     }
     CbBtReceive cbBtReceive = new CbBtReceive() {
         @Override
         public void cb(int code, String data) {
             Log.i("MY_TEG", "---- BT DATA  - --------");
             if(G_.jmmr_list == null){
                 Log.i("MY_TEG", "G_.jmmr_list -> null");
             }
             switch (code){
                 case C_.CB_CODE_NEW_DATA   : receiveBtData(data);                           break;
//                case CB_CODE_DISCONNECT : G_.btActiveState = BT_ACTIVE_STATE_ENABLE;
//                    Log.i("MY_TEG", "---- BT DISCONNECT  - --------");                break;
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
         animeTmBtSign.schedule(new TimerTask() {
             @Override
             public void run() {

                 switch (G_.btActiveState){
                     case BT_ACTIVE_STATE_CONNECTING :
                     case BT_ACTIVE_STATE_SEARCHING  :
                         setVisibleBtMenuInfo(GONE);
                         if(stt[0])setBtIcon(C_.BT_ICON_ENABLE);
                         else      setBtIcon(C_.BT_ICON_CONNECTED);
                         stt[0] = !stt[0];
                         break;
                     case BT_ACTIVE_STATE_CONNECTED  :
                         setBtIcon(C_.BT_ICON_CONNECTED);
                         break;
                     case BT_ACTIVE_STATE_ENABLE     :
                         setBtIcon(C_.BT_ICON_ENABLE);
                         if(G_.jmmr_list != null){
                             Log.i("MY_TEG", "G_.jmmr_list -> clear 11111");
                             G_.jmmr_list.clear();
                         }
                         setVisibleMenuJmmrList();
                         setVisibleBtMenuInfo(GONE);
                         break;
                 }
             }
         }, 300, 300);
     }
     private void setVisibleBtMenuInfo(int visible){
         runOnUiThread(new Runnable() {
             @Override
             public void run() {

                 btDevInfo.setVisibility(visible);
             }
         });
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
     private void setVisibleMenuJmmrList(){
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 int v = GONE;
                 if(G_.jmmr_list != null)v = G_.jmmr_list.isEmpty() ? GONE  : VISIBLE  ;
                 btDevList.setVisibility(v);
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
             return;
         }
         for(JmmrState jmmr : G_.jmmr_list){
             if(jmmr.pwr1 != 1)jmmr.pwr1 = 2;
             if(jmmr.pwr2 != 1)jmmr.pwr2 = 2;
         }
        viewUpdateDevList();
     }
    private void viewUpdateDevList(){
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 initDevListAdapter();
             }
         });
    }
    private void receiveBtData(String data){
         Log.i("MY_TEG", data);
         if(data.startsWith("start___")){
             G_.btData = "";
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
         G_.btData += data;
         if(G_.btData.contains("_stop")){
             int lastInd = G_.btData.lastIndexOf("}")+1;
             if(lastInd > 0){
                 String tmpStr = G_.btData.substring(8, lastInd);
                 G_.btData = tmpStr;
                 G_.btDataOk = true;
                 if(btReceiveTm != null){
                     btReceiveTm.cancel();
                     btReceiveTm = null;
                 }
                 processingBtData();
             }
         }
     }
    void initViewElements(){
        mainLV = findViewById(R.id.mainLV);

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

        btDevInfo = findViewById(R.id.btDevInfo);
        btDevInfo.setOnClickListener(this);

        btDevList = findViewById(R.id.btDevList);
        btDevList.setOnClickListener(this);

        btSearch = findViewById(R.id.btSearch);
        btSearch.setOnClickListener(this);
    }

    void initDevListAdapter(){

//
//        for(int i=0; i<3; i++){
//            JmmrState jmmrState = new JmmrState();
//            jmmrState.ad_esp = i+1;
//            jmmrState.dev_range = i*2+1;
//            jmmrState.dev_type = 1;
//            jmmrState.msk1 = (0xF << i);
//            jmmrState.msk2 = (0xC << i*2);
//            G_.jmmr_list.add(jmmrState);
//        }

        if(devListAdapter != null)devListAdapter = null;
        devListAdapter = new DevListAdapter(this, R.layout.dev_list_item, G_.jmmr_list, new ItemDevSelListener() {
            @Override
            public void onItemDevSelClick(int pos) {
                G_.currentJmmrNum = pos;
                G_.selectRange = G_.jmmr_list.get(pos).dev_range;
                int mask = G_.jmmr_list.get(pos).msk1;

                showPageNarrowband();
            }
        });
        mainLV.setAdapter(devListAdapter);
    }
    void init(){
        Bundle arguments = getIntent().getExtras();
        if(arguments != null){
            Log.i("MY_TEG", "there is arguments ");
            if(arguments.getBoolean("needUpdateBandView")){
                Log.i("MY_TEG", "--  needUpdateBandView--   ");
            }
        }else {
            G_.init();
            Log.i("MY_TEG", "arguments ");
        }


        G_.currentJmmrNum = -1;
        context = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initViewElements();
        initRangesList();
        initRangesGroupList();
        animeBtStateIcon();
        initDevListAdapter();
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
        if(G_.bttnSuppressState){
            G_.bttnSuppressState = false;
            bttnSuppress.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_suppress_off, null));
        }else {
            G_.bttnSuppressState = true;
            bttnSuppress.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_suppress_on, null));
            btSendJmmrList();
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
     private void showBtDevList(){
         if(btConnect!=null){
             try{
                 G_.currentJmmrNum = -1;
                 btConnect.connectThread.closeConnection();
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



    public void onClick(View v) {
        vibro();

        int vId = v.getId();
        if(vId == R.id.sMainButtonRanges)showPageRanges();
        if(vId == R.id.sMainButtonSuppress)bttnSuppress();
        if(vId == R.id.sMainButtonPatt1)selectPattern(1);
        if(vId == R.id.sMainButtonPatt2)selectPattern(2);
        if(vId == R.id.sMainButtonPatt3)selectPattern(3);
        if(vId == R.id.sMainButtonPatt4)selectPattern(4);

//        if(vId == R.id.btDevInfo)selectPattern(4);
        if(vId == R.id.btDevList){
//            G_.btActiveState = BT_ACTIVE_STATE_SEARCHING;
//            animeBtStateIcon();
        }
        if(vId == R.id.btSearch)showBtDevList();

    }
}