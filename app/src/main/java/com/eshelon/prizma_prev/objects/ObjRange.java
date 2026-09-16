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
    public void setFrqPosition(Integer frqPosition) {
        boolean canChange = true;
        Log.i("MY_TEG","pos -> "+frqPosition);
        if(frqPosition+currentBandStickQty/2 >= C_.FRQ_STEP_QTY+1){
//            frqPosition -= frqPosition+currentBandStickQty/2 - C_.FRQ_STEP_QTY;
            canChange = false;
        }
        if(frqPosition-currentBandStickQty/2 < 0){
//            frqPosition = frqPosition+currentBandStickQty/2;
            canChange = false;
        }
        if(canChange)this.frqPosition = frqPosition;
        applyNewParameters();
    }
    ArrayList<String>viewBandStepList = new ArrayList<>();

    public ArrayList<String> getViewBandStepList() {
        return viewBandStepList;
    }

    Integer currentBandStickQty = 0;
    ArrayList<Integer> bandStickQtyList = new ArrayList<>();
    void applyNewParameters(){
        currentBandWidth = bandList.get(currentBand);
        frqCenter = frqStep * frqPosition+start;//+currentBandWidth/2
        currentBandStart =  frqCenter - currentBandWidth/2;
//        currentBandStop  = frqCenter + currentBandWidth/2;
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

    public ObjRange(int _start, int _stop){
        cnt++;
        num = cnt;
        start = _start;
        stop = _stop;
        viewRange = start.toString()+" - "+stop.toString()+" МГц";
        rangeWidth = (float)stop - start;
        frqStep = rangeWidth / C_.FRQ_STEP_QTY;
        frqPosition = C_.FRQ_STEP_QTY/2;
        frqCenter = frqStep * frqPosition;

        for(int i=1; i<=5; i++){
            bandList.add( (int) ( frqStep*i*2));
            bandStickQtyList.add(i*2);
        }
        String str;
        for(int i=0; i<5; i++){
            str = "  "+bandList.get(i)+" МГц";
            viewBandList.add(str);
        }

        float prevStart = start;
        int tmp;
        for(int i=0; i<C_.FRQ_STEP_QTY; i++){
            tmp = (int)Math.ceil (prevStart+frqStep);
            if(tmp > stop)tmp = stop;
            str = (int)Math.round (prevStart)+" - "+tmp+" МГц";
            viewBandStepList.add(str);
            prevStart += frqStep;
        }

        applyNewParameters();
    }
}
