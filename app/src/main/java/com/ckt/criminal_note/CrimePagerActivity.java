package com.ckt.criminal_note;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import android.widget.Toast;

import com.ckt.criminal_note.db.CrimeLab;
import com.ckt.criminal_note.fragment.CrimeFragment;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity
        implements View.OnClickListener, CrimeFragment.Callbacks {


    public static final String EXTRA_CRIME_ID =
            "com.ck_telecom.d22434.criminal_note.crime_id";


    private ViewPager mViewPager;
    private FloatingActionButton mJumpFirst, mJumpLast;
    private List<Crime> mCrimes;

    private int curItem;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mCrimes = CrimeLab.get(this).getmCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();

        mJumpFirst = (FloatingActionButton) findViewById(R.id.jump_first);
        mJumpLast = (FloatingActionButton) findViewById(R.id.jump_last);
        mJumpFirst.setOnClickListener(this);
        mJumpLast.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateJumpButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                updateJumpButton(i);
                break;
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jump_first:
                mViewPager.setCurrentItem(0);
                updateJumpButton(0);
                break;
            case R.id.jump_last:
                mViewPager.setCurrentItem(mCrimes.size() - 1);
                updateJumpButton(mCrimes.size() - 1);
                break;
        }
    }

    /**
     * 更新跳转按钮
     *
     * @param position
     */
    private void updateJumpButton(int position) {

        if (position == 0) {
            mJumpFirst.hide();
        } else {
            mJumpFirst.show();
        }
        if (position == mCrimes.size() - 1) {
            mJumpLast.hide();
        } else {
            mJumpLast.show();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}
