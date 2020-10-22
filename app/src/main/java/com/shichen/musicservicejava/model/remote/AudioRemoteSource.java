package com.shichen.musicservicejava.model.remote;

import com.shichen.musicservicejava.model.AudioSource;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/4.
 */
public class AudioRemoteSource implements AudioSource {
    private static volatile AudioRemoteSource INSTANCE;

    public static AudioRemoteSource getInstance() {
        if (INSTANCE == null) {
            synchronized (AudioRemoteSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AudioRemoteSource();
                }
            }
        }
        return INSTANCE;
    }

    private AudioRemoteSource() {

    }
}
