package com.ckt.criminal_note.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ckt.criminal_note.Crime;
import com.ckt.criminal_note.R;
import com.ckt.criminal_note.db.CrimeLab;
import com.ckt.criminal_note.utils.PictureUtils;
import com.ckt.criminal_note.utils.TimeUtil;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by D22434 on 2017/7/21.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String IMAGE_PHOTO = "PhotoImage";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 4;

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 5;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 3;

    private Crime mCrime;
    private File mPhotoFile;

    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageView mPhotoImageView;
    private ImageButton mPhotoButton;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);

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

    /**
     * 新增 fragment argument
     *
     * @param crimeId
     * @return
     */
    public static CrimeFragment newInstance(UUID crimeId) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        Log.d(ARG_CRIME_ID, crimeId.toString());

        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This one too
                updateCrime();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mCallButton = (Button) v.findViewById(R.id.crime_call);
        mPhotoImageView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);


        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
//                DatePickerFragment dialog = new DatePickerFragment();
                //传递数据
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();

                //传递数据
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });
        /**
         * 联系人
         */
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        });

        /**
         * 发送消息
         */
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .startChooser();

                /*Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);*/
            }
        });

        /**
         * 打电话
         */
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + mCrime.getNumber());
                Intent intent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(intent);
            }
        });

        /**
         * 选择
         */
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect() + "(" + mCrime.getNumber() + ")");
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null
                && captureImage.resolveActivity(packageManager) != null;

        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermission(MY_PERMISSIONS_REQUEST_CAMERA)) {
                    Uri uri = FileProvider.getUriForFile(getActivity(),
                            "com.ckt.criminal_note.fileprovider", mPhotoFile);
                    captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    List<ResolveInfo> cameraActivities = getActivity()
                            .getPackageManager().queryIntentActivities(captureImage,
                                    PackageManager.MATCH_DEFAULT_ONLY);

                    for (ResolveInfo activity : cameraActivities) {
                        getActivity().grantUriPermission(activity.activityInfo.packageName,
                                uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivityForResult(captureImage, REQUEST_PHOTO);
                    }
                }

            }
        });

        ViewTreeObserver viewTreeObserver = mPhotoImageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoView(mPhotoImageView.getWidth(), mPhotoImageView.getHeight());
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setmDate(date);
            updateCrime();
            updateDate();
        }
        if (requestCode == REQUEST_TIME) {
            Date date = (Date) data
                    .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setmDate(date);
            updateCrime();
            updateDate();
        }
        //查找手机联系人
        if (data != null && requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            String[] phone = getContacts(contactUri);
            if (phone == null) {
                Toast.makeText(getActivity(), "请选择有效的联系人！", Toast.LENGTH_SHORT).show();
                return;
            }
            updateCrime();
            mCrime.setSuspect(phone[0]);
            mCrime.setNumber(phone[1]);
            mSuspectButton.setText(phone[0]);

        }
        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.ckt.criminal_note.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate() {
//        mDateButton.setText(mCrime.getDate().toString());
        //格式化日期
        mDateButton.setText(TimeUtil.TimeFormat(mCrime.getDate()));
        mTimeButton.setText(TimeUtil.TimeFormatToTime(mCrime.getDate()));
    }

    /**
     * 获取Crime报告
     *
     * @return
     */
    public String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_no_suspect);
        }

        String dateString = TimeUtil.TimeFormat(mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void updatePhotoView(int x, int y) {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoImageView.setImageDrawable(null);
            return;
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), x, y);
            mPhotoImageView.setImageBitmap(bitmap);
        }
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                PhotoFragment photo = PhotoFragment
                        .newInstance(mPhotoFile.getPath());
                photo.setCancelable(true);
                photo.show(manager, IMAGE_PHOTO);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        //数据刷新
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 获取联系人的姓名、联系电话
     *
     * @param contactUri
     * @return
     */
    private String[] getContacts(Uri contactUri) {
        String[] phoneContacts = new String[2];

        String[] queryFields = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID
        };

        Cursor c = getActivity().getContentResolver()
                .query(contactUri, queryFields, null, null, null);

        try {
            if (c.getCount() == 0) {
                return null;
            }
            c.moveToFirst();
            phoneContacts[0] = c.getString(0);
            String contactId = c.getString(1);

            Cursor phone = getActivity().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                                    + contactId, null, null);

            if (phone.getCount() == 0) {
                return null;
            }
            // 只取第一个数据
            c.moveToFirst();
            phone.moveToFirst();
            String number = phone.getString(phone
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneContacts[1] = number;
        } catch (Exception e) {
            return null;
        } finally {
            c.close();
        }
        return phoneContacts;
    }

    /**
     * 检测授权
     *
     * @param request
     */
    private boolean checkPermission(int request) {
        switch (request) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    return false;
                } else {
                    Intent pickContact = new Intent(Intent.ACTION_PICK,
                            ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(pickContact, REQUEST_CONTACT);
                }
                return true;

            case MY_PERMISSIONS_REQUEST_CAMERA:

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                    return false;
                }
                return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

//                    Intent pickContact = new Intent(Intent.ACTION_PICK,
//                            ContactsContract.Contacts.CONTENT_URI);
//                    startActivityForResult(pickContact, REQUEST_CONTACT);

                } else {
                    Toast.makeText(getActivity(), "授权被拒绝", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

//                    Intent pickContact = new Intent(Intent.ACTION_PICK,
//                            ContactsContract.Contacts.CONTENT_URI);
//                    startActivityForResult(pickContact, REQUEST_CONTACT);

                } else {
                    Toast.makeText(getActivity(), "授权被拒绝", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return;
        }
    }
}
