package com.eshelon.prizma_prev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.eshelon.prizma_prev.G_;
import com.eshelon.prizma_prev.interfaces.ItemClickListener;
import com.eshelon.prizma_prev.R;
import com.eshelon.prizma_prev.objects.BtDevData;

import java.util.List;

public class BtAdapter extends ArrayAdapter<BtDevData> {

    ItemClickListener listener;
    List<BtDevData> btDevList;
    Context ctxt;
    public BtAdapter(Context context, int resource, List<BtDevData> btDevList, ItemClickListener listener){
        super(context, resource, btDevList);
        this.btDevList = btDevList;
        ctxt = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_item, null, false);
            viewHolder.btName = convertView.findViewById(R.id.btDevItemName);
            viewHolder.btMac  = convertView.findViewById(R.id.btDevItemMac);
            viewHolder.btNum  = convertView.findViewById(R.id.btDevItemNum);
            viewHolder.btItem = convertView.findViewById(R.id.btDevItem);
            convertView.setTag(viewHolder);


        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.btName.setText(btDevList.get(position).getName());
        viewHolder.btMac.setText(btDevList.get(position).getMac());
        viewHolder.btNum.setText(btDevList.get(position).getNum());

        viewHolder.btItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btDevList.isEmpty()){
                    Toast.makeText(ctxt, "btDevList is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                BtDevData data = new BtDevData();
                Toast.makeText(ctxt, btDevList.get(position).getName(), Toast.LENGTH_SHORT).show();
                G_.selectBtDevice.setName(btDevList.get(position).getName());
                G_.selectBtDevice.setMac(btDevList.get(position).getMac());
                G_.selectBtDevice.setNum(position);
                G_.selectBtDevice.setDeviceSelected(true);
                data = G_.selectBtDevice;
                listener.onItemClick(data);
            }
        });

        return convertView;
    }



    static class ViewHolder{
        TextView btName, btMac, btNum;
        LinearLayout btItem;
    }
}
