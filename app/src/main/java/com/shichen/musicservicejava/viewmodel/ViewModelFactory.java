package com.shichen.musicservicejava.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.shichen.musicservicejava.model.local.AudioLocalSource;
import com.shichen.musicservicejava.model.remote.AudioRemoteSource;
import com.shichen.musicservicejava.model.repository.AudioRepository;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/4.
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static volatile ViewModelFactory INSTANCE;
    private final Application application;
    private final AudioRepository audioRepository;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application, AudioRepository.getInstance(AudioLocalSource.getInstance(), AudioRemoteSource.getInstance()));
                }
            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application, AudioRepository audioRepository) {
        this.application = application;
        this.audioRepository = audioRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ScanLocalViewModel.class)) {
            //noinspection unchecked
            return (T) new ScanLocalViewModel(application,audioRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
