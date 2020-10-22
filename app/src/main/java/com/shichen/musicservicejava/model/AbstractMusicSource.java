package com.shichen.musicservicejava.model;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/7.
 */
public abstract class AbstractMusicSource implements MusicSource{

    interface State{
        int STATE_CREATED = 1;
        int STATE_INITIALIZING = 2;
        int STATE_INITIALIZED = 3;
        int STATE_ERROR = 4;
    }

    //@Retention表示这个注解保留的范围，SOURCE=注解将被编译器编译的时候丢弃，不在代码运行时存在，这个注解只是希望IDE警告限定值的范围并不需要保留到VM或者运行时
    @Retention(SOURCE)
//@Target 这个注解需要使用的地方 PARAMETER=注解将被使用到方法的参数中
    @Target({FIELD,PARAMETER})
//显式声明被定义的整数值，除了@IntDef还有@LongDef @StringDef等等
    @IntDef(value = {State.STATE_CREATED,State.STATE_INITIALIZING,State.STATE_INITIALIZED,State.STATE_ERROR})
    private  @interface StateDef {

    }

    @StateDef
    protected int state=State.STATE_CREATED;

    synchronized public void setState(@StateDef int value) {
        this.state=value;
    }

    @Override
    public boolean whenReady() {
        return state==State.STATE_INITIALIZED;
    }

    protected List<MediaMetadataCompat> sourceData=new ArrayList<>();

    @Override
    public List<MediaMetadataCompat> getAll() {
        return sourceData;
    }

    @Override
    public List<MediaMetadataCompat> search(String query, Bundle extras) {
        List<MediaMetadataCompat> focusSearchResult=new ArrayList<>();
        Iterator<MediaMetadataCompat> iterator=sourceData.iterator();
        String extraMediaFocus=extras.getString(MediaStore.EXTRA_MEDIA_FOCUS);
        if (extraMediaFocus==null){
            focusSearchResult.clear();
        }else if (extraMediaFocus.equals(MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE)){
            String genre=extras.getString(MediaStore.EXTRA_MEDIA_GENRE);
            while (iterator.hasNext()){
                MediaMetadataCompat item=iterator.next();
                if (item.getString(MediaMetadataCompat.METADATA_KEY_GENRE).equals(genre)){
                    focusSearchResult.add(item);
                }
            }
        }else if (extraMediaFocus.equals(MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE)){
            String artist=extras.getString(MediaStore.EXTRA_MEDIA_ARTIST);
            while (iterator.hasNext()){
                MediaMetadataCompat item=iterator.next();
                if (item.getString(MediaMetadataCompat.METADATA_KEY_ARTIST).equals(artist)||item.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST).equals(artist)){
                    focusSearchResult.add(item);
                }
            }
        }else if (extraMediaFocus.equals(MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE)){
            String artist = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST);
            String album = extras.getString(MediaStore.EXTRA_MEDIA_ALBUM);
            while (iterator.hasNext()){
                MediaMetadataCompat item=iterator.next();
                if ((item.getString(MediaMetadataCompat.METADATA_KEY_ARTIST).equals(artist)||item.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST).equals(artist))&&item.getString(MediaMetadataCompat.METADATA_KEY_ALBUM).equals(album)){
                    focusSearchResult.add(item);
                }
            }
        }else if (extraMediaFocus.equals(MediaStore.Audio.Media.ENTRY_CONTENT_TYPE)){
            String title = extras.getString(MediaStore.EXTRA_MEDIA_TITLE);
            String artist = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST);
            String album = extras.getString(MediaStore.EXTRA_MEDIA_ALBUM);
            while (iterator.hasNext()){
                MediaMetadataCompat item=iterator.next();
                if ((item.getString(MediaMetadataCompat.METADATA_KEY_ARTIST).equals(artist)||item.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST).equals(artist))&&item.getString(MediaMetadataCompat.METADATA_KEY_ALBUM).equals(album)&&item.getString(MediaMetadataCompat.METADATA_KEY_TITLE).equals(title)){
                    focusSearchResult.add(item);
                }
            }
        }else {
            focusSearchResult.clear();
        }

        if (focusSearchResult.isEmpty()){
            String genre=extras.getString(MediaStore.EXTRA_MEDIA_GENRE);
            String title = extras.getString(MediaStore.EXTRA_MEDIA_TITLE);
            if (query.isEmpty()&&genre!=null&&title!=null){
                while (iterator.hasNext()){
                    MediaMetadataCompat item=iterator.next();
                    if ((item.getString(MediaMetadataCompat.METADATA_KEY_GENRE).contains(genre))||(item.getString(MediaMetadataCompat.METADATA_KEY_TITLE).contains(title))){
                        focusSearchResult.add(item);
                    }
                }
            }
        }
        return focusSearchResult;
    }
}
