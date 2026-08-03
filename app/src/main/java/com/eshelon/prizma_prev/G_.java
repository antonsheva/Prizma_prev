package com.eshelon.prizma_prev;

import com.eshelon.prizma_prev.adapter.BtDevData;
import com.eshelon.prizma_prev.objects.ObjRange;
import com.eshelon.prizma_prev.objects.JmmrState;

import java.util.ArrayList;

public class G_ {
    public static boolean bttnSuppressState = false;
    public static int selectPattern = 0;
    public static int selectRange = 0;
    public static final ArrayList<ObjRange> rangeList = new ArrayList<>();


    public static ArrayList<JmmrState> jmmr_list;
    public static String  btData = "";
    public static int btPackQty = 0;
    public static boolean btDataOk = false;
    public static int btDevAddr = 0;
    public static int btActiveState = 0;



    public static ArrayList<BtDevData> devList;
    public static BtDevData selectBtDevice;

    public static String btReceiveData = "";

    public static int msgId;

    /**
     * quantity of connected bt devices
     */
    public static int btDevCnt;


    /**
     * jmmr number in jmmr list
     */
    public static int  currentJmmrNum = 0;

    public static JmmrState currentJmmr;




    public static void init(){
        jmmr_list = new ArrayList<>();
        btDevCnt = 0;
        devList = new ArrayList<BtDevData>();
        selectBtDevice = new BtDevData();
        msgId = 0;
        currentJmmr = new JmmrState();
    }
}
