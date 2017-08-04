package com.ckt.beatbox;

import android.databinding.DataBindingUtil;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.ckt.beatbox.databinding.FragmentBeatBoxBinding;
import com.ckt.beatbox.databinding.ListItemSoundBinding;

import java.util.List;

/**
 * Created by D22434 on 2017/7/27.
 */

public class BeatBoxFragment extends Fragment {

    private static String TAG = "BeatBoxFragment";
    private BeatBox mBeatBox;
    private FragmentBeatBoxBinding binding;
    private float rate = 1.0f;

    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mBeatBox = new BeatBox(getActivity());


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_beat_box, container, false);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.recyclerView.setAdapter(new SoundAdapter(mBeatBox.getmSounds()));

        binding.seekbar.setMax(100);

        int progress = (int) ((rate - 0.5) / 1.5 * 100);

        binding.seekbar.setProgress(progress);
        binding.progress.setText(getRate(rate));

        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.d(TAG, "progress + " + progress);
                rate = setRate(progress);

                binding.progress.setText(getRate(rate));
                Log.d(TAG, "progress + " + rate);
                Log.d(TAG, "rate--- + " + (progress / 100 * 1.5 + 0.5));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return binding.getRoot();
    }

    private class SoundHolder extends RecyclerView.ViewHolder {
        private ListItemSoundBinding mBinding;

        public SoundHolder(ListItemSoundBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewModel(new SoundViewModel(mBeatBox));
        }

        public void bind(final Sound sound) {
            mBinding.getViewModel().setmSound(sound);
            mBinding.executePendingBindings();
            mBinding.getRoot().findViewById(R.id.sound).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "rate + " + rate);
                    mBeatBox.play(sound, rate);

                }
            });
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {
        private List<Sound> mSounds;

        public SoundAdapter(List<Sound> mSounds) {
            this.mSounds = mSounds;
        }

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ListItemSoundBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.list_item_sound, parent, false);

            return new SoundHolder(binding);
        }

        @Override
        public void onBindViewHolder(SoundHolder holder, int position) {
            Sound sound = mSounds.get(position);
            holder.bind(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
    }


    public String getRate(float rate) {
        return "播放速率：" + (int) ((rate - 0.5) / 1.5 * 100) + "%";
    }

    public float setRate(int progress) {
        return (float) (progress * 1.0 / 100 * 1.5 + 0.5);
    }


}
