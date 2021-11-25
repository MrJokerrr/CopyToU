package com.masai.copytou.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.masai.copytou.R;
import com.masai.copytou.utils.ToastUtil;

public class DisplayHint {

    private static Dialog dialog;
    private static ImageView imageView;
    private static HandlerThread handlerThread;
    // 显示进度条类型
    public static final int DIALOG_PROCESS = 0; // 过程显示
    public static final int DIALOG_CLOSE = 1; // 过程显示
    public static final int TOAST = 2; // 显示,显示几秒自动关闭

    /**
     * 消息机制 另起线程显示 提示信息
     */
    public static class CommHandler extends Handler {
        public CommHandler() {
        }

        public CommHandler(Looper looper) {
            super(looper);

        }

        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String key = bundle.getString("key");
            if (key.equals(DIALOG_PROCESS + ""))// 过程显示提示信息
            {
                commDialog((Activity) msg.obj, bundle.getString("value"));
            } else if (key.equals(TOAST + ""))// 显示提示信息
            {
                displayToast(bundle.getString("value"), (Context) msg.obj);
            } else if (key.equals(DIALOG_CLOSE + "")) {
                closeCommDialog();
            }
        }
    }

    /**
     * @作者： 王梦
     * @版权： 艾体威尔电子技术(北京)有限公司
     * @函数功能: 启子线程调用父线程更改布局界面
     * @参数: message 需要显示的信息
     * @返回值: 无
     * @备注: 无
     */
    public static void HandlerOpenCommDialog(Activity context, String message) {
        handlerThread = HandleThreadSingle.creatHandlerThreadSg();
        CommHandler myHandler = new CommHandler(handlerThread.getLooper());
        Message msg = myHandler.obtainMessage();
        msg.obj = context;
        Bundle bundle = new Bundle();
        bundle.putString("value", message);
        bundle.putString("key", DIALOG_PROCESS + "");
        msg.setData(bundle);
        myHandler.sendMessage(msg);
    }

    /**
     * @作者： 王梦
     * @版权： 艾体威尔电子技术(北京)有限公司
     * @函数功能: 关闭过程显示信息
     * @参数: 无
     * @返回值: 无
     * @备注: 无
     */
    public static void HandlerCloseCommDialog() {
        handlerThread = HandleThreadSingle.creatHandlerThreadSg();
        CommHandler myHandler = new CommHandler(handlerThread.getLooper());
        Message msg = myHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("key", DIALOG_CLOSE + "");
        msg.setData(bundle);
        myHandler.sendMessage(msg);
    }

    public static void destoryHandlerThread() {
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }

    /**
     * @作者： 王梦
     * @版权： 艾体威尔电子技术(北京)有限公司
     * @函数功能: 启子线程显示提示信息
     * @参数: @param message 提示信息
     * @返回值: void
     * @备注: 无
     */
    public static void HandlerDisplayHint(String message, Activity context) {
        handlerThread = HandleThreadSingle.creatHandlerThreadSg();
        CommHandler myHandler = new CommHandler(handlerThread.getLooper());
        Message msg = myHandler.obtainMessage();
        msg.obj = context;
        Bundle bundle = new Bundle();
        bundle.putString("value", message);
        bundle.putString("key", TOAST + "");
        msg.setData(bundle);
        myHandler.sendMessage(msg);
    }

    /**
     * @作者： 王梦
     * @版权： 艾体威尔电子技术(北京)有限公司
     * @函数功能: 过程显示提示信息
     * @参数: message 需要显示的信息
     * @返回值: 无
     * @备注: 无
     */
    public static void commDialog(final Activity context, String message) {
        if (null != dialog) {
            dialog.dismiss();
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.progress_layout, null);// 得到加载view
        TextView tipTextView = (TextView) v.findViewById(R.id.tv_tishi);// 提示文字
        tipTextView.setText(message);
        imageView = (ImageView) v.findViewById(R.id.iv_load);
        //加载动画XML文件,生成动画指令
        imageView.setImageResource(R.drawable.animation_load);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new Dialog(context, R.style.LodingDialog);
                dialog.setContentView(v);
                dialog.setCancelable(false);// 不可以用“返回键”取消
                dialog.show();
            }
        });
    }

    // 增加可取消和取消监听
    public static void commDialog(final Activity context, String message, final boolean cancelable, final DialogInterface.OnCancelListener listener) {
        if (null != dialog) {
            dialog.dismiss();
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.progress_layout, null);// 得到加载view
        TextView tipTextView = (TextView) v.findViewById(R.id.tv_tishi);// 提示文字
        tipTextView.setText(message);
        imageView = (ImageView) v.findViewById(R.id.iv_load);
        //加载动画XML文件,生成动画指令
        imageView.setImageResource(R.drawable.animation_load);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new Dialog(context, R.style.LodingDialog);
                dialog.setContentView(v);
                dialog.setCancelable(cancelable);// 不可以用“返回键”取消
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnCancelListener(listener);
                dialog.show();
            }
        });
    }

    /**
     * @作者： 王梦
     * @版权： 艾体威尔电子技术(北京)有限公司
     * @函数功能: 关闭过程显示信息
     * @参数: 无
     * @返回值: 无
     * @备注: 无
     */
    public static void closeCommDialog() {
        if (null != dialog) {
            if (imageView != null) {
                Drawable draw = imageView.getDrawable();
                if (draw != null) {
                    if (draw instanceof AnimationDrawable) {
                        AnimationDrawable animationDrawable = (AnimationDrawable) draw;
                        animationDrawable.stop();
                    }
                }
                dialog.dismiss();
            }
        }
    }

    /**
     * @作者： 王梦
     * @版权： 艾体威尔电子技术(北京)有限公司
     * @函数功能: 显示提示信息
     * @参数: @param message 提示信息
     * @返回值: void
     * @备注: 无
     */
    public static void displayToast(String message, Context context) {
        closeCommDialog();
        ToastUtil.showText(message);
    }

}
