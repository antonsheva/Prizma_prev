package com.eshelon.prizma_prev.objects;

import android.util.Log;

import com.eshelon.prizma_prev.C_;

import java.util.ArrayList;

public class ObjRange {
    private static int cnt = 0;
    Integer start, stop;
    Float rangeWidth;

    Integer num;
    Float frqStep = 0f;
    Integer currentBand = 0;
    Float currentBandStart = 0f;
    Float currentBandStop = 0f;
    Integer currentBandWidth = 0;
    Integer frqPosition = 0;
    Float frqCenter = 0f;
    Long rangeMask = 0L;
    int seekBarPosition = 0;
    public int getOffMaskLeft() {
        return offMaskLeft;
    }
    public int getOffMaskRight() {
        return offMaskRight;
    }
    int offMaskLeft = 0;
    int offMaskRight = 0;

    public int getOnMaskLength() {
        return onMaskLength;
    }

    int onMaskLength = C_.FRQ_STEP_QTY;


    public Integer getOffMask() {
        return offMask;
    }

    Integer offMask = 0;


    Integer modCode = 0;

    public Integer getModCode() {
        return modCode;
    }

    public void setModCode(Integer modCode) {
        this.modCode = modCode;
    }

    public Long getRangeMask() {
        return rangeMask;
    }

    public void setRangeMask(Long rangeMask) {
        if(rangeMask > 0x7FFFFFFFL)rangeMask &=0x7FFFFFFFL;
        rangeMask &= offMask;
        this.rangeMask = rangeMask;
        Log.i("MY_TEG", "setRangeMask      -> "+this.rangeMask);
    }

    ArrayList<Integer>bandList = new ArrayList<>();
    ArrayList<Integer>bandStartList = new ArrayList<>();
    ArrayList<Integer>bandStopList = new ArrayList<>();
    ArrayList<String> viewBandList = new ArrayList<>();

    String viewRange ="";
    String viewBand ="";


    String viewBandWidth ="";


    public String getViewBandWidth() {
        return viewBandWidth;
    }

    public static int getCnt() {
        return cnt;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getStop() {
        return stop;
    }

    public Float getRangeWidth() {
        return rangeWidth;
    }
    
//TODO ----  ?????   ------
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
//--------------
    
    
    public Float getFrqStep() {
        return frqStep;
    }

    public Integer getCurrentBand() {
        return currentBand;
    }
    
    public Float getCurrentBandStart() {
        return currentBandStart;
    }

    public Float getCurrentBandStop() {
        return currentBandStop;
    }

    public Integer getCurrentBandWidth() {
        return currentBandWidth;
    }

    public ArrayList<Integer> getBandList() {
        return bandList;
    }

    public ArrayList<Integer> getBandStartList() {
        return bandStartList;
    }
    public ArrayList<Integer> getBandStopList() {
        return bandStopList;
    }
    public Float getFrqCenter() {
        return frqCenter;
    }
    public String getViewRange() {
        return viewRange;
    }
    public String getViewBand() {
        return viewBand;
    }

    public Integer getFrqPosition() {
        return frqPosition;
    }

    public void setCurrentBand(Integer currentBand) {
        this.currentBand = currentBand;
        applyNewParameters();
    }

    public int getSeekBarPosition() {
        return seekBarPosition;
    }


    public void setFrqPosition(Integer frqPosition) {
        boolean canChange = true;
        seekBarPosition = frqPosition;
        frqPosition+=offMaskRight;//
        Log.i("MY_TEG","pos -> "+frqPosition);
        if(seekBarPosition + currentBandStickQty/2 >= onMaskLength+1){
            canChange = false;
        }
        if(seekBarPosition-currentBandStickQty/2 < 0){
            canChange = false;
        }
        if(canChange){
            this.frqPosition = frqPosition;
            applyNewParameters();
        }
    }
    ArrayList<String>viewBandStepList = new ArrayList<>();

    public ArrayList<String> getViewBandStepList() {
        return viewBandStepList;
    }

    Integer currentBandStickQty = 0;
    ArrayList<Integer> bandStickQtyList = new ArrayList<>();
    void applyNewParameters(){
        currentBandWidth = bandList.get(currentBand);
        frqCenter = frqStep * seekBarPosition +start;//       +currentBandWidth/2
        currentBandStart =  frqCenter - currentBandWidth/2;
        currentBandStop  = currentBandWidth+currentBandStart;
        if(currentBandStop > stop)currentBandStop = (float)stop;
        if(frqPosition+currentBandStickQty/2>C_.FRQ_STEP_QTY-1)currentBandStop =  (float)stop;
        viewBand = "  "+currentBandWidth+" МГц";

        viewBandWidth = Math.round(currentBandStart) + " - " + Math.round(frqCenter) +" - "+ Math.round(currentBandStop);

        currentBandStickQty = bandStickQtyList.get(currentBand);
        rangeMask = 0L;
        int tmp;
        for(int i=0; i<currentBandStickQty; i++){
            tmp = frqPosition - currentBandStickQty/2+i;
            if(tmp>C_.FRQ_STEP_QTY)tmp = C_.FRQ_STEP_QTY; if(tmp<0)tmp = 0;
            rangeMask |= (1<<tmp);
        }
        if(rangeMask > 0x7FFFFFFFL)rangeMask &= 0x7FFFFFFFL;
    }

    public ArrayList<String> getViewBandList() {
        return viewBandList;
    }



    public void setOffMask(Integer offMask) {
        offMaskLeft = 0;
        offMaskRight = 0;
        this.offMask = ~offMask;
        for(int i=0; i<15; i++){
            if((0x40000000 & (offMask<<i))!=0)offMaskLeft++;
            else break;;
        }

        for(int i=0; i<15; i++){
            if((0x1 & (offMask>>i))!=0)offMaskRight++;
            else break;
        }
        onMaskLength = 0;
        for(int i=0; i<31; i++){
            if(((this.offMask << i) & 0x40000000) != 0) onMaskLength++;
        }
        initFrqStep();
        seekBarPosition = onMaskLength/2;
    }

    private void initFrqStep(){
        String str;
        frqStep = rangeWidth / onMaskLength;
        frqPosition = onMaskLength/2;
        frqCenter = frqStep * frqPosition;
        bandList.clear();
        for(int i=1; i<=5; i++){
            bandList.add( (int) ( frqStep*i*2));
            bandStickQtyList.add(i*2);
        }
        viewBandList.clear();
        for(int i=0; i<5; i++){
            str = "  "+bandList.get(i)+" МГц";
            viewBandList.add(str);
        }

        float prevStart = start;
        int tmp;
        viewBandStepList.clear();
        for(int i=0; i<onMaskLength; i++){
            tmp = (int)Math.ceil (prevStart+frqStep);
            if(tmp > stop)tmp = stop;
            str = (int)Math.round (prevStart)+" - "+tmp+" МГц";
            viewBandStepList.add(str);
            prevStart += frqStep;
        }
    }

    public ObjRange(int _start, int _stop){
        String str;
        cnt++;
        num = cnt;
        start = _start;
        stop = _stop;
        viewRange = start.toString()+" - "+stop.toString()+" МГц";
        rangeWidth = (float)stop - start;
        initFrqStep();



        applyNewParameters();
    }
}
