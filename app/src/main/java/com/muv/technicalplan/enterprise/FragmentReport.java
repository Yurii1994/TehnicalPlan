package com.muv.technicalplan.enterprise;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.esafirm.rxdownloader.RxDownloader;
import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.DialogFragmentProgress;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.UploadMultipart;
import com.muv.technicalplan.data.DataMaps;
import com.muv.technicalplan.data.DataPosition;
import com.muv.technicalplan.data.DataUser;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import static android.app.Activity.RESULT_OK;

public class FragmentReport extends AbstractTabFragment implements View.OnClickListener
{
    private static final int LAYOUT = R.layout.fragment_report;
    private Toast toast;
    private List<DataPosition> dataPosition = new ArrayList<>();
    private JsonParser jsonParser = new JsonParser();
    private ConstantUrl url = new ConstantUrl();
    private List<DataUser> user = DataUser.listAll(DataUser.class);
    private FragmentTransaction transaction;
    private ArrayList<PositionsParcelable> positionsParcelables = new ArrayList<>();
    private String withText;
    private String byText;
    private TextView hint;
    private LinearLayout fragment_container;
    private RelativeLayout progress;
    private boolean isCheckedWithout;
    private boolean isCheckedAll;
    private boolean isCheckedManager;
    private boolean isCheckedPerformer;
    private boolean isCheckedWhoMade;
    private boolean isCheckedRegimeAll;
    private boolean isCheckedRegimeNormal;
    private boolean isCheckedRegimeLightweight;
    private boolean isCheckedRegimeLight;
    private EditText head;
    private EditText name_doc;
    private String head_text;
    private String name_doc_text;
    private String download_name;
    private String path = "";
    private String TAG_Upload_Download;
    private EnterpriseActivity activity;
    private CheckBox without;
    private CheckBox all;
    private CheckBox manager;
    private CheckBox performer;
    private CheckBox who_made;
    private CheckBox regime_all;
    private CheckBox regime_normal;
    private CheckBox regime_lightweight;
    private CheckBox regime_light;
    private TextView with;
    private TextView by;
    private DialogFragmentProgress dialog;
    private String idTemplate;

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("PositionParcelable", positionsParcelables);
        outState.putString("WithText", withText);
        outState.putString("ByText", byText);
        outState.putBoolean("isCheckedWithout", isCheckedWithout);
        outState.putBoolean("isCheckedAll", isCheckedAll);
        outState.putBoolean("isCheckedManager", isCheckedManager);
        outState.putBoolean("isCheckedPerformer", isCheckedPerformer);
        outState.putBoolean("isCheckedWhoMade", isCheckedWhoMade);
        outState.putBoolean("isCheckedRegimeAll", isCheckedRegimeAll);
        outState.putBoolean("isCheckedRegimeNormal", isCheckedRegimeNormal);
        outState.putBoolean("isCheckedRegimeLightweight", isCheckedRegimeLightweight);
        outState.putBoolean("isCheckedRegimeLight", isCheckedRegimeLight);
        outState.putString("Head", head_text);
        outState.putString("Name_doc", name_doc_text);
        outState.putString("Download_name", download_name);
        outState.putString("Path", path);
        outState.putString("TAG_Upload_Download", TAG_Upload_Download);
        outState.putString("Id_template", idTemplate);
    }

    public static FragmentReport getInstance(Context context, String title)
    {
        Bundle args = new Bundle();
        FragmentReport fragment = new FragmentReport();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(title);
        return fragment;
    }

    private void showToastMessage(String a)
    {
        if (toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(getContext(), a, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        context = getContext();
        activity = (EnterpriseActivity)getActivity();

        with = (TextView)view.findViewById(R.id.report_with);
        by = (TextView)view.findViewById(R.id.report_by);

        if (savedInstanceState == null)
        {
            head_text = "";
            name_doc_text = "";
            withText = getDate();
            byText = getDate();
            with.setText(withText);
            by.setText(byText);
        }
        else
        {
            idTemplate = savedInstanceState.getString("Id_template");
            download_name = savedInstanceState.getString("Download_name");
            withText = savedInstanceState.getString("WithText");
            byText = savedInstanceState.getString("ByText");
            head_text = savedInstanceState.getString("Head");
            name_doc_text = savedInstanceState.getString("Name_doc");
            path = savedInstanceState.getString("Path");
            TAG_Upload_Download = savedInstanceState.getString("TAG_Upload_Download");
            with.setText(withText);
            by.setText(byText);
        }


        final SublimePickerFragment.Callback mFragmentCallbackWith = new SublimePickerFragment.Callback() {
            @Override
            public void onCancelled()
            {
                with.setText(getDate());
                withText = getDate();
            }

            @Override
            public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute,
                                                SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                                String recurrenceRule)
            {
                int year = selectedDate.getEndDate().get(Calendar.YEAR);
                int month = selectedDate.getEndDate().get(Calendar.MONTH) + 1;
                int days = selectedDate.getEndDate().get(Calendar.DAY_OF_MONTH);
                selectedDate.getEndDate().set(Calendar.HOUR_OF_DAY, 0);
                selectedDate.getEndDate().set(Calendar.MINUTE, 0);
                selectedDate.getEndDate().set(Calendar.SECOND, 0);
                selectedDate.getEndDate().set(Calendar.MILLISECOND, 0);
                String monthReal = month + "";
                if (monthReal.length() == 1)
                {
                    monthReal = "0" + month;
                }
                String daysReal = days + "";
                if (daysReal.length() == 1)
                {
                    daysReal = "0" + days;
                }
                String dateSelected = daysReal + "." + monthReal + "." + year;

                Date date = new Date();
                Calendar calendar_real = Calendar.getInstance();
                calendar_real.setTime(date);
                calendar_real.set(Calendar.HOUR_OF_DAY, 0);
                calendar_real.set(Calendar.MINUTE, 0);
                calendar_real.set(Calendar.SECOND, 0);
                calendar_real.set(Calendar.MILLISECOND, 0);

                int state_date = calendar_real.compareTo(selectedDate.getEndDate());
                if (state_date >= 0)
                {
                    with.setText(dateSelected);
                    withText = dateSelected;
                }
                else
                {
                    with.setText(getDate());
                    withText = getDate();
                    showToastMessage(getContext().getText(R.string.date_error).toString());
                }
            }
        };

        final SublimePickerFragment.Callback mFragmentCallbackBy = new SublimePickerFragment.Callback() {
            @Override
            public void onCancelled()
            {
                by.setText(getDate());
                byText = getDate();
            }

            @Override
            public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute,
                                                SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                                String recurrenceRule)
            {
                int year = selectedDate.getEndDate().get(Calendar.YEAR);
                int month = selectedDate.getEndDate().get(Calendar.MONTH) + 1;
                int days = selectedDate.getEndDate().get(Calendar.DAY_OF_MONTH);
                String monthReal = month + "";
                if (monthReal.length() == 1)
                {
                    monthReal = "0" + month;
                }
                String daysReal = days + "";
                if (daysReal.length() == 1)
                {
                    daysReal = "0" + days;
                }
                String dateSelected = daysReal + "." + monthReal + "." + year;
                Date date = new Date();
                Calendar calendar_real = Calendar.getInstance();
                calendar_real.setTime(date);
                calendar_real.set(Calendar.HOUR_OF_DAY, 0);
                calendar_real.set(Calendar.MINUTE, 0);
                calendar_real.set(Calendar.SECOND, 0);
                calendar_real.set(Calendar.MILLISECOND, 0);

                int state_date = calendar_real.compareTo(selectedDate.getEndDate());
                if (state_date >= 0)
                {
                    by.setText(dateSelected);
                    byText = dateSelected;
                }
                else
                {
                    by.setText(getDate());
                    byText = getDate();
                    showToastMessage(getContext().getText(R.string.date_error).toString());
                }
            }
        };

        with.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SublimePickerFragment pickerFrag = new SublimePickerFragment();
                pickerFrag.setCallback(mFragmentCallbackWith);

                Pair<Boolean, SublimeOptions> optionsPair = getOptions();

                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
                pickerFrag.setArguments(bundle);

                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getFragmentManager(), "SUBLIME_PICKER");
            }
        });

        by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SublimePickerFragment pickerFrag = new SublimePickerFragment();
                pickerFrag.setCallback(mFragmentCallbackBy);

                Pair<Boolean, SublimeOptions> optionsPair = getOptions();

                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
                pickerFrag.setArguments(bundle);

                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getFragmentManager(), "SUBLIME_PICKER");
            }
        });

        hint = (TextView)view.findViewById(R.id.position_hint_enterprise);
        fragment_container = (LinearLayout) view.findViewById(R.id.fragment_container_enterprise);
        progress = (RelativeLayout)view.findViewById(R.id.progress_position_enterprise);

        if (savedInstanceState == null)
        {
            update(progress, fragment_container, hint);
        }
        else
        {
            positionsParcelables = savedInstanceState.getParcelableArrayList("PositionParcelable");
            if (positionsParcelables != null)
            {
                for (int i = 0; i < positionsParcelables.size(); i++)
                {
                    DataPosition dataPosition = new DataPosition();
                    dataPosition.setLogin(positionsParcelables.get(i).login);
                    dataPosition.setPosition(positionsParcelables.get(i).position);
                    dataPosition.setCode(positionsParcelables.get(i).code);
                    dataPosition.setName_table(positionsParcelables.get(i).name_table);
                    this.dataPosition.add(dataPosition);
                }
            }

            progress.setVisibility(View.GONE);
            if (dataPosition.size() > 1)
            {
                fragment_container.setVisibility(View.VISIBLE);
            }
            else
            {
                fragment_container.setVisibility(View.GONE);
                hint.setVisibility(View.VISIBLE);
            }
        }

        without = (CheckBox)view.findViewById(R.id.checkbox_position_without);
        all = (CheckBox)view.findViewById(R.id.checkbox_position_all);
        manager = (CheckBox)view.findViewById(R.id.checkbox_position_manager);
        performer = (CheckBox)view.findViewById(R.id.checkbox_position_performer);
        who_made = (CheckBox)view.findViewById(R.id.checkbox_who_made);

        without.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isCheckedWithout = isChecked;
                if(!all.isChecked() & !manager.isChecked() & !performer.isChecked() & !without.isChecked())
                {
                    without.setChecked(true);
                }
                if (isChecked)
                {
                    all.setChecked(false);
                    manager.setChecked(false);
                    performer.setChecked(false);
                }
            }
        });

        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isCheckedAll = isChecked;
                if(!all.isChecked() & !manager.isChecked() & !performer.isChecked() & !without.isChecked())
                {
                    all.setChecked(true);
                }
                if (isChecked)
                {
                    without.setChecked(false);
                    manager.setChecked(false);
                    performer.setChecked(false);
                }
            }
        });

        manager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isCheckedManager = isChecked;
                if(!all.isChecked() & !manager.isChecked() & !performer.isChecked() & !without.isChecked())
                {
                    manager.setChecked(true);
                }
                if (isChecked)
                {
                    without.setChecked(false);
                    all.setChecked(false);
                    performer.setChecked(false);
                }
            }
        });

        performer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isCheckedPerformer = isChecked;
                if(!all.isChecked() & !manager.isChecked() & !performer.isChecked() & !without.isChecked())
                {
                    performer.setChecked(true);
                }
                if (isChecked)
                {
                    without.setChecked(false);
                    all.setChecked(false);
                    manager.setChecked(false);
                }
            }
        });

        who_made.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isCheckedWhoMade = isChecked;
            }
        });

        regime_all = (CheckBox)view.findViewById(R.id.checkbox_regime_all);
        regime_normal = (CheckBox)view.findViewById(R.id.checkbox_regime_normal);
        regime_lightweight = (CheckBox)view.findViewById(R.id.checkbox_regime_lightweight);
        regime_light = (CheckBox)view.findViewById(R.id.checkbox_regime_light);

        regime_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isCheckedWithout = isChecked;
                if(!regime_all.isChecked() & !regime_normal.isChecked() & !regime_lightweight.isChecked() & !regime_light.isChecked())
                {
                    regime_all.setChecked(true);
                }
                if (isChecked)
                {
                    regime_normal.setChecked(false);
                    regime_lightweight.setChecked(false);
                    regime_light.setChecked(false);
                }
            }
        });

        regime_normal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isCheckedAll = isChecked;
                if(!regime_all.isChecked() & !regime_normal.isChecked() & !regime_lightweight.isChecked() & !regime_light.isChecked())
                {
                    regime_normal.setChecked(true);
                }
                if (isChecked)
                {
                    regime_all.setChecked(false);
                    regime_lightweight.setChecked(false);
                    regime_light.setChecked(false);
                }
            }
        });

        regime_lightweight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isCheckedManager = isChecked;
                if(!regime_all.isChecked() & !regime_normal.isChecked() & !regime_lightweight.isChecked() & !regime_light.isChecked())
                {
                    regime_lightweight.setChecked(true);
                }
                if (isChecked)
                {
                    regime_all.setChecked(false);
                    regime_normal.setChecked(false);
                    regime_light.setChecked(false);
                }
            }
        });

        regime_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isCheckedPerformer = isChecked;
                if(!regime_all.isChecked() & !regime_normal.isChecked() & !regime_lightweight.isChecked() & !regime_light.isChecked())
                {
                    regime_light.setChecked(true);
                }
                if (isChecked)
                {
                    regime_all.setChecked(false);
                    regime_normal.setChecked(false);
                    regime_lightweight.setChecked(false);
                }
            }
        });

        if (savedInstanceState != null)
        {
            isCheckedWithout = savedInstanceState.getBoolean("isCheckedWithout");
            isCheckedAll = savedInstanceState.getBoolean("isCheckedAll");
            isCheckedManager = savedInstanceState.getBoolean("isCheckedManager");
            isCheckedPerformer = savedInstanceState.getBoolean("isCheckedPerformer");
            isCheckedWhoMade = savedInstanceState.getBoolean("isCheckedWhoMade");
            isCheckedRegimeAll = savedInstanceState.getBoolean("isCheckedRegimeAll");
            isCheckedRegimeNormal = savedInstanceState.getBoolean("isCheckedRegimeNormal");
            isCheckedRegimeLightweight = savedInstanceState.getBoolean("isCheckedRegimeLightweight");
            isCheckedRegimeLight = savedInstanceState.getBoolean("isCheckedRegimeLight");

            without.setChecked(isCheckedWithout);
            all.setChecked(isCheckedAll);
            manager.setChecked(isCheckedManager);
            performer.setChecked(isCheckedPerformer);
            who_made.setChecked(isCheckedWhoMade);
            regime_all.setChecked(isCheckedRegimeAll);
            regime_normal.setChecked(isCheckedRegimeNormal);
            regime_lightweight.setChecked(isCheckedRegimeLightweight);
            regime_light.setChecked(isCheckedRegimeLight);
        }
        else
        {
            isCheckedWithout = true;
            isCheckedRegimeAll = true;
            without.setChecked(isCheckedWithout);
            regime_all.setChecked(isCheckedRegimeAll);
        }

        Button save = (Button)view.findViewById(R.id.save_report);
        save.setOnClickListener(this);

        head = (EditText)view.findViewById(R.id.report_head);
        name_doc = (EditText)view.findViewById(R.id.report_name_doc);
        head.setText(head_text);
        name_doc.setText(name_doc_text);
        Button template = (Button)view.findViewById(R.id.report_template);
        Button sample_template = (Button)view.findViewById(R.id.report_sample_template);
        template.setOnClickListener(this);
        sample_template.setOnClickListener(this);
        getChangeKeyboard();

        dialog = new DialogFragmentProgress().newInstance(getResources().getString(R.string.processing));

        return view;
    }

    public void UploadDownloadFile(final String name, String TAG_Upload_Download)
    {
        if (isStoragePermissionGranted())
        {
            if (TAG_Upload_Download.equals("Upload"))
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
                    url = constantUrl.getUrlDownloadSampleReport(name);
                }
                catch (Exception e)
                {}
                RxDownloader.getInstance(context)
                        .download(url, name)
                        .subscribeOn(AndroidSchedulers.mainThread());
            }

        }
        else
        {
            download_name = name;
        }
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
                    if (!getFileExtension(file).equals("docx"))
                    {
                        path = "";
                        Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.not_file_docx),
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        }
    }

    private static String getFileExtension(File file)
    {
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
                UploadDownloadFile(download_name, TAG_Upload_Download);
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

    private void getChangeKeyboard()
    {
        head.setCursorVisible(false);
        name_doc.setCursorVisible(false);
        KeyboardVisibilityEvent.setEventListener(getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen)
                        {
                            head.setCursorVisible(true);
                            name_doc.setCursorVisible(true);
                        }
                        else
                        {
                            head.setCursorVisible(false);
                            name_doc.setCursorVisible(false);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.save_report:
                if (head.getText().length() == 0 || name_doc.getText().length() == 0)
                {
                    if (head.getText().length() == 0 & name_doc.getText().length() == 0)
                    {
                        showToastMessage(context.getText(R.string.head_initials_name_document).toString());
                    }
                    else
                    if (head.getText().length() == 0)
                    {
                        showToastMessage(context.getText(R.string.head_initials).toString());
                    }
                    else
                    {
                        showToastMessage(context.getText(R.string.name_document).toString());
                    }
                }
                else
                {
                    if (!path.equals(""))
                    {
                        idTemplate = getIdTemplate(0, 10);
                        ConstantUrl constantUrl = new ConstantUrl();
                        String url = constantUrl.getUrlUploadSampleReport(idTemplate);

                        UploadMultipart uploadMultipart = new UploadMultipart();
                        uploadMultipart.uploadTemplate(context, path, url, activity);
                    }
                    else
                    {
                        path = "";
                        getDataMaps dataMaps = new getDataMaps(getDataPositionToReport(dataPosition), path);
                        dataMaps.execute();
                    }
                }
                break;

            case R.id.report_sample_template:
                TAG_Upload_Download = "Download";
                UploadDownloadFile("sample_report_template.docx", TAG_Upload_Download);
                break;

            case R.id.report_template:
                if (path.equals(""))
                {
                    getFileDownload();
                }
                else
                {
                    DialogFragmentTemplate dialogFragmentTemplate = new DialogFragmentTemplate().newInstance(this);
                    dialogFragmentTemplate.setCancelable(false);
                    dialogFragmentTemplate.show(getFragmentManager(), "dialogFragment");
                }
                break;
        }
    }

    private String getIdTemplate(int min, int max)
    {
        Random random = new Random();
        String rez = "";
        for (int i = 0; i < 10; i++)
        {
            int d = random.nextInt(max - min) + min;
            rez = rez + d;
        }
        return rez;
    }

    public void  getDataMapsUpload()
    {
        String fileName = "";
        try
        {
            File file = new File(path);
            String ext = getFileExtension(file);
            fileName = idTemplate + "." + ext;
        }
        catch (Exception e)
        {}
        getDataMaps dataMaps = new getDataMaps(getDataPositionToReport(dataPosition), fileName);
        dataMaps.execute();
    }

    public void dialogShow()
    {
        if (dialog != null)
        {
            if (!dialog.isShowing())
            {

                dialog.show(activity.getFragmentManager(), "dialogFragment");
                dialog.setCancelable(false);
                dialog.setShowing(true);
            }
        }
    }

    public void dialogDismiss()
    {
        if (dialog != null)
        {
            if (dialog.isShowing())
            {
                dialog.dismiss();
                dialog.setShowing(false);
            }
        }
    }

    public void getFileDownload()
    {
        TAG_Upload_Download = "Upload";
        UploadDownloadFile("", TAG_Upload_Download);
    }

    public void getFileDelete()
    {
        path = "";
    }

    private List<DataPosition> getDataPositionToReport(List<DataPosition> dataPositions)
    {
        List<DataPosition> result = new ArrayList<>();
        List<FragmentPosition> fragmentPositions = getFragmentsPosition();
        for (int i = 0; i < fragmentPositions.size(); i++)
        {
            if (fragmentPositions.get(i).getCheckBox().isChecked() &
                    fragmentPositions.get(i).getCheckBox().getText().equals(context.getText(R.string.all).toString()))
            {
                result = new ArrayList<>();
                for (int j = 0; j < dataPositions.size(); j++)
                {
                    if (!dataPositions.get(j).getPosition().equals(context.getText(R.string.all).toString()))
                    {
                        result.add(dataPositions.get(j));
                    }
                }
                break;
            }
            else
            if (fragmentPositions.get(i).getCheckBox().isChecked())
            {
                for (int j = 0; j < dataPositions.size(); j++)
                {
                    if (dataPositions.get(j).getPosition().equals(fragmentPositions.get(i).getCheckBox().getText()))
                    {
                        result.add(dataPositions.get(j));
                    }
                }
            }
        }
        return result;
    }

    class getDataMaps extends AsyncTask<Void, Void, Void>
    {
        private List<DataPosition> positions;
        private List<DataMaps> dataMaps = new ArrayList<>();
        private String path;

        public getDataMaps(List<DataPosition> positions, String path)
        {
            dialogShow();
            this.positions = positions;
            this.path = path;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                ConstantUrl constantUrl = new ConstantUrl();
                for (int i = 0; i < positions.size(); i++)
                {
                    String url = constantUrl.getUrlTablePosition(positions.get(i).getName_table());
                    dataMaps.addAll(jsonParser.parseMaps(url, positions.get(i).getPosition(), positions.get(i).getName_table()));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    if (dataMaps.size() > 0)
                    {
                        FragmentSettings fragmentSettings = activity.getFragmentSettings();
                        String enterprise = fragmentSettings.getEnterprise();
                        String headText = head.getText().toString();
                        String name_file = name_doc.getText().toString();
                        String name = user.get(0).getName();
                        String surname = user.get(0).getSurname();
                        String surname_father = user.get(0).getSurname_father();
                        String manager = getNameMan(name, surname, surname_father);
                        String position = getPosition(positions);
                        String textReport = getTextReportText(positions, dataMaps);
                        String textDescription = getTextReportDescription(positions, dataMaps);

                        ConstantUrl constantUrl = new ConstantUrl();
                        try
                        {
                            String url = constantUrl.getUrlReport();
                            String params = constantUrl.getUrlReportParameter(textReport, textDescription,
                                    enterprise, headText, manager, position, name_file, path);
                            CreateReport createReport = new CreateReport(url, params);
                            createReport.execute();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        showToastMessage(context.getText(R.string.not_data).toString());
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    class CreateReport extends AsyncTask<Void, Void, Void>
    {
        String url;
        String params;
        String name_file;

        public CreateReport(String url, String params)
        {
            this.params = params;
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                name_file = jsonParser.getCreateReport(url, this.params);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            final ConstantUrl constantUrl = new ConstantUrl();
            try
            {
                String urlDownload = constantUrl.getUrlDownloadReport(name_file);
                RxDownloader.getInstance(context)
                        .download(urlDownload, name_file, "text/csv")
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted()
                            {
                                String url = constantUrl.getDeleteReport(name_file);
                                DeleteReport deleteReport = new DeleteReport(url);
                                deleteReport.execute();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(String s) {
                            }
                        });
            }
            catch (Exception e)
            {}
        }
    }

    class DeleteReport extends AsyncTask<Void, Void, Void>
    {
        String url;
        boolean state;

        public DeleteReport(String url)
        {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                state = jsonParser.getDeleteReport(url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            dialogDismiss();
        }
    }

    private String getTextReportDescription(List<DataPosition> positions, List<DataMaps> dataMap)
    {
        String text = "";
        String position = context.getText(R.string.positions).toString().replace(":", " - ");
        for (int i = 0; i < positions.size(); i++)
        {
            String positionItem = positions.get(i).getPosition();
            if (text.equals(""))
            {
                text = position + "\""+ positionItem + "\":";
            }
            else
            {
                text = text + "\n" + position + "\""+ positionItem + "\":";
            }
            List<DataMaps> dataMapPosition = getDataMapPosition(dataMap, positionItem);

            List<DataMaps> dataMapsCompleteNormal = getDataReportComplete(dataMapPosition, "normal");
            List<DataMaps> dataMapsCompleteLightweight = getDataReportComplete(dataMapPosition, "lightweight");
            List<DataMaps> dataMapsCompleteLight = getDataReportComplete(dataMapPosition, "light");
            List<DataMaps> dataMapsStitchedNormal = getDataReportStitched(dataMapPosition, "normal");
            List<DataMaps> dataMapsStitchedLightweight = getDataReportStitched(dataMapPosition, "lightweight");
            List<DataMaps> dataMapsStitchedLight = getDataReportStitched(dataMapPosition, "light");

            List<DataMaps> dataMaps = getDataMapsDescription(dataMapsCompleteNormal, dataMapsCompleteLightweight, dataMapsCompleteLight,
                    dataMapsStitchedNormal, dataMapsStitchedLightweight, dataMapsStitchedLight);

            text = text + "\n" + getTextDescription(dataMaps);
        }
        return text;
    }

    private String getTextDescription(List<DataMaps> dataMaps)
    {
        String code = context.getText(R.string.code_report).toString();
        String general_type = context.getText(R.string.general_type).toString().toLowerCase();
        String relative_type = context.getText(R.string.relative_type).toString().toLowerCase();
        String description = context.getText(R.string.description).toString().toLowerCase();
        String rez = "";
        for (int i = 0; i < dataMaps.size(); i++)
        {
            rez = rez + "\n" + code + " - " + dataMaps.get(i).getCode().replace(",", ".") + ", " + general_type + " - "
                    + "\"" + dataMaps.get(i).getGeneral() + "\"" + ", " + relative_type + " - "
                    + "\"" + dataMaps.get(i).getRelative() + "\"" + ", " + description + " - " + "\"" + dataMaps.get(i).getDescription() + "\".";
        }
        return rez;
    }

    private List<DataMaps> getDataMapsDescription(List<DataMaps> dataMapsCompleteNormal, List<DataMaps> dataMapsCompleteLightweight,
                                                  List<DataMaps> dataMapsCompleteLight, List<DataMaps> dataMapsStitchedNormal,
                                                  List<DataMaps> dataMapsStitchedLightweight, List<DataMaps> dataMapsStitchedLight)
    {
        List<DataMaps> rez = new ArrayList<>();
        rez = getDataMapDescription(rez, dataMapsCompleteNormal);
        rez = getDataMapDescription(rez, dataMapsCompleteLightweight);
        rez = getDataMapDescription(rez, dataMapsCompleteLight);
        rez = getDataMapDescription(rez, dataMapsStitchedNormal);
        rez = getDataMapDescription(rez, dataMapsStitchedLightweight);
        rez = getDataMapDescription(rez, dataMapsStitchedLight);
        return rez;
    }

    private List<DataMaps> getDataMapDescription(List<DataMaps> rez, List<DataMaps> data)
    {
        List<DataMaps> rezNew = new ArrayList<>();
        rezNew.addAll(rez);
        for (int i = 0; i < data.size(); i++)
        {
            String positionData = data.get(i).getPosition();
            String descriptionData = data.get(i).getDescription();
            boolean state = false;
            for (int j = 0; j < rez.size(); j++)
            {
                String positionRez = rez.get(j).getPosition();
                String descriptionRez = rez.get(j).getDescription();
                if (positionData.equals(positionRez) & descriptionData.equals(descriptionRez))
                {
                    state = true;
                }
            }
            if (!state)
            {
                rezNew.add(data.get(i));
            }
        }
        return rezNew;
    }

    private String getTextReportText(List<DataPosition> positions, List<DataMaps> dataMap)
    {
        String text = "";
        String position = context.getText(R.string.positions).toString().replace(":", " - ");

        for (int i = 0; i < positions.size(); i++)
        {
            String positionItem = positions.get(i).getPosition();
            if (text.equals(""))
            {
                text = position + "\""+ positionItem + "\":";
            }
            else
            {
                text = text + "\n" + position + "\""+ positionItem + "\":";
            }
            List<DataMaps> dataMapPosition = getDataMapPosition(dataMap, positionItem);

            List<DataMaps> dataMapsCompleteNormal = getDataReportComplete(dataMapPosition, "normal");
            List<DataMaps> dataMapsCompleteLightweight = getDataReportComplete(dataMapPosition, "lightweight");
            List<DataMaps> dataMapsCompleteLight = getDataReportComplete(dataMapPosition, "light");
            List<DataMaps> dataMapsStitchedNormal = getDataReportStitched(dataMapPosition, "normal");
            List<DataMaps> dataMapsStitchedLightweight = getDataReportStitched(dataMapPosition, "lightweight");
            List<DataMaps> dataMapsStitchedLight = getDataReportStitched(dataMapPosition, "light");

            text = text + "\n" + getTextReport(dataMapsCompleteNormal, dataMapsCompleteLightweight, dataMapsCompleteLight,
                    dataMapsStitchedNormal, dataMapsStitchedLightweight, dataMapsStitchedLight);

        }
        return text;
    }

    private List<DataMaps> getDataMapPosition(List<DataMaps> dataMap, String position)
    {
        List<DataMaps> rez = new ArrayList<>();
        for (int i = 0; i < dataMap.size(); i++)
        {
            if (position.equals(dataMap.get(i).getPosition()))
            {
                rez.add(dataMap.get(i));
            }
        }
        return rez;
    }

    private String getTextReport(List<DataMaps> dataMapsCompleteNormal, List<DataMaps> dataMapsCompleteLightweight, List<DataMaps> dataMapsCompleteLight,
                                 List<DataMaps> dataMapsStitchedNormal, List<DataMaps> dataMapsStitchedLightweight, List<DataMaps> dataMapsStitchedLight)
    {
        String completed = context.getText(R.string.complated).toString();
        String not_completed = context.getText(R.string.not_complated).toString();
        String stitched = context.getText(R.string.stitched_repport).toString();
        String days = context.getText(R.string.days_report).toString();
        String date_completed = context.getText(R.string.date_completed).toString();
        String regime = context.getText(R.string.regime).toString().replace(":", " -");
        String normal = context.getText(R.string.normal_regime).toString().toLowerCase();
        String lightweight = context.getText(R.string.lightweight_regime).toString().toLowerCase();
        String light = context.getText(R.string.light_regime).toString().toLowerCase();
        String code = context.getText(R.string.code_report).toString().toLowerCase();
        String comment_manager = context.getText(R.string.comment_manager_report).toString().toLowerCase();
        String comment_performer = context.getText(R.string.comment_performer_report).toString().toLowerCase();
        String who_complete = context.getText(R.string.who_complete).toString().toLowerCase();

        String rezCompetedNormal = getText(dataMapsCompleteNormal, completed, stitched, code, days, date_completed, comment_manager, comment_performer, who_complete);
        String rezCompetedLightweight = getText(dataMapsCompleteLightweight, completed, stitched, code, days, date_completed, comment_manager, comment_performer, who_complete);
        String rezCompetedLight = getText(dataMapsCompleteLight, completed, stitched, code, days, date_completed, comment_manager, comment_performer, who_complete);

        String rezStitchedNormal = getText(dataMapsStitchedNormal, not_completed, stitched, code, days, date_completed, comment_manager, comment_performer, who_complete);
        String rezStitchedLightweight = getText(dataMapsStitchedLightweight, not_completed, stitched, code, days, date_completed, comment_manager, comment_performer, who_complete);
        String rezStitchedLight = getText(dataMapsStitchedLight, not_completed, stitched, code, days, date_completed, comment_manager, comment_performer, who_complete);

        return getText(regime, normal, lightweight, light, rezCompetedNormal, rezCompetedLightweight, rezCompetedLight, rezStitchedNormal, rezStitchedLightweight, rezStitchedLight);
    }

    private String getText(String regime, String normal, String lightweight, String light, String rezCompetedNormal, String rezCompetedLightweight, String rezCompetedLight,
                           String rezStitchedNormal, String rezStitchedLightweight, String rezStitchedLight)
    {
        String rezCompeted = "";
        String regimeNormal = "\n" + regime + " " + normal + ":\n";
        String regimeLightweight = "\n" + regime + " " + lightweight + ":\n";
        String regimeLight = "\n" + regime + " " + light + ":\n";
        if (regime_all.isChecked())
        {
            rezCompeted = regimeNormal + rezCompetedNormal + "\n" + rezStitchedNormal +
                    regimeLightweight + rezCompetedLightweight + "\n" + rezStitchedLightweight +
                    regimeLight + rezCompetedLight+ "\n" + rezStitchedLight;
        }
        else
        if (regime_normal.isChecked())
        {
            rezCompeted = regimeNormal + rezCompetedNormal + "\n" + rezStitchedNormal;
        }
        else
        if (regime_lightweight.isChecked())
        {
            rezCompeted = regimeLightweight + rezCompetedLightweight + "\n" + rezStitchedLightweight;
        }
        else
        if (regime_light.isChecked())
        {
            rezCompeted = regimeLight + rezCompetedLight + "\n" + rezStitchedLight;
        }
        return rezCompeted;
    }

    private String getText(List<DataMaps> data, String completed, String stitched, String code,
                           String days, String date_completed, String comment_manager, String comment_performer, String who_complete)
    {
        String rez = "";
        if (data.size() > 0)
        {
            rez = completed;
        }
        for (int i = 0; i < data.size(); i++)
        {
            String codeItem = data.get(i).getCode();
            String stitchedItem = data.get(i).getStitched();
            String dateItem = data.get(i).getDate();
            String comment_managerItem = data.get(i).getComment_manager();
            String comment_performerItem = data.get(i).getComment_performer();
            String name = data.get(i).getName();
            String surname = data.get(i).getSurname();
            String surname_father = data.get(i).getSurname_father();
            String nameMan = getNameMan(name, surname, surname_father);
            if (i != 0)
            {
                rez = rez + "; ";
            }
            else
            {
                rez = rez + " ";
            }
            rez = rez + code + " - " + codeItem.replace(",", ".") + ", " + date_completed + " - " + dateItem;
            if (stitchedItem.length() != 0)
            {
                if (stitchedItem.length() > 0)
                {
                    rez = rez + ", " + stitched + " - " + stitchedItem + " " + days;
                }
            }
            if (who_made.isChecked())
            {
                if (nameMan.length() > 0)
                {
                    rez = rez + ", " + who_complete + " - " + nameMan;
                }
            }
            if (all.isChecked())
            {
                if (comment_managerItem.length() > 0)
                {
                    rez = rez + ", " + comment_manager + " - " +  "\"" + comment_managerItem + "\"";
                }
                if (comment_performerItem.length() > 0)
                {
                    rez = rez + ", " + comment_performer + " - " +  "\"" + comment_performer + "\"";
                }
            }
            else
            if (manager.isChecked())
            {
                if (comment_managerItem.length() > 0)
                {
                    rez = rez + ", " + comment_manager + " - " +  "\"" + comment_managerItem + "\"";
                }
            }
            else
            if (performer.isChecked())
            {
                if (comment_performerItem.length() > 0)
                {
                    rez = rez + ", " + comment_performer + " - " +  "\"" + comment_performer + "\"";
                }
            }
            if (i == data.size() - 1)
            {
                rez = rez + ".";
            }
        }
        return rez;
    }

    private List<DataMaps> getDataReportStitched(List<DataMaps> dataMaps, String type)
    {
        List<DataMaps> data = new ArrayList<>();
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.UK);
            Date convertedDateBy = dateFormat.parse(with.getText().toString());
            Calendar calendarBy = Calendar.getInstance();
            calendarBy.setTime(convertedDateBy);
            calendarBy.set(Calendar.HOUR_OF_DAY, 0);
            calendarBy.set(Calendar.MINUTE, 0);
            calendarBy.set(Calendar.SECOND, 0);
            calendarBy.set(Calendar.MILLISECOND, 0);

            for (int i = 0; i < dataMaps.size(); i++)
            {
                String dateText = dataMaps.get(i).getDate();
                String normal = dataMaps.get(i).getNormal().replace(",", ".");
                String lightweight = dataMaps.get(i).getLightweight().replace(",", ".");
                String light = dataMaps.get(i).getLight().replace(",", ".");
                float period = 0;
                if (type.equals("normal"))
                {
                    if (normal.length() > 0)
                    {
                        try
                        {
                            period = Float.parseFloat(normal);
                        }
                        catch (Exception e)
                        {}
                    }
                }
                if (type.equals("lightweight"))
                {
                    if (lightweight.length() > 0)
                    {
                        try
                        {
                            period = Float.parseFloat(lightweight);
                        }
                        catch (Exception e)
                        {}
                    }
                }
                if (type.equals("light"))
                {
                    if (lightweight.length() > 0)
                    {
                        try
                        {
                            period = Float.parseFloat(light);
                        }
                        catch (Exception e)
                        {}
                    }
                }
                int day_period = 0;
                if (period != 0)
                {
                    float a = 0;
                    while (true)
                    {
                        a = a + period;
                        day_period++;
                        if (a >= 1)
                        {
                            break;
                        }
                    }
                    try
                    {
                        if (dateText.length() > 0)
                        {
                            Date convertedDate = dateFormat.parse(dateText);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(convertedDate);
                            calendar.add(Calendar.DATE, day_period);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            int state_by = calendar.compareTo(calendarBy);

                            long milliseconds = calendarBy.getTime().getTime() - calendar.getTime().getTime();
                            int day_stitched = (int) (milliseconds / (24 * 60 * 60 * 1000));

                            if (state_by == 0 || state_by == - 1)
                            {
                                DataMaps dataMap = dataMaps.get(i);
                                dataMap.setStitched(day_stitched + "");
                                data.add(dataMap);
                            }
                        }
                        else
                        {
                            DataMaps dataMap = dataMaps.get(i);
                            data.add(dataMap);
                        }
                    }
                    catch (Exception e)
                    {}
                }
            }
        }
        catch (Exception e)
        {}
        return data;
    }

    private List<DataMaps> getDataReportComplete(List<DataMaps> dataMaps, String type)
    {
        List<DataMaps> data = new ArrayList<>();
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.UK);
            Date convertedDateWith = dateFormat.parse(with.getText().toString());
            Calendar calendarWith = Calendar.getInstance();
            calendarWith.setTime(convertedDateWith);
            calendarWith.set(Calendar.HOUR_OF_DAY, 0);
            calendarWith.set(Calendar.MINUTE, 0);
            calendarWith.set(Calendar.SECOND, 0);
            calendarWith.set(Calendar.MILLISECOND, 0);

            Date convertedDateBy = dateFormat.parse(with.getText().toString());
            Calendar calendarBy = Calendar.getInstance();
            calendarBy.setTime(convertedDateBy);
            calendarBy.set(Calendar.HOUR_OF_DAY, 0);
            calendarBy.set(Calendar.MINUTE, 0);
            calendarBy.set(Calendar.SECOND, 0);
            calendarBy.set(Calendar.MILLISECOND, 0);

            for (int i = 0; i < dataMaps.size(); i++)
            {
                try
                {
                    String dateText = dataMaps.get(i).getDate();
                    if (dateText.length() > 0)
                    {
                        Date convertedDate = dateFormat.parse(dateText);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(convertedDate);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        int state_with = calendar.compareTo(calendarWith);
                        int state_by = calendar.compareTo(calendarBy);

                        if (state_with == 0 || state_with == 1)
                        {
                            if (state_by == 0 || state_by == - 1)
                            {
                                if (type.equals("normal"))
                                {
                                    if (dataMaps.get(i).getNormal().length() > 0)
                                    {
                                        data.add(dataMaps.get(i));
                                    }
                                }
                                if (type.equals("lightweight"))
                                {
                                    if (dataMaps.get(i).getLightweight().length() > 0)
                                    {
                                        data.add(dataMaps.get(i));
                                    }
                                }
                                if (type.equals("light"))
                                {
                                    if (dataMaps.get(i).getLight().length() > 0)
                                    {
                                        data.add(dataMaps.get(i));
                                    }
                                }
                            }
                        }
                    }
                }
                catch (Exception e)
                {}
            }
        }
        catch (Exception e)
        {}
        return data;
    }

    private String getPosition(List<DataPosition> positions)
    {
        String result = "";
        for (int i = 0; i < positions.size(); i++)
        {
            if (i == 0)
            {
                result = result + "\"" + positions.get(i).getPosition()+ "\"";
            }
            else
            {
                result = result + ", "+ "\"" + positions.get(i).getPosition()+ "\"";
            }
        }
        return result;
    }

    private String getNameMan(String name, String surname, String surname_father)
    {
        String result;
        try
        {
            result = surname + " " + name.substring(0, 1) + "." + surname_father.substring(0, 1)+ ".";
        }
        catch (Exception e)
        {
            return "";
        }
        return result;
    }

    public RelativeLayout getProgress() {
        return progress;
    }

    public LinearLayout getFragment_container() {
        return fragment_container;
    }

    public TextView getHint() {
        return hint;
    }

    public void update(View progress, LinearLayout fragment_container, TextView hint)
    {
        String login = user.get(0).getLogin();
        Position position = new Position(url.getUrlGetPosition(login), progress, fragment_container, hint);
        position.execute();
    }

    private List<FragmentPosition> getFragmentsPosition()
    {
        List<Fragment> fragments = getFragmentManager().getFragments();
        List<FragmentPosition> fragmentPositions = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++)
        {
            try
            {
                FragmentPosition fragmentPosition = (FragmentPosition) fragments.get(i);
                fragmentPositions.add(fragmentPosition);

            }
            catch (Exception e)
            {}
        }
        return fragmentPositions;
    }

    class Position extends AsyncTask<Void, Void, Void> {

        private String url;
        private View progress;
        private LinearLayout fragment_container;
        private TextView hint;

        public Position(String url, View progress, LinearLayout fragment_container, TextView hint)
        {
            List<FragmentPosition> fragmentPositions = getFragmentsPosition();
            transaction = getFragmentManager().beginTransaction();
            for (int i = 0; i < fragmentPositions.size(); i++)
            {
                transaction.remove(fragmentPositions.get(i));
                transaction.commitNow();
            }
            this.url = url;
            this.progress = progress;
            this.fragment_container = fragment_container;
            this.hint = hint;
            progress.setVisibility(View.VISIBLE);
            fragment_container.setVisibility(View.GONE);
            hint.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                dataPosition = jsonParser.parseGetPosition(url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            positionsParcelables = new ArrayList<>();
            for (int i = 0; i < dataPosition.size(); i++)
            {
                positionsParcelables.add(new PositionsParcelable(dataPosition.get(i).getLogin(),
                        dataPosition.get(i).getPosition(), dataPosition.get(i).getCode(), dataPosition.get(i).getName_table()));
            }
            createFragment(progress, fragment_container, hint);
        }
    }

    private void createFragment(View progress, LinearLayout fragment_container, TextView hint)
    {
        progress.setVisibility(View.GONE);
        HashMap<String, String> position = new LinkedHashMap<>();
        for (int i = 0;i < dataPosition.size(); i++)
        {
            if (i == 0)
            {
                position.put(context.getText(R.string.all).toString(), "");
            }
            position.put(dataPosition.get(i).getPosition(), dataPosition.get(i).getName_table());
        }
        if (position.size() > 1)
        {
            fragment_container.setVisibility(View.VISIBLE);
            for (Map.Entry entry : position.entrySet())
            {
                transaction = getFragmentManager().beginTransaction();
                FragmentPosition fragmentPosition = new FragmentPosition().newInstance(entry.getKey().toString(), entry.getValue().toString());
                transaction.add(R.id.fragment_container_enterprise, fragmentPosition);
                transaction.commit();
            }
        }
        else
        {
            fragment_container.setVisibility(View.GONE);
            hint.setVisibility(View.VISIBLE);
        }
    }

    Pair<Boolean, SublimeOptions> getOptions()
    {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;
        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        options.setDisplayOptions(displayOptions);
        options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);
        return new Pair<>(displayOptions != 0 ? Boolean.TRUE : Boolean.FALSE, options);
    }

    private String getDate()
    {
        Date date = new Date();
        Calendar calendar_real = Calendar.getInstance();
        calendar_real.setTime(date);
        int year = calendar_real.get(Calendar.YEAR);
        int month = calendar_real.get(Calendar.MONTH) + 1;
        int days = calendar_real.get(Calendar.DAY_OF_MONTH);
        String monthReal = month + "";
        if (monthReal.length() == 1)
        {
            monthReal = "0" + month;
        }
        String daysReal = days + "";
        if (daysReal.length() == 1)
        {
            daysReal = "0" + days;
        }
        return daysReal + "." + monthReal + "." + year;
    }

}