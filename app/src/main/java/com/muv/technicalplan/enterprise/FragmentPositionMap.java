package com.muv.technicalplan.enterprise;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.esafirm.rxdownloader.RxDownloader;
import com.muv.technicalplan.CircleImage;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.DialogFragmentProgress;
import com.muv.technicalplan.R;
import com.muv.technicalplan.data.DataMap;
import com.muv.technicalplan.data.DataPosition;
import com.muv.technicalplan.profile.DialogFragmentDeleteAccount;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static android.app.Activity.RESULT_OK;

public class FragmentPositionMap extends Fragment implements View.OnClickListener
{
    private static final int LAYOUT = R.layout.fragment_position_map;
    private Context context;
    private LinearLayout delete;
    private LinearLayout upload;
    private LinearLayout download;
    private EditText position;
    private FragmentSettings fragmentSettings;
    private String path;
    private String code;
    private String TAG;
    private String positionOld;
    private String name_table;
    private String TAG_Upload_Download;

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("Position", position.getText().toString());
        outState.putString("PositionOld", positionOld);
        outState.putString("Path", path);
        outState.putString("Code", code);
        outState.putString("Tag", TAG);
        outState.putString("Name_table", name_table);
        outState.putString("TAG_Upload_Download", TAG_Upload_Download);
    }

    public String getPositionOld() {
        return positionOld;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public void setPositionOld(String position)
    {
        this.positionOld = position;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void setName_table(String name_table) {
        this.name_table = name_table;
    }

    public String getName_table() {
        return name_table;
    }

    public static FragmentPositionMap newInstance(Context context, FragmentSettings fragmentSettings,
                                                  String TAG, String position, String path, String code, String name_table)
    {
        Bundle args = new Bundle();
        FragmentPositionMap fragment = new FragmentPositionMap();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setFragmentSettings(fragmentSettings);
        fragment.setTAG(TAG);
        fragment.setPositionOld(position);
        fragment.setPath(path);
        fragment.setCode(code);
        fragment.setName_table(name_table);
        return fragment;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    private void setFragmentSettings(FragmentSettings fragmentSettings)
    {
        this.fragmentSettings = fragmentSettings;
    }

    private void setContext(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT, container, false);
        delete =(LinearLayout)view.findViewById(R.id.delete);
        upload =(LinearLayout)view.findViewById(R.id.upload);
        download =(LinearLayout)view.findViewById(R.id.download_scv);
        position =(EditText) view.findViewById(R.id.add_position_map_name);
        position.setText(positionOld);
        download.setOnClickListener(this);
        delete.setOnClickListener(this);
        upload.setOnClickListener(this);
        getChangeKeyboard();

        if (savedInstanceState != null)
        {
            position.setText(savedInstanceState.getString("Position"));
            positionOld = savedInstanceState.getString("PositionOld");
            path = savedInstanceState.getString("Path");
            code = savedInstanceState.getString("Code");
            TAG = savedInstanceState.getString("Tag");
            name_table = savedInstanceState.getString("Name_table");
            TAG_Upload_Download = savedInstanceState.getString("TAG_Upload_Download");
        }
        return view;
    }

    public String getPosition()
    {
        return position.getText().toString();
    }

    public String getPath()
    {
        return path;
    }

    private void getChangeKeyboard()
    {
        position.setCursorVisible(false);
        KeyboardVisibilityEvent.setEventListener(getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen)
                        {
                            position.setCursorVisible(true);
                        }
                        else
                        {
                            position.setCursorVisible(false);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.delete:
                if (code == null)
                {
                    deleteFragment();
                }
                else
                {
                    DialogFragmentDeletePosition deletePosition = new DialogFragmentDeletePosition().newInstance(this);
                    deletePosition.setCancelable(false);
                    deletePosition.show(getFragmentManager(), "dialogFragment");
                }
                break;

            case R.id.upload:
                TAG_Upload_Download = "UPLOAD";
                UploadDownloadCSVFile(TAG_Upload_Download, false);
                break;

            case R.id.download_scv:
                DialogFragmentDownload deletePosition = new DialogFragmentDownload().newInstance(this);
                deletePosition.setCancelable(false);
                deletePosition.show(getFragmentManager(), "dialogFragment");
                break;
        }
    }

    public void DownloadScv(boolean state)
    {
        TAG_Upload_Download = "DOWNLOAD";
        UploadDownloadCSVFile(TAG_Upload_Download, state);
    }

    public void deleteFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(this).commit();
        fragmentManager.executePendingTransactions();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                fragmentSettings.update();
            }
        });
        DataMap dataMap = new DataMap();
        dataMap.setEnterprise(fragmentSettings.getEnterprise());
        dataMap.setPosition(getPosition());
        dataMap.setCode(code);
        dataMap.setPath(path);
        dataMap.setTAG(TAG);
        dataMap.setName_table(name_table);
        fragmentSettings.getEnterpriseActivity().setUpdate(false);
        fragmentSettings.setDataMapsDelete(dataMap);
    }

    private void UploadDownloadCSVFile(String TAG_Upload_Download, boolean comment)
    {
        if (name_table != null || TAG_Upload_Download.equals("UPLOAD"))
        {
            if (isStoragePermissionGranted())
            {
                if (TAG_Upload_Download.equals("UPLOAD"))
                {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/csv");
                    startActivityForResult(intent, 1);
                }
                else
                {
                    ConstantUrl constantUrl = new ConstantUrl();
                    String url = "";
                    try
                    {
                        url = constantUrl.getUrlDownloadCsvCreated(name_table, position.getText().toString(), comment);
                    }
                    catch (Exception e)
                    {}
                    RxDownloader.getInstance(context)
                            .download(url, "karta.csv", "text/csv")
                            .subscribeOn(AndroidSchedulers.mainThread());
                }
            }
        }
        else
        {
            Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.not_created_csv),
                    Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK)
                {
                    Uri selected = intent.getData();
                    path = selected.getPath();
                    File file = new File(path);
                    if (!getFileExtension(file).equals("csv"))
                    {
                        path = "";
                        Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.not_file_csv),
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0)
        {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                UploadDownloadCSVFile(TAG_Upload_Download, false);
            }
        }
    }

    public  boolean isStoragePermissionGranted()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
            else
            {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else
        {
            return true;
        }
    }
}
