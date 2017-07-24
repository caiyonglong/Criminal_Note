package com.ck_telecom.d22434.criminal_note;

import java.util.Date;
import java.util.UUID;

/**
 * Created by D22434 on 2017/7/21.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setSolved(boolean solved) {
        this.mSolved = solved;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }
}
