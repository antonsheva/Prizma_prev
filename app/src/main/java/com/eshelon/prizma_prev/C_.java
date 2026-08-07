package com.eshelon.prizma_prev;

public class C_ {
    public static final String BASE_SRC_ID_NAME = "com.eshelon.prizma_prev:id/";
    public static final String SRC_ID_NAME_PATT_RANGE_BUTTON = "sRangesButtonPatt";
    public static final String SRC_ID_NAME_PATT_BAND_BUTTON = "sFrqBandButtonBand";
    public static final String SRC_ID_NAME_PATT_BAND_BUTTON_TXT = "sFrqBandTxt";
    public static final String SRC_ID_NAME_PATT_RANGE_PANEL = "sRangesPanelPatt";
    public static final String SRC_ID_NAME_PATT_SPECTER = "spectrum_";

    public static final int FRQ_STEP_QTY  = 32;


    public static final int CMD_RM_AT            = 1;
    public static final int CMD_RM_GET_ATBT      = 2;
    public static final int CMD_RM_GET_ATC       = 3;
    public static final int CMD_RM_SET_ATC       = 4;
    public static final int CMD_RM_SET_ATE0      = 5;
    public static final int CMD_RM_SET_ATE1      = 6;
    public static final int CMD_RM_GET_ATI       = 7;
    public static final int CMD_RM_ATZ           = 8;
    public static final int CMD_RM_GET_ATW       = 9;
    public static final int CMD_RM_SET_ATW       = 10;
    public static final int CMD_RM_GET_STATE     = 11;
    public static final int CMD_SET_JMMR_DATA     = 12;
    public static final int CMD_RM_GET_INFO      = 13;
    public static final int CMD_PRINT_ADDRESSES  = 14;
    public static final int CMD_GET_JMMR_DATA    = 15;
    public static final int CMD_SET_ADDR_RM_1    = 16;
    public static final int CMD_SET_ADDR_RM_2    = 17;
    public static final int CMD_RESPONSE_DATA    = 18;
    public static final int CMD_GET_JMMR_LIST    = 19;
    public static final int CMD_SEARCH_DEVICES   = 20;
    public static final int CMD_GET_STACK_SIZE   = 21;
    public static final int CMD_GEN_TEST_DATA    = 22;
    public static final int CMD_SET_JMMR_LIST    = 23;
    public static final int CMD_TEST             = 24;
//    public static final int CMD_SET_JMMR_DATA    = 25;
    public static final int CMD_SET_ADDR_ESP     = 26;
    public static final int CMD_SET_PWR          = 27;
    public static final int CMD_RESTART_ESP      = 28;
    public static final int CMD_SET_ADDR_RM      = 29;
    public static final int CMD_SET_DEV_ID       = 30;
    public static final int CMD_SET_DEV_TYPE     = 31;
    public static final int CMD_SET_GROUP_ID     = 32;
    public static final int CMD_SET_DEV_RANGE    = 33;
    public static final int CMD_GET_DEV_PARAM    = 34;
    public static final int CMD_GET_ALL_STACK    = 35;
    public static final int CMD_APLAY_PWR        = 36;
    public static final int CMD_UPDT_LOC_DATA    = 37;
    public static final int CMD_BT_START         = 38;
    public static final int CMD_BT_STOP          = 39;
    public static final int CMD_BT_SEND          = 40;
    public static final int CMD_BT_RECEIVE       = 41;

    public static final String PARAM_CMD        = "cmd";
    public static final String PARAM_MOD_CODE   = "mc";
    public static final String PARAM_MOD_CODE_1 = "mc1";
    public static final String PARAM_MOD_CODE_2 = "mc2";
    public static final String PARAM_MASK       = "msk";
    public static final String PARAM_MASK_1     = "msk1";
    public static final String PARAM_MASK_2     = "msk2";
    public static final String PARAM_RM_NUM     = "rm_num";
    public static final String PARAM_RM_STATE   = "rm_stt";
    public static final String PARAM_ADDRESSEE  = "addr";
    public static final String PARAM_ADDR_RM_1  = "ad_rm1";
    public static final String PARAM_ADDR_RM_2  = "ad_rm2";
    public static final String PARAM_NEED_BT_OFF = "need_bt_off";



    public static final int CB_CODE_CONNECT         = 1;
    public static final int CB_CODE_DISCONNECT      = 2;
    public static final int CB_CODE_NEW_DATA        = 3;

    public static final int CB_CODE_ERROR_CONNECT   = 3;

    public static final int BT_ACTIVE_STATE_DISABLE     = 0;
    public static final int BT_ACTIVE_STATE_ENABLE      = 1;
    public static final int BT_ACTIVE_STATE_SEARCHING   = 2;
    public static final int BT_ACTIVE_STATE_CONNECTING  = 3;
    public static final int BT_ACTIVE_STATE_CONNECTED   = 4;


    public static final int BT_ICON_DISABLE = 0;
    public static final int BT_ICON_ENABLE = 1;
    public static final int BT_ICON_CONNECTED = 2;
    public static final int BT_ICON_SCAN = 3;


}
