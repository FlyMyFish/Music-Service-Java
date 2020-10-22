package com.shichen.musicservicejava.model.local;

import com.shichen.musicservicejava.model.AudioSource;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/4.
 */
public class AudioLocalSource implements AudioSource {
    private static volatile AudioLocalSource INSTANCE;

    public static AudioLocalSource getInstance(){
        if (INSTANCE==null){
            synchronized (AudioLocalSource.class){
                if (INSTANCE==null){
                    INSTANCE=new AudioLocalSource();
                }
            }
        }
        return INSTANCE;
    }

    private AudioLocalSource(){

    }
}
