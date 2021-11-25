package com.masai.copytou.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.masai.copytou.R;
import com.masai.copytou.adapter.MenuSelectAdapter;

import java.util.ArrayList;

public class SingleSelectDialog {

    private static int clickPosition = 0;

    public static void SingleSelectDialog(Context mContext, String title, ArrayList items) {
        clickPosition = 0;
        final MenuSelectAdapter selectAdapter = new MenuSelectAdapter(mContext, items);

        final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_select_dialog, null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        TextView tvTitle = (TextView) view.findViewById(R.id.txt_title);
        TextView sure = (TextView) view.findViewById(R.id.txt_sure);
        TextView cancel = (TextView) view.findViewById(R.id.txt_cancel);
        ListView typeList = (ListView) view.findViewById(R.id.lv_select);
        typeList.setAdapter(selectAdapter);
        tvTitle.setText(title);
        typeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickPosition = position;
                selectAdapter.setSelectedPosition(position);
                selectAdapter.notifyDataSetInvalidated();
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    dialog.dismiss();
//                    setResult(null);
                    return true;
                }
                return false;
            }
        });

        dialog.show();
    }

    public static void SingleSelectDialog(Context mContext, String title, ArrayList<String> contents, final OnSingleDialogSelected listener) {
        clickPosition = 0;
        final MenuSelectAdapter selectAdapter = new MenuSelectAdapter(mContext, contents);

        final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_select_dialog, null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        TextView tvTitle = (TextView) view.findViewById(R.id.txt_title);
        TextView sure = (TextView) view.findViewById(R.id.txt_sure);
        TextView cancel = (TextView) view.findViewById(R.id.txt_cancel);
        ListView typeList = (ListView) view.findViewById(R.id.lv_select);
        typeList.setAdapter(selectAdapter);
        tvTitle.setText(title);
        typeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickPosition = position;
                selectAdapter.setSelectedPosition(position);
                selectAdapter.notifyDataSetInvalidated();
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onConfirm(clickPosition);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onCancel();
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    dialog.dismiss();
//                    setResult(null);
                    return true;
                }
                return false;
            }
        });

        dialog.show();
    }

    // 回调接口
    public interface OnSingleDialogSelected{
        void onCancel();
        void onConfirm(int position);
    }

}
