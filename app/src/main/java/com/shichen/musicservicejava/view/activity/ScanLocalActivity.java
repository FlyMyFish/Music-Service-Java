package com.shichen.musicservicejava.view.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shichen.musicservicejava.R;
import com.shichen.musicservicejava.model.AudioInfo;
import com.shichen.musicservicejava.service.ScanMusicService;
import com.shichen.musicservicejava.viewmodel.ScanLocalViewModel;
import com.shichen.musicservicejava.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.drawable.GradientDrawable.LINE;
import static android.graphics.drawable.GradientDrawable.LINEAR_GRADIENT;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/4.
 */
public class ScanLocalActivity extends AppCompatActivity {
    private String[] needPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ScanMusicService.ScanMusicBinder mBinder;
    private volatile boolean bindSuccess = false;
    private ScanLocalMusicConnection connection = new ScanLocalMusicConnection();
    ScanLocalViewModel scanLocalViewModel;
    private static final int PERMISSON_REQUESTCODE = 0;
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    private RecyclerView rvLocalMusic;
    private ProgressBar pbScanning;
    private MusicAdapter musicAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_music_layout);
        pbScanning = findViewById(R.id.pb_scanning);
        rvLocalMusic = findViewById(R.id.rv_local_music);
        rvLocalMusic.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        int[] gradientColor = new int[2];
        gradientColor[0] = 0xffffffff;
        gradientColor[1] = getResources().getColor(R.color.colorPrimary);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColor);
        gradientDrawable.setGradientType(LINEAR_GRADIENT);
        float density = getResources().getDisplayMetrics().density;
        gradientDrawable.setSize(getResources().getDisplayMetrics().widthPixels, (int) density * 2);

        itemDecoration.setDrawable(gradientDrawable);
        rvLocalMusic.addItemDecoration(itemDecoration);
        musicAdapter = new MusicAdapter();
        rvLocalMusic.setAdapter(musicAdapter);
        scanLocalViewModel = obtainViewModel();
        scanLocalViewModel.audioList.observe(this, new Observer<List<AudioInfo>>() {
            @Override
            public void onChanged(List<AudioInfo> audioInfoList) {
                musicAdapter.replaceAll(audioInfoList);
            }
        });
        scanLocalViewModel.scanning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    pbScanning.setVisibility(View.VISIBLE);
                } else {
                    pbScanning.setVisibility(View.GONE);
                }
            }
        });
    }

    private ScanLocalViewModel obtainViewModel() {
        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        ViewModelProvider provider = new ViewModelProvider(this, factory);
        return provider.get(ScanLocalViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isNeedCheck) {
            //通过注解反射得到该页面需要的权限
            if (needPermissions.length > 0) {
                checkPermissions(needPermissions);
            }
        }
    }

    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        } else {
            allPermissionsOk(permissions);
        }
    }

    public void allPermissionsOk(String[] permissions) {
        Intent intent = new Intent(this, ScanMusicService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String... permissions) {
        List<String> needRequestPermissionList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissionList.add(perm);
            }
        }
        return needRequestPermissionList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(grantResults)) {
                permissionsDenied(deniedPermissions(permissions, grantResults));
                isNeedCheck = false;
            } else {
                allPermissionsOk(permissions);
            }
        }
    }

    private List<String> deniedPermissions(@NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> deniedPermissionList = new ArrayList<>();
        for (int index = 0; index < grantResults.length; index++) {
            if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissionList.add(permissions[index]);
            }
        }
        return deniedPermissionList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults 授权结果
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void permissionsDenied(List<String> deniedPermissions) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBinder != null) {
            scanLocalViewModel.scanning.setValue(true);
            mBinder.startScan();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBinder != null) {
            scanLocalViewModel.scanning.postValue(false);
            mBinder.pauseScan();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bindSuccess) {
            mBinder.stopScan();
            unbindService(connection);
            mBinder = null;
            connection = null;
        }
    }

    private class ScanLocalMusicConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder = (ScanMusicService.ScanMusicBinder) iBinder;
            mBinder.setCallback(new ScanMusicService.ScanCallback() {
                @Override
                public void scanAllData(List<AudioInfo> audioInfo) {
                    scanLocalViewModel.scanning.postValue(false);
                    scanLocalViewModel.audioList.postValue(audioInfo);
                }
            });
            scanLocalViewModel.scanning.setValue(true);
            mBinder.startScan();
            bindSuccess = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bindSuccess = false;
        }
    }

    private class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<AudioInfo> audioInfoList;

        public MusicAdapter() {
            audioInfoList = new ArrayList<>();
        }

        public void replaceAll(List<AudioInfo> newData) {
            audioInfoList.clear();
            audioInfoList.addAll(newData);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MusicItemVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_local_music_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MusicItemVH) {
                MusicItemVH mHolder = (MusicItemVH) holder;
                AudioInfo audioInfo = audioInfoList.get(position);
                String strArtist = "歌手:" + audioInfo.getArtist();
                mHolder.tvTitle.setText(audioInfo.getTitle());
                mHolder.tvArtist.setText(strArtist);
                String strAlbum = "专辑:" + audioInfo.getAlbum();
                mHolder.tvAlbum.setText(strAlbum);
                mHolder.tvOrder.setText(String.valueOf(position + 1));
            }
        }

        @Override
        public int getItemCount() {
            return audioInfoList.size();
        }

        class MusicItemVH extends RecyclerView.ViewHolder {
            TextView tvTitle;
            TextView tvArtist;
            TextView tvAlbum;
            TextView tvOrder;

            public MusicItemVH(@NonNull View itemView) {
                super(itemView);
                tvOrder = itemView.findViewById(R.id.tv_order);
                tvTitle = itemView.findViewById(R.id.tv_audio_title);
                tvArtist = itemView.findViewById(R.id.tv_audio_artist);
                tvAlbum = itemView.findViewById(R.id.tv_audio_album);
            }
        }
    }
}
