package com.eshelon.prizma_prev.adapter;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static com.eshelon.prizma_prev.C_.CMD_REMOVE_DB_LINE;
import static com.eshelon.prizma_prev.C_.CMD_SELECT_PATTERN;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eshelon.prizma_prev.AnimeViewElements;
import com.eshelon.prizma_prev.G_;
import com.eshelon.prizma_prev.MessageBox;
import com.eshelon.prizma_prev.R;
import com.eshelon.prizma_prev.interfaces.MainInterface;
import com.eshelon.prizma_prev.objects.JmmrState;
import com.eshelon.prizma_prev.objects.ObjectProcessingData;

import java.util.List;

public class PatternAdapter extends ArrayAdapter<JmmrState> {
    MainInterface listener;
    List<JmmrState> jmmrStateList;
    Context context;
    boolean mOnLongTouch = false;
    public PatternAdapter(@NonNull Context _context, int resource,  @NonNull List<JmmrState> objects, MainInterface clickListener) {
        super(_context, resource, objects);
        listener = clickListener;
        context = _context;
        jmmrStateList = objects;
    }
    void setBackground(ViewHolder viewHolder, int position, boolean updateNoActive){
        if(G_.jmmr_list != null){
            for(JmmrState jmmr :  G_.jmmr_list){
                if(jmmrStateList.get(position).dev_range == jmmr.dev_range){
                    jmmrStateList.get(position).physicalDevice = 1;
                    viewHolder.pattItem.setBackgroundResource(R.drawable.button_unpress_active);
                    viewHolder.txtTitle.setTextAppearance(R.style.txtActivePanel);
                    viewHolder.txtRange.setTextAppearance(R.style.txtActivePanel);
                }else{
                    if(updateNoActive){
                        jmmrStateList.get(position).physicalDevice = 0;
                        viewHolder.pattItem.setBackgroundResource(R.drawable.range_bacground_no_active);
                        viewHolder.txtTitle.setTextAppearance(R.style.txtUnactivePanel);
                        viewHolder.txtRange.setTextAppearance(R.style.txtUnactivePanel);
                    }
                }
            }
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.i("MY_TEG","pos -> "+position);
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
            setBackground(viewHolder,position, true);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            setBackground(viewHolder, position, true);
        }
        int range = jmmrStateList.get(position).dev_range > 0 ? jmmrStateList.get(position).dev_range - 1 : 0;
        String msk1 = Math.toIntExact(jmmrStateList.get(position).msk1)+"";
        String msk2 = Math.toIntExact(jmmrStateList.get(position).msk2)+"";
        viewHolder.txtTitle.setText(jmmrStateList.get(position).patt_name);
        viewHolder.txtRange.setText(G_.rangeGroupList.get(range).getViewRange());
        viewHolder.txtMask1.setText(msk1);
        viewHolder.txtMask2.setText(msk2);
        setBackground(viewHolder, position, false);

        viewHolder.pattItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int vId = v.getId();
                int color  = 0;
//                boolean returnVal = true;
                AnimeViewElements anime = new AnimeViewElements();
                switch (event.getAction()){
                    case ACTION_DOWN : anime.onTouch((Activity) context, v, true, color); return false;
                    case ACTION_UP   : anime.onTouch((Activity) context, v, false, 0);onPressItem(position); break;
                    case ACTION_MOVE : anime.onTouch((Activity) context, v, false, 0);return true;
                }
                v.performClick();
                return false;
            }
        });
        viewHolder.pattItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnLongTouch = true;
                Log.i("MY_TEG", "setOnLongClickListener");
                ObjectProcessingData o = new ObjectProcessingData();
                o.cmd = CMD_REMOVE_DB_LINE;
                o.object = (Integer) jmmrStateList.get(position).db_id;
                new MessageBox((Activity) context, o, listener).showModalWindow("Удалить шаблон?");

                return true;
            }
        });
        return convertView;
    }
    void onPressItem(int position){
        if(mOnLongTouch){
            mOnLongTouch = false;
            return;
        }
        ObjectProcessingData o = new ObjectProcessingData();
        o.cmd = CMD_SELECT_PATTERN;
        o.object = jmmrStateList.get(position);
        listener.cb(o);
    }
    static class ViewHolder {
        TextView txtTitle, txtRange, txtMask1, txtMask2;
        LinearLayout pattItem;
    }
}
