package com.eshelon.prizma_prev.object;

public class ObjRange {
    private static int cnt = 0;
    public Integer start, stop;
    public Integer width;

    public Integer num;

    public String view;

    public ObjRange(int _start, int _stop){
        cnt++;
        num = cnt;
        start = _start;
        stop = _stop;
        width = stop - start;
        view = start.toString()+" - "+stop.toString()+" МГц";
    }
}
