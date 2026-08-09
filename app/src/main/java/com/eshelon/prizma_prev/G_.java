package com.eshelon.prizma_prev;

import com.eshelon.prizma_prev.interfaces.CB;
import com.eshelon.prizma_prev.objects.BtDevData;
import com.eshelon.prizma_prev.objects.ObjRange;
import com.eshelon.prizma_prev.objects.JmmrState;

import java.util.ArrayList;

public class G_ {
    public static boolean bttnSuppressState = false;
    public static int selectPattern = 0;
    public static int selectRange = 0;
    public static final ArrayList<ObjRange> rangeList = new ArrayList<>();
    public static final ArrayList<ObjRange> rangeGroupList = new ArrayList<>();


    public static ArrayList<JmmrState> jmmr_list = new ArrayList<>();
    public static String  btData = "";
    public static int btPackQty = 0;
    public static boolean btDataOk = false;
    public static int btDevAddr = 0;
    public static int btActiveState = 0;



    public static ArrayList<BtDevData> devList = new ArrayList<>();
    public static BtDevData selectBtDevice = new BtDevData();

    public static String btReceiveData = "";

    public static int msgId =0;

    /**
     * quantity of connected bt devices
     */
    public static int btDevCnt = 0;


    /**
     * jmmr number in jmmr list
     */
    public static int  currentJmmrNum = -1;

    public static JmmrState currentJmmr = new JmmrState();

    public static boolean btHasNewData = false;
    public static BtConnect btConnect;
    public static int animeBtConnectionIconState = 0;
    public static int animeBtUpdateIconState = 0;
    public static boolean btWaitOnConnect = false;

    public static void init(){
//        jmmr_list = new ArrayList<>();
//        btDevCnt = 0;
//        devList = new ArrayList<BtDevData>();
//        selectBtDevice = new BtDevData();
//        msgId = 0;
//        currentJmmr = new JmmrState();
    }
}
