package com.eshelon.prizma_prev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eshelon.prizma_prev.G_;
import com.eshelon.prizma_prev.R;
import com.eshelon.prizma_prev.interfaces.CB;
import com.eshelon.prizma_prev.interfaces.ItemPatternListener;
import com.eshelon.prizma_prev.objects.JmmrState;

import java.util.List;

public class PatternAdapter extends ArrayAdapter<JmmrState> {
    ItemPatternListener listener;
    List<JmmrState> jmmrStateList;
    Context ctxt;

    public PatternAdapter(@NonNull Context context, int resource,  @NonNull List<JmmrState> objects, ItemPatternListener clickListener) {
        super(context, resource, objects);
        listener = clickListener;
        ctxt = context;
        jmmrStateList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pattern_list_item, null, false);
            viewHolder.txtTitle = convertView.findViewById(R.id.sPattItemTitle);
            viewHolder.txtRange = convertView.findViewById(R.id.sPattItemRange);
            viewHolder.txtMask1 = convertView.findViewById(R.id.sPattItemMask1);
            viewHolder.txtMask2 = convertView.findViewById(R.id.sPattItemMask2);
            viewHolder.pattItem = convertView.findViewById(R.id.sPattItem);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            if(G_.jmmr_list != null){
                for(JmmrState jmmr :  G_.jmmr_list){
                    if(jmmrStateList.get(position).dev_range == jmmr.dev_range){
                        viewHolder.pattItem.setBackgroundResource(R.drawable.range_bacground_red);
                    }else{
                        viewHolder.pattItem.setBackgroundResource(R.drawable.range_bacground_no_active);
                    }
                }
            }
        }
        viewHolder.txtTitle.setText(jmmrStateList.get(position).patt_name);
        viewHolder.txtRange.setText(G_.rangeGroupList.get(jmmrStateList.get(position).dev_range).getViewRange());
        viewHolder.txtMask1.setText(Math.toIntExact(jmmrStateList.get(position).msk1)+"");
        viewHolder.txtMask2.setText(Math.toIntExact(jmmrStateList.get(position).msk2)+"");

        if(G_.jmmr_list != null){
            for(JmmrState jmmr :  G_.jmmr_list){
                if(jmmrStateList.get(position).dev_range == jmmr.dev_range){
                    viewHolder.pattItem.setBackgroundResource(R.drawable.range_bacground_red);
                }else{
                    viewHolder.pattItem.setBackgroundResource(R.drawable.range_bacground_no_active);
                }
            }
        }

        viewHolder.pattItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JmmrState jmmrState = new JmmrState();
                jmmrState = jmmrStateList.get(position);
                listener.cb(jmmrState);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView txtTitle, txtRange, txtMask1, txtMask2;
        LinearLayout pattItem;
    }
}
