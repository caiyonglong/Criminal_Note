package com.ck_telecom.d22434.criminal_note.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ck_telecom.d22434.criminal_note.Crime;
import com.ck_telecom.d22434.criminal_note.db.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by D22434 on 2017/7/24.
 */

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuid = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int solved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setTitle(title);
        crime.setmDate(new Date(date));
        crime.setSolved(solved != 0);
        crime.setSuspect(suspect);

        return crime;
    }
}
