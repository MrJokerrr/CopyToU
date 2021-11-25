package com.masai.copytou.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.masai.copytou.MyApplication;


/**
 * 通用（主、子线程）的自定义Toast
 */
public class ToastUtil {
	static Toast toast = null;
	static Handler mainHandler = null;

	/**
	 * 显示Toast信息（子线程也可以调用）
	 */
	private static void showText(Context context, int resId) {
		show(context, null, resId);
	}

	/**
	 * 显示Toast信息（子线程也可以调用）
	 */
	private static void showText(Context context, String msg) {
		show(context, msg, 0);
	}

	public static void showText(String msg) {
		showText(MyApplication.getContext(), msg);
	}

	public static void showText(int resId) {
		showText(MyApplication.getContext(), resId);
	}

	/**
	 * 通过handler（构造方法用MainLooper），实现子线程也能Toast
	 *
	 * @param context
	 * @param msg
	 * @param resId
	 */
	private static void show(final Context context, String msg, int resId) {
		if (mainHandler == null) {
			synchronized (ToastUtil.class) {
				if (mainHandler == null) {
					mainHandler = new Handler(context.getMainLooper()) {
						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);
							if (msg.obj != null) {
								showString(context, String.valueOf(msg.obj), msg.arg2);
							} else {
								showInt(context, msg.arg1, msg.arg2);
							}
						}
					};
				}
			}
		}
		Message message = mainHandler.obtainMessage(1, resId, Toast.LENGTH_LONG, msg);
		mainHandler.sendMessage(message);
	}

	private static void showInt(Context context, int resId, int time) {
		if (toast == null) {
			toast = Toast.makeText(context, "", time);
		}
		toast.setText(resId);
		toast.show();

	}

	private static void showString(Context context, String msg, int time) {
		if (toast == null) {
			toast = Toast.makeText(context, "", time);
		}
		toast.setText(msg);
		toast.show();
	}
}
