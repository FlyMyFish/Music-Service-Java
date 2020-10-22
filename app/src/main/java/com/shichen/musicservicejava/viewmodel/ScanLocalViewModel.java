package com.shichen.musicservicejava.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.shichen.musicservicejava.model.AudioInfo;
import com.shichen.musicservicejava.model.repository.AudioRepository;

import java.util.List;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/4.
 */
public class ScanLocalViewModel extends AndroidViewModel {
    private final AudioRepository audioRepository;
    public MutableLiveData<List<AudioInfo>> audioList=new MutableLiveData<>();
    public MutableLiveData<Boolean> scanning=new MutableLiveData<>();
    public ScanLocalViewModel(@NonNull Application application,AudioRepository audioRepository) {
        super(application);
        this.audioRepository=audioRepository;
    }
}
