package com.shichen.musicservicejava.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import com.shichen.musicservicejava.dispatcher.AllTaskDispatcher;


/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/7.
 */
public class ScanSource extends AbstractMusicSource implements Runnable {
    private LoadCallback callback;
    private Context context;
    private Handler mainHandler;

    public ScanSource(Context context) {
        this.context = context;
        mainHandler=new Handler(Looper.getMainLooper());
    }

    @Override
    public void load(LoadCallback callback) {
        this.callback = callback;
        AllTaskDispatcher.getInstance().diskExecutor().execute(this);
    }

    @Override
    public void run() {
        Log.d(getClass().getSimpleName(), "run: start--------------");
        setState(State.STATE_INITIALIZING);
        //实现Runnable接口，执行具体的线程任务
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AudioInfo newData = new AudioInfo();
                newData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                newData.setFilePath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                newData.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                newData.setAddTime(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)));
                newData.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                newData.setSize(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                newData.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                newData.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                sourceData.add(newData.convert());
            }
            mainHandler.post(()->callback.success());
            cursor.close();
            setState(State.STATE_INITIALIZED);
        }else {
            mainHandler.post(()->callback.fail());
            setState(State.STATE_ERROR);
        }
        Log.d(getClass().getSimpleName(), "run: finish--------------");
    }
}
