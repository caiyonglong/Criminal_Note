package com.ck_telecom.d22434.criminal_note;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import android.widget.Toast;

import com.ck_telecom.d22434.criminal_note.db.CrimeLab;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements View.OnClickListener {


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

        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);

                //初始化跳到第一条和最后一条
                if (i == 0) {
                    mJumpFirst.setEnabled(false);
                } else {
                    mJumpFirst.setEnabled(true);
                }
                if (i == mCrimes.size() - 1) {
                    mJumpLast.setEnabled(false);
                } else {
                    mJumpLast.setEnabled(true);
                }
                break;
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jump_first:
                mViewPager.setCurrentItem(0);
                Toast.makeText(CrimePagerActivity.this, "the first one", Toast.LENGTH_SHORT).show();
                break;
            case R.id.jump_last:
                mViewPager.setCurrentItem(mCrimes.size() - 1);
                Toast.makeText(CrimePagerActivity.this, "the last one", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
