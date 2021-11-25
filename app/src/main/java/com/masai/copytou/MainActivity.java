package com.masai.copytou;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.partition.Partition;
import com.masai.copytou.constants.DefConstants;
import com.masai.copytou.dialog.DisplayHint;
import com.masai.copytou.utils.FileUtil;
import com.masai.copytou.utils.SPUtils;
import com.masai.copytou.utils.ToastUtil;
import com.masai.copytou.utils.VersionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //自定义U盘读写权限
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final String LOG_DEFAULT_PATH = "/mnt/sdcard/mispos/log/Debug";
    //当前处接U盘列表
    private UsbMassStorageDevice[] storageDevices;
    //当前U盘所在文件目录
    private UsbFile cFolder;
    private final static String U_DISK_FILE_NAME = "Log_";
    private Button btnCopyToU1, btnCopyToU2;
    private EditText etPathAll, etPathDate;
    private TextView tvVersion, tvShowTitle;
    private String handlerStr = "";
    // 线程池
    private ExecutorService executorService;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    DisplayHint.commDialog(MainActivity.this, (String) msg.obj);
                    break;
                default:
                    Log.e("INFO", "mHandler: " + handlerStr);
                    showToastMsg(handlerStr);
                    DisplayHint.closeCommDialog();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerUDiskReceiver();
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("INFO", "copy to u onDestroy");
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    private void initView() {
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvShowTitle = (TextView) findViewById(R.id.tv_show_title);
        etPathAll = (EditText) findViewById(R.id.et_path_all);
        etPathDate = (EditText) findViewById(R.id.et_path_date);
        btnCopyToU1 = (Button) findViewById(R.id.btn_copy_to_u_1);
        btnCopyToU2 = (Button) findViewById(R.id.btn_copy_to_u_2);
        btnCopyToU1.setOnClickListener(this);
        btnCopyToU2.setOnClickListener(this);
        tvVersion.setText("当前版本:" + VersionUtils.getLocalVersionName(this));

        tvShowTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 跳转到设置界面
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettingActivity.class);
                MainActivity.this.startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copy_to_u_1:

                if (null == cFolder) {
                    showToastMsg("请确认已连接好线,或重新插拔OTG线!" + "\n" + "或换个U盘使用!");
                    return;
                }

                final String pathStr1 = etPathAll.getText().toString().trim();
                if (TextUtils.isEmpty(pathStr1)) {
                    showToastMsg("路径不能为空!");
                    return;
                }

                if (executorService == null) {
                    executorService = Executors.newSingleThreadExecutor();
                }

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        saveText2UDisk(pathStr1);
                    }
                });
                break;
            case R.id.btn_copy_to_u_2:

                if (null == cFolder) {
                    showToastMsg("请确认已连接好线,或重新插拔OTG线!" + "\n" + "或换个U盘使用!");
                    return;
                }

                final String pathStr2 = etPathDate.getText().toString().trim();
                if (TextUtils.isEmpty(pathStr2)) {
                    showToastMsg("路径不能为空!");
                    return;
                }

                if (executorService == null) {
                    executorService = Executors.newSingleThreadExecutor();
                }

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        saveText2UDisk(LOG_DEFAULT_PATH + pathStr2);
                    }
                });
                break;
        }
    }

    /**
     * 读取文件内容
     *
     * @param file    文件
     * @param charset 文件编码
     * @return 文件内容
     */
//    public String readFile(File file, String charset) {
//
//        long length = file.length();
//        String fileLength = formetFileSize(length);
//        Message msg = new Message();
//        msg.what = 100;
//        msg.obj = "处理中...\n文件大小:" + fileLength;
//        mHandler.sendMessage(msg);
//
//        StringBuilder sb = new StringBuilder();
//        try {
//            InputStreamReader read = new InputStreamReader(new FileInputStream(file), charset);
//            BufferedReader reader = new BufferedReader(read);
//            String line = "";
//            int i = 0;
//            while ((line = reader.readLine()) != null) {
//                if (i == 0)
//                    sb.append(line);
//                else
//                    sb.append("\n" + line);
//                i++;
//            }
//            read.close();
//        } catch (Exception e) {
//            Log.e("MS", "读取文件内容操作出错", e);
//            handlerStr = "读取文件内容操作出错: " + e.getMessage();
//            mHandler.sendEmptyMessage(0);
//        }
//        return sb.toString();
//    }
    public String readFile(File file, String charset) {

        long length = file.length();
        String fileLength = formetFileSize(length);

        Message msg = new Message();
        msg.what = 100;
        msg.obj = "        处理中...\n文件大小:" + fileLength;
        mHandler.sendMessage(msg);

        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), charset);
            BufferedReader reader = new BufferedReader(read);
            String line = "";
            int i = 0;
            while ((line = reader.readLine()) != null) {
                if (i == 0)
                    sb.append(line);
                else
                    sb.append("\n" + line);
                i++;
            }
            read.close();
        } catch (Exception e) {
            Log.e("MS", "读取文件内容操作出错", e);
            return "";
        }
        return sb.toString();
    }

    /**
     * @description 保存数据到U盘，目前是保存到根目录的
     * @author ldm
     * @time 2017/9/1 17:17
     */
//    private void saveText2UDisk(String content) {
//
//        String fileStr = readFile(new File(content), "gbk");
//        if (TextUtils.isEmpty(fileStr)) {
//            handlerStr = "读取日志内容为空,请确认路径是否正确?";
//            mHandler.sendEmptyMessage(0);
//            return;
//        }
//
//        // 拼接保存日志的文件名
//        String[] fileNames = content.split("/");
//        String fileName = U_DISK_FILE_NAME + fileNames[fileNames.length - 1] + ".txt";
//        File file = FileUtil.getSaveFile(getPackageName()
//                        + File.separator + FileUtil.DEFAULT_BIN_DIR,
//                fileName);
//        try {
//            FileWriter fw = new FileWriter(file);
//            fw.write(fileStr);
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            handlerStr = e.getMessage();
//            mHandler.sendEmptyMessage(0);
//        }
//        if (null != cFolder) {
//            Message msg = new Message();
//            msg.what = 100;
//            msg.obj = "拷贝数据中...";
//            mHandler.sendMessage(msg);
//
//            boolean saved = FileUtil.saveSDFile2OTG(file, cFolder);
//            handlerStr = saved ? "保存成功" : "保存失败";
//            mHandler.sendEmptyMessage(0);
//        }
//    }
    private void saveText2UDisk(String content) {

        Log.e("INFO", "开始保存日志");

        String fileStr = readFile(new File(content), (String) SPUtils.get(MainActivity.this, SPUtils.SP_KEY_FILE_ENCODE, DefConstants.FILE_ENCODE_GBK));
        if (TextUtils.isEmpty(fileStr)) {
            handlerStr = "读取日志内容为空,请确认路径是否正确?";
            mHandler.sendEmptyMessage(0);
            return;
        }

        // 拼接保存日志的文件名
        String[] fileNames = content.split("/");
        String fileName = U_DISK_FILE_NAME + fileNames[fileNames.length - 1] + ".txt";

        // 判断u盘是否已存在文件
        //获取根目录的文件
        try {
            UsbFile[] usbFiles = cFolder.listFiles();
            //如果有文件则遍历文件列表
            if (usbFiles != null && usbFiles.length > 0) {
                for (UsbFile file : usbFiles) {
                    if (file.getName().equals(fileName)) {
                        file.delete();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg = new Message();
        msg.what = 100;
        msg.obj = "拷贝数据中...";
        mHandler.sendMessage(msg);

        if (null != cFolder) {
            UsbFile usbFile;
            OutputStream os = null;
            try {
                usbFile = cFolder.createFile(fileName);
                os = new UsbFileOutputStream(usbFile);
                os.write(fileStr.getBytes());
                os.close();
                handlerStr = "保存成功";
                mHandler.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            handlerStr = "cFolder为null!";
            mHandler.sendEmptyMessage(0);
        }
    }

    /**
     * @description OTG广播注册
     * @author ldm
     * @time 2017/9/1 17:19
     */
    private void registerUDiskReceiver() {
        //监听otg插入 拔出
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mOtgReceiver, usbDeviceStateFilter);
        //注册监听自定义广播
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mOtgReceiver, filter);
    }

    /**
     * @description OTG广播，监听U盘的插入及拔出
     * @author ldm
     * @time 2017/9/1 17:20
     * @param
     */
    private BroadcastReceiver mOtgReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_USB_PERMISSION:
                    //接受到自定义广播
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    //允许权限申请
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbDevice != null) {
                            //用户已授权，可以进行读取操作
                            readDevice(getUsbMass(usbDevice));
                        } else {
                            cFolder = null;
                            showToastMsg("没有插入U盘");
                        }
                    } else {
                        showToastMsg("未获取到U盘权限");
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    //接收到U盘设备插入广播
                    UsbDevice device_add = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_add != null) {
                        //接收到U盘插入广播，尝试读取U盘设备数据
                        redUDiskDevsList();
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    //接收到U盘设设备拔出广播
                    cFolder = null;
                    showToastMsg("U盘已拔出");
                    DisplayHint.closeCommDialog();
                    if (executorService != null) {
                        executorService.shutdown();
                    }
                    break;
            }
        }
    };

    /**
     * @description U盘设备读取
     * @author ldm
     * @time 2017/9/1 17:20
     */
    private void redUDiskDevsList() {
        //设备管理器
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //获取U盘存储设备
        storageDevices = UsbMassStorageDevice.getMassStorageDevices(this);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        //一般手机只有1个OTG插口
        for (UsbMassStorageDevice device : storageDevices) {
            //读取设备是否有权限
            if (usbManager.hasPermission(device.getUsbDevice())) {
                readDevice(device);
            } else {
                //没有权限，进行申请
                usbManager.requestPermission(device.getUsbDevice(), pendingIntent);
            }
        }
        if (storageDevices.length == 0) {
            showToastMsg("请插入可用的U盘");
        }
    }

    private UsbMassStorageDevice getUsbMass(UsbDevice usbDevice) {
        for (UsbMassStorageDevice device : storageDevices) {
            if (usbDevice.equals(device.getUsbDevice())) {
                return device;
            }
        }
        return null;
    }

    private void readDevice(UsbMassStorageDevice device) {
        try {
            device.init();
            //初始化
            // 设备分区
            List<Partition> partitionList = device.getPartitions();
            Partition partition = partitionList.get(0);
            //文件系统
            FileSystem currentFs = partition.getFileSystem();
            currentFs.getVolumeLabel();
            //可以获取到设备的标识
            // 通过FileSystem可以获取当前U盘的一些存储信息，包括剩余空间大小，容量等等
//            Log.e("Capacity: ", currentFs.getCapacity() + "");
//            Log.e("Occupied Space: ", currentFs.getOccupiedSpace() + "");
//            Log.e("Free Space: ", currentFs.getFreeSpace() + "");
//            Log.e("Chunk size: ", currentFs.getChunkSize() + "");
            cFolder = currentFs.getRootDirectory();
            //设置当前文件对象为根目录
        } catch (Exception e) {
            e.printStackTrace();
            showToastMsg(e.getMessage());
        }
    }

    private void showToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("退出程序")
                .setMessage("是否退出程序")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                return;
                            }
                        }).create();
        alertDialog.show();
    }

    /**
     * 格式化文件大小
     *
     * @param fileLength 单位b
     * @return
     */
    public static String formetFileSize(Long fileLength) {
        String fileSizeString = "";
        if (fileLength == null) {
            return fileSizeString;
        }
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileLength < 1024) {
            fileSizeString = df.format((double) fileLength) + "B";
        } else if (fileLength < 1048576) {
            fileSizeString = df.format((double) fileLength / 1024) + "K";
        } else if (fileLength < 1073741824) {
            fileSizeString = df.format((double) fileLength / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileLength / 1073741824) + "G";
        }
        return fileSizeString;
    }
}
