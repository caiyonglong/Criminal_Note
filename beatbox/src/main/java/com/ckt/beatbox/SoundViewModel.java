package com.ckt.beatbox;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by D22434 on 2017/7/27.
 */

public class SoundViewModel extends BaseObservable {
    private Sound mSound;
    private BeatBox mBeatBox;

    public SoundViewModel(BeatBox mBeatBox) {
        this.mBeatBox = mBeatBox;
    }

    public Sound getmSound() {
        return mSound;
    }

    @Bindable
    public String getTitle() {
        return mSound.getmName();
    }

    public void setmSound(Sound mSound) {
        this.mSound = mSound;
        notifyChange();
    }

}
