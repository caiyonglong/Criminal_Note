package com.ckt.criminal_note.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ckt.criminal_note.Crime;
import com.ckt.criminal_note.CrimePagerActivity;
import com.ckt.criminal_note.R;
import com.ckt.criminal_note.db.CrimeLab;
import com.ckt.criminal_note.utils.TimeUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by D22434 on 2017/7/21.
 */

public class CrimeListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String ItemTouchHelper_TAG = "ItemTouchHelper";

    private Crime mCrime;
    private List<Crime> crimes;
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private Button mEmptyView;

    private int positionClicked;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;
    ItemTouchHelper.Callback mITCallback;

    public interface Callbacks {
        void CrimeSelect(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mITCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //拖动功能，
                int fromx = viewHolder.getAdapterPosition();//初始时的位置
                int tox = target.getAdapterPosition();//拖动后的位置

                Collections.swap(crimes, fromx, tox);
                mAdapter.notifyItemMoved(fromx, tox);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                //删除数据
                CrimeLab.get(getActivity()).deleteCrime(crimes.get(position));
                crimes.remove(position);
                mAdapter.notifyItemRemoved(position);
                updateUI();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mITCallback);

        itemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);

        mEmptyView = (Button) v.findViewById(R.id.empty_view);


        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        return v;
    }


    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSovledImageView;


        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSovledImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            this.mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
//            mDateTextView.setText(crime.getDate().toString());
            //格式化日期
            mDateTextView.setText(TimeUtil.TimeFormat(crime.getDate()));
            mSovledImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(getContext(), CrimeActivity.class);
            //change to Viewpager
//            Intent intent = CrimeActivity.newIntent(getContext(), mCrime.getId());
            //第十七章注释掉
//            Intent intent = CrimePagerActivity.newIntent(getContext(), mCrime.getId());
//            startActivity(intent);
            //回调
            mCallbacks.CrimeSelect(mCrime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            this.mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());


            return new CrimeHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {

            Crime crime = mCrimes.get(position);
            holder.bind(crime);

        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setmCrimes(List<Crime> mCrimes) {
            this.mCrimes = mCrimes;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    /**
     * 创建菜单
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    /**
     * 菜单点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                new_crime();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 更新子菜单
     */
    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getmCrimes().size();
//        String subtitle = getString(R.string.subtitle_format, crimeCount);
        //复数显示
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    /**
     * 更新RecyclerView,显示最新状态
     */
    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        crimes = crimeLab.getmCrimes();

        if (crimes.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new_crime();
                }
            });
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setmCrimes(crimes);
            mAdapter.notifyDataSetChanged();
//            mAdapter.notifyItemChanged(positionClicked);
        }

        updateSubtitle();
    }

    private void new_crime() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
//        Intent intent = CrimePagerActivity.newIntent(
//                getActivity(), crime.getId());
//        startActivity(intent);
        /**
         * 调用回调新建crime
         */
        updateUI();
        mCallbacks.CrimeSelect(crime);

    }

}
