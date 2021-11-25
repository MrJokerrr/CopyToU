package com.masai.copytou.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.masai.copytou.R;

import java.util.List;

/**
 * 功能描述：选择菜单适配器
 *
 * @author AsiaLYF
 * @date 2018/6/12
 */

public class MenuSelectAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList;
    private LayoutInflater mInflater;
    /**
     * 选中的位置
     */
    private int selectedPosition = 0;

    public MenuSelectAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_check_view, null);
        }

        TextView itemName = ViewHolder.get(convertView, R.id.item_name);
        ImageView itemCheck = ViewHolder.get(convertView, R.id.item_check);

        itemName.setText(mList.get(position));

        if (position % 2 == 0) {
            if (selectedPosition == position) {
                convertView.setSelected(true);
                convertView.setPressed(true);
                itemCheck.setImageResource(R.mipmap.select_check);
            } else {
                convertView.setSelected(false);
                convertView.setPressed(false);
                itemCheck.setImageResource(R.mipmap.select_uncheck);
            }
        } else {
            if (selectedPosition == position) {
                convertView.setSelected(true);
                convertView.setPressed(true);
                itemCheck.setImageResource(R.mipmap.select_check);
            } else {
                convertView.setSelected(false);
                convertView.setPressed(false);
                itemCheck.setImageResource(R.mipmap.select_uncheck);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
