package com.ckt.beatbox;

/**
 * Created by D22434 on 2017/7/27.
 */

public class Sound {
    private String mAssetPath;
    private String mName;
    private Integer mSoundId;


    public Sound(String mAssetPath) {
        this.mAssetPath = mAssetPath;
        String[] components = mAssetPath.split("/");
        String filename = components[components.length - 1];
        mName = filename.replace(".txt", "");

    }


    public String getmAssetPath() {
        return mAssetPath;
    }

    public String getmName() {
        return mName;
    }

    public Integer getmSoundId() {
        return mSoundId;
    }

    public void setmSoundId(Integer mSoundId) {
        this.mSoundId = mSoundId;
    }
}
