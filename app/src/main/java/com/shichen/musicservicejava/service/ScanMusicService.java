package com.shichen.musicservicejava.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.shichen.musicservicejava.model.AudioInfo;
import com.shichen.musicservicejava.dispatcher.AllTaskDispatcher;

import java.util.ArrayList;
import java.util.List;

public class ScanMusicService extends Service implements Runnable {
    private ScanMusicBinder mScanMusicBinder;//通过bindService与Activity进行通信
    private volatile boolean started = false;//是否已经启动过扫描服务，用来开启和关闭扫描线程，需要在线程之间保证数据一致性，使用volatile关键字
    private volatile boolean running = false;//是否处于扫描运行中的状态，用来开始和暂停扫描线程，需要在线程之间保证数据一致性，使用volatile关键字

    public ScanMusicService() {
        mScanMusicBinder = new ScanMusicBinder();//初始化Binder对象
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mScanMusicBinder;//bindService需要实现的方法，返回一个Binder对象
    }

    @Override
    public void run() {
        Log.d(getClass().getSimpleName(), "run: start--------------");
        //实现Runnable接口，执行具体的线程任务
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        List<AudioInfo> audioInfoList=new ArrayList<>();
        while (started) {
            if (cursor != null) {
                while (running && cursor.moveToNext()) {
                    AudioInfo newData = new AudioInfo();
                    newData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                    newData.setFilePath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    newData.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                    newData.setAddTime(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)));
                    newData.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                    newData.setSize(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                    newData.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                    newData.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                    audioInfoList.add(newData);
                }
                started = false;
            } else {
                started = false;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        mScanMusicBinder.scanAllData(audioInfoList);
        Log.d(getClass().getSimpleName(), "run: finish--------------");
    }

    private void start() {
        if (started) {
            //开启扫描线程，若之前已经开启过，则使用restart()唤醒线程
            restart();
        } else {
            //没有开启过扫描线程，则使用线程池开启线程
            started = true;
            if (!running) running=true;
            AllTaskDispatcher.getInstance().diskExecutor().execute(this);
        }
    }

    private void pause() {
        //暂停线程，若正在扫描中，则更改状态running暂停扫描
        if (running) {
            running = false;
        }
    }

    private void restart() {
        //重启线程，若扫描已暂停，则更改状态running唤醒扫描
        if (!running) {
            running = true;
        }
    }

    private void stop() {
        //关闭扫描服务
        if (started) {
            started = false;
        }
        if (running){
            running=false;
        }
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        stop();
        super.unbindService(conn);
    }

    public class ScanMusicBinder extends Binder {
        private ScanCallback callback;

        public void setCallback(ScanCallback callback) {
            this.callback = callback;
        }

        /**
         * 通过Binder控制扫描任务，这里开启任务
         */
        public void startScan() {
            start();
        }

        /**
         * 通过Binder控制扫描任务，这里暂停任务
         */
        public void pauseScan() {
            pause();
        }

        /**
         * 通过Binder控制扫描任务，这里关闭任务
         */
        public void stopScan() {
            stop();
        }

        public void scanAllData(List<AudioInfo> newData){
            if (callback!=null){
                callback.scanAllData(newData);
            }
        }
    }

    public interface ScanCallback {
        void scanAllData(List<AudioInfo> audioInfo);
    }
}
