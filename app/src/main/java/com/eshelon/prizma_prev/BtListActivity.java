package com.eshelon.prizma_prev;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.widget.Toast.LENGTH_SHORT;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.eshelon.prizma_prev.adapter.BtAdapter;
import com.eshelon.prizma_prev.adapter.BtDevData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class BtListActivity extends AppCompatActivity implements ItemClickListener{


    boolean mPermScan   = false;
    boolean mPermConnect = false;
    boolean mBtIsEanbled = false;

    private BluetoothAdapter bluetoothAdapter;



    CB cb;
    Context cntxt;

    Timer timer;
    ImageView btSearch;



    List<BtDevData> btDevList = new ArrayList<BtDevData>();
    private BtAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_bt_list);
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
        IntentFilter f1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter f2 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(broadcastReceiverBltDevFound, f1);
        registerReceiver(broadcastReceiverBltDevFound, f2);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiverBltDevFound);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private final BroadcastReceiver broadcastReceiverBltDevFound = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(context, BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    getPermissionsBtConnect();
                    return;
                }
                if(device == null)return;
                BtDevData devData = new BtDevData();
                devData.setMac(device.getAddress());
                devData.setNum(G_.btDevCnt++);

                //// TODO: 22.06.2026

                devData.setName(device.getName());
                if(devData.getName() == null)return;
                if(!devData.getName().isEmpty()){
                    if(devData.getName().startsWith("Prizma_JMR")){
                        G_.devList.add(devData);
                        btAdapter.notifyDataSetChanged();
                    }
                }

                //end  TODO: 22.06.2026

            }
        }
    } ;

    void startJmmrsSearch(){
        G_.selectBtDevice.setDeviceSelected(false);
        if(mBtIsEanbled){
            if (ActivityCompat.checkSelfPermission(this, BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                getPermissionsBtConnect();
                Log.i("MY_TEG", "getPermissionsBtConnect error ------");
                return;
            }
            if(bluetoothAdapter.isDiscovering()){
                Log.i("MY_TEG", "bluetoothAdapter.isDiscovering ------");
                return;
            }

            G_.devList.clear();

            bluetoothAdapter.startDiscovery();
            final boolean[] stt = {false};
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(stt[0])setBtIcon(C_.BT_ICON_ENABLE);
                            else      setBtIcon(C_.BT_ICON_CONNECTED);
                            stt[0] = !stt[0];
//                            Log.i("MY_TEG", "timer 1");
                        }
                    });
//                    Log.i("MY_TEG", "timer  2");
                    if (ActivityCompat.checkSelfPermission(cntxt, BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        getPermissionsBtConnect();
                        return;
                    }
                    if(!(bluetoothAdapter.isDiscovering())){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setBtIcon(C_.BT_ICON_ENABLE);
                            }
                        });
                        timer.cancel();
                    }

                }
            }, 300, 300);

        }else {
            Log.i("MY_TEG", "getPermissionsBtConnect   ------");
            int perm = PERMISSION_GRANTED;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perm = ContextCompat.checkSelfPermission(this, BLUETOOTH_CONNECT);
            }
            if (perm != PERMISSION_GRANTED)return;
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBltActivityResult.launch(i);
        }

    }

    void getPairedDevices(){
        int cnt = 0;

        if (ActivityCompat.checkSelfPermission(this, BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        StringBuilder string = new StringBuilder();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(G_.devList == null)G_.devList = new ArrayList<BtDevData>();
        else                  G_.devList.clear();
        if(!pairedDevices.isEmpty()){
            for (BluetoothDevice device : pairedDevices){
                if(device.getName().startsWith("Prizma_JMR")){
                    BtDevData devData = new BtDevData();
                    devData.setName(device.getName());
                    devData.setMac(device.getAddress());
                    devData.setNum(cnt++);
                    G_.devList.add(devData);
                }
            }
            btAdapter.notifyDataSetChanged();
        }
    }

    ActivityResultLauncher<Intent> enableBltActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode()==RESULT_OK){
                        mBtIsEanbled = true;
                        setBtIcon(C_.BT_ICON_ENABLE);
                    }
                }
            }
    );



    private boolean initBt(){
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mPermConnect = (ActivityCompat.checkSelfPermission(this, BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mPermScan = (ActivityCompat.checkSelfPermission(this, BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED);
        }

        if(!mPermConnect || !mPermScan){
            getPermissionsBtConnect();
            return false;
        }
        return true;
    }

    private void init(){
        cntxt = this;
        timer = new Timer();
        if(G_.devList != null) G_.devList.clear();
        if(!initBt()){
            Log.i("MY_TEG", "Error BT init");
            return;
        }
        Log.i("MY_TEG", "BT init Ok !!!");

        btSearch = findViewById(R.id.btSearch);
        ListView listView = findViewById(R.id.btDevListView);

        btAdapter = new BtAdapter(this, R.layout.bt_list_item, G_.devList, new ItemClickListener() {
              @Override
              public void onItemClick(BtDevData data) {
                finish();
              }
          });


        listView.setAdapter(btAdapter);

        if(bluetoothAdapter.isEnabled()){
            setBtIcon(C_.BT_ICON_ENABLE);
            mBtIsEanbled = true;
        }else{
            setBtIcon(C_.BT_ICON_DISABLE);
            mBtIsEanbled = false;
        }
        startJmmrsSearch();
//        getPairedDevices();
    }

    void setBtIcon(int icon){
        switch (icon){
            case C_.BT_ICON_DISABLE    : btSearch.setImageResource(R.drawable.bt_disable); break;
            case C_.BT_ICON_ENABLE     : btSearch.setImageResource(R.drawable.bt_enable); break;
            case C_.BT_ICON_CONNECTED  : btSearch.setImageResource(R.drawable.bt_connected); break;
            case C_.BT_ICON_SCAN       : btSearch.setImageResource(R.drawable.bt_scan); break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if(requestCode == 120){
            if(grantResults.length==0)return;
            if(grantResults[0] == PERMISSION_GRANTED){
                mPermConnect = true;
            }else {
                Toast.makeText(this, "Need permission   BLUETOOTH_CONNECT", LENGTH_SHORT).show();
            }
        }
    }

    void getPermissionsBtConnect(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(new String[]{BLUETOOTH_CONNECT, BLUETOOTH_SCAN, ACCESS_FINE_LOCATION}, 120);
        }
    }

    @Override
    public void onItemClick(BtDevData data) {

    }


    public interface CB{
        void cb(int code);
    }
}