package com.ckt.criminal_note.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.ckt.criminal_note.utils.PictureUtils;

/**
 * Created by D22434 on 2017/7/26.
 */

public class PhotoFragment extends DialogFragment {

    private static String PHOTO_PATH = "path";

    public static PhotoFragment newInstance(String path) {

        Bundle args = new Bundle();
        args.putString(PHOTO_PATH, path);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String path = getArguments().getString(PHOTO_PATH);

        //加载布局
//        View v = LayoutInflater.from(getActivity())
//                .inflate(R.layout.di, null);
        Bitmap bitmap = PictureUtils.getScaledBitmap(path, getActivity());

        ImageView mImageView = new ImageView(getActivity());
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(mImageView)
                .setCancelable(true)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
