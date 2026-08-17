package com.eshelon.prizma_prev.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eshelon.prizma_prev.BtListActivity;
import com.eshelon.prizma_prev.C_;
import com.eshelon.prizma_prev.G_;
import com.eshelon.prizma_prev.interfaces.ItemClickListener;
import com.eshelon.prizma_prev.R;
import com.eshelon.prizma_prev.interfaces.ItemDevSelListener;
import com.eshelon.prizma_prev.objects.JmmrState;
import com.eshelon.prizma_prev.objects.ObjRange;

import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DevListAdapter extends ArrayAdapter<JmmrState> {
    ItemDevSelListener listener;
    List<JmmrState> jmmrStateList;
    JmmrState jmmrState;
    Context ctxt;
    ObjRange devRange;
    LinearLayout llDevInfoItem;

    LinearLayout.LayoutParams lParams;
    LinearLayout llChngParam;
    LinearLayout.LayoutParams lParamsSizeParent;
    LinearLayout llStick;
    public DevListAdapter(@NonNull Context context, int resource, @NonNull List<JmmrState> objectList, ItemDevSelListener listener) {
        super(context, resource, objectList);
        this.jmmrStateList = objectList;
        this.ctxt = context;
        this.listener = listener;
        Log.i("MY_TEG", "objectList.size -> "+objectList.size());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int  devType, devGroup;
        long devId;

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dev_list_item, null, false);
            viewHolder.txtDevInfo  = convertView.findViewById(R.id.txtDevInfo);
            viewHolder.devInfoItem = convertView.findViewById(R.id.devInfoItem1);
            viewHolder.devListItem = convertView.findViewById(R.id.devListItem);
            viewHolder.battState   = convertView.findViewById(R.id.battState);
            String str;
            int vId;

            for(int i=0; i<C_.FRQ_STEP_QTY; i++){
                str = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_SPECTER +"1_"+Integer.toString(i);
                vId = ctxt.getResources().getIdentifier(str, "id", ctxt.getPackageName());
                llStick =  convertView.findViewById(vId);
                viewHolder.specterPiece1.add(llStick);

                str = C_.BASE_SRC_ID_NAME+C_.SRC_ID_NAME_PATT_SPECTER +"2_"+Integer.toString(i);
                vId = ctxt.getResources().getIdentifier(str, "id", ctxt.getPackageName());
                llStick = (LinearLayout)convertView.findViewById(vId);
                viewHolder.specterPiece2.add(llStick);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        String rangeStr = "";
        jmmrState = jmmrStateList.get(position);
        if(jmmrState.dev_range < 1 || jmmrState.dev_range > 12){
            rangeStr  = "Ошибка параметра \"Диапазон\" ";
        }else {
            devRange  = G_.rangeGroupList.get(jmmrState.dev_range - 1);
            rangeStr  = "диап. "+devRange.getViewRange();
        }
        setBattState(viewHolder, jmmrState.batt_stt);
        String typeStr = jmmrState.dev_type == 1 ? "A " : "B ";
        String addressEsp = Integer.toString(jmmrState.ad_esp);


        String title = "Тип "+typeStr+" адр."+addressEsp+"  "+rangeStr;

        viewHolder.txtDevInfo.setText(title);

        llDevInfoItem = viewHolder.devInfoItem;

        lParamsSizeParent = (LinearLayout.LayoutParams) llDevInfoItem.getLayoutParams();
        int hParent = lParamsSizeParent.height;
        int noActiveHeight  = hParent/3;
        int activeHeight    = hParent - hParent/10;
        int margTopNoActive = hParent-noActiveHeight - hParent /20;
        int margTopActive   = hParent /18;

        boolean swch1 = false;
        boolean swch2 = false;
        long mask1 = jmmrState.msk1;
        long mask2 = jmmrState.msk2;

        for(int i=0; i<C_.FRQ_STEP_QTY; i++){
            swch1 = ((mask1 << i) & 0x40000000L) == 0x40000000L;
            swch2 = ((mask2 << i) & 0x40000000L) == 0x40000000L;

            llChngParam = viewHolder.specterPiece1.get(i);
            lParams = (LinearLayout.LayoutParams) llChngParam.getLayoutParams();

            if(!swch1){
                llChngParam.setBackgroundResource(R.drawable.range_no_active);
                lParams.height = noActiveHeight;
                lParams.topMargin = margTopNoActive;
            }
            else{
                llChngParam.setBackgroundResource(R.drawable.range_active);
                lParams.height = activeHeight;
                lParams.topMargin = margTopActive;
            }

            llChngParam = viewHolder.specterPiece2.get(i);
            lParams = (LinearLayout.LayoutParams) llChngParam.getLayoutParams();
            if(!swch2){
                llChngParam.setBackgroundResource(R.drawable.range_no_active);
                lParams.height = noActiveHeight;
                lParams.topMargin = margTopNoActive;
            }
            else{
                llChngParam.setBackgroundResource(R.drawable.range_active);
                lParams.height = activeHeight;
                lParams.topMargin = margTopActive;
            }
        }



        viewHolder.devListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jmmrStateList.isEmpty()) {
                    Log.i("MY_TEG", "viewHolder.devListItem.setOnClickListener: (G_.jmmrStateList is empty");
                    return;
                }
                listener.onItemDevSelClick(position);
            }
        }
        );
        return convertView;
    }

    void setBattState(ViewHolder viewHolder, int stt){
        int val = 1;
        if( stt < 2100) val = 1;
        if((stt >= 2100)&&(stt < 2350))val = 25;
        if((stt >= 2350)&&(stt < 2450))val = 50;
        if((stt >= 2450)&&(stt < 2600))val = 75;
        if( stt >= 2600)val = 100;
        viewHolder.battState.setText(val+"%");
    }
    static class ViewHolder{
        TextView txtDevInfo;
        LinearLayout devInfoItem;
        LinearLayout devListItem;
        TextView battState;
        ArrayList<LinearLayout>specterPiece1 = new ArrayList<>();
        ArrayList<LinearLayout>specterPiece2 = new ArrayList<>();
    }
}
