package com.eshelon.prizma_prev.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
            viewHolder.txtDevInfo = convertView.findViewById(R.id.txtDevInfo);
            viewHolder.devInfoItem = convertView.findViewById(R.id.devInfoItem1);
            String str;
            int vId;

            for(int i=0; i<32; i++){
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
        int mask1 = jmmrState.msk1;
        int mask2 = jmmrState.msk2;

        for(int i=0; i<32; i++){
            swch1 = ((mask1 << i) & 0x80000000) == 0x80000000;
            swch2 = ((mask2 << i) & 0x80000000) == 0x80000000;

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

        viewHolder.txtDevInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jmmrStateList.isEmpty()) {
                    Toast.makeText(ctxt, "jmmrStateList is empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                listener.onItemDevSelClick(jmmrState);
            }
        }
        );
        return convertView;
    }

    static class ViewHolder{
        TextView txtDevInfo;
        LinearLayout devInfoItem;
        ArrayList<LinearLayout>specterPiece1 = new ArrayList<>();
        ArrayList<LinearLayout>specterPiece2 = new ArrayList<>();


    }
}
