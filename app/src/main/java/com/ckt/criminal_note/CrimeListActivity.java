package com.ckt.criminal_note;

import android.support.v4.app.Fragment;

import com.ckt.criminal_note.fragment.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

}
