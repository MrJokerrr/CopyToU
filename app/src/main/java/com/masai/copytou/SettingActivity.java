package com.masai.copytou;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.masai.copytou.constants.DefConstants;
import com.masai.copytou.dialog.SingleSelectDialog;
import com.masai.copytou.utils.SPUtils;

import java.util.ArrayList;

/**
 * Created by masai on 2021/11/25.
 */

public class SettingActivity extends Activity implements View.OnClickListener, SingleSelectDialog.OnSingleDialogSelected {

    private TextView tvTitleText, tvFileEncode;
    private ArrayList fileEncodeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        tvTitleText = (TextView) findViewById(R.id.tv_title);
        tvFileEncode = (TextView) findViewById(R.id.tv_file_encode);
        tvTitleText.setText("参数设置");
        tvFileEncode.setText((CharSequence) SPUtils.get(SettingActivity.this, SPUtils.SP_KEY_FILE_ENCODE, DefConstants.FILE_ENCODE_GBK));

        tvFileEncode.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_file_encode:
                fileEncodeList = new ArrayList<>();
                fileEncodeList.add("GBK");
                fileEncodeList.add("UTF-8");
                SingleSelectDialog.SingleSelectDialog(SettingActivity.this, "请选择编码类型", fileEncodeList, this);
                break;
        }
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onConfirm(int position) {
        tvFileEncode.setText("" + fileEncodeList.get(position));
        SPUtils.put(SettingActivity.this, SPUtils.SP_KEY_FILE_ENCODE, fileEncodeList.get(position));
    }
}
