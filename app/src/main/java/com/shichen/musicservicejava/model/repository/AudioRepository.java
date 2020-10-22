package com.shichen.musicservicejava.model.repository;

import com.shichen.musicservicejava.model.AudioSource;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/4.
 */
public class AudioRepository implements AudioSource {
    private static volatile AudioRepository INSTANCE;
    private final AudioSource audioLocalSource, audioRemoteSource;

    public static AudioRepository getInstance(AudioSource audioLocalSource, AudioSource audioRemoteSource) {
        if (INSTANCE == null) {
            synchronized (AudioRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AudioRepository(audioLocalSource, audioRemoteSource);
                }
            }
        }
        return INSTANCE;
    }

    private AudioRepository(AudioSource audioLocalSource, AudioSource audioRemoteSource) {
        this.audioLocalSource = audioLocalSource;
        this.audioRemoteSource = audioRemoteSource;
    }

}
