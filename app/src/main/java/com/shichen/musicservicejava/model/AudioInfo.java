package com.shichen.musicservicejava.model;

import android.support.v4.media.MediaMetadataCompat;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/4.
 */
public class AudioInfo {
    private int id;
    private String filePath;
    private String title;
    private Long addTime;

    private String displayName;
    private String size;
    private String artist;
    private String album;
    private String genre;

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public MediaMetadataCompat convert() {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(id))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, filePath)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, displayName)
                .putString(MediaMetadataCompat.METADATA_KEY_DATE, String.valueOf(addTime))
                .build();
    }

    @Override
    public String toString() {
        return "AudioInfo{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", title='" + title + '\'' +
                ", addTime=" + addTime +
                ", displayName='" + displayName + '\'' +
                ", size='" + size + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                '}';
    }
}
