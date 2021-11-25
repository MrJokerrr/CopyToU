package com.masai.copytou.dialog;

import android.os.HandlerThread;

public class HandleThreadSingle {

    private static HandlerThread handlerThread;


    private HandleThreadSingle(){

    }

    public static HandlerThread creatHandlerThreadSg(){
        if(handlerThread==null){
            handlerThread=new HandlerThread("handler_thread");
            handlerThread.start();
        }
        return handlerThread;
    }

}
