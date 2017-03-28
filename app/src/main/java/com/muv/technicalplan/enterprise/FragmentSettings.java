package com.muv.technicalplan.enterprise;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.rxdownloader.RxDownloader;
import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.data.DataMap;
import com.muv.technicalplan.data.DataPosition;
import com.muv.technicalplan.data.DataUser;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

public class FragmentSettings  extends AbstractTabFragment implements View.OnClickListener
{
    private static final int LAYOUT = R.layout.fragment_settings;
    private FragmentTransaction transaction;
    private Button add;
    private Button load_sample;
    private Button load_template;
    private EditText enterprise;
    private RelativeLayout progress;
    private TextView hint;
    private List<DataPosition> dataPosition;
    private JsonParser jsonParser = new JsonParser();
    private List<DataUser> user = DataUser.listAll(DataUser.class);
    private FragmentSettings fragmentSettings;
    private int count_created;
    private List<DataMap> dataMapsDelete = new ArrayList<>();
    private EnterpriseActivity activity;

    public List<DataMap> getDataMapsDelete() {
        return dataMapsDelete;
    }

    public void setDataMapsDelete(DataMap dataMaps) {
        this.dataMapsDelete.add(dataMaps);
    }

    public void setDataMapsDelete(List<DataMap> dataMapsDelete) {
        this.dataMapsDelete = dataMapsDelete;
    }

    public int getCount_created() {
        return count_created;
    }

    public static FragmentSettings getInstance(Context context, String title, EnterpriseActivity activity)
    {
        Bundle args = new Bundle();
        FragmentSettings fragment = new FragmentSettings();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(title);
        fragment.setEnterpriseActivity(activity);
        return fragment;
    }

    public EnterpriseActivity getEnterpriseActivity() {
        return activity;
    }

    public void setEnterpriseActivity(EnterpriseActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        fragmentSettings = this;
        add = (Button)view.findViewById(R.id.add_position);
        add.setOnClickListener(this);
        load_sample = (Button)view.findViewById(R.id.load_sample);
        load_sample.setOnClickListener(this);
        load_template = (Button)view.findViewById(R.id.load_template);
        load_template.setOnClickListener(this);
        enterprise = (EditText)view.findViewById(R.id.enterprise_name);
        progress = (RelativeLayout)view.findViewById(R.id.progress_position_enterprise);
        hint = (TextView)view.findViewById(R.id.position_hint_enterprise);
        ConstantUrl constantUrl = new ConstantUrl();
        String url = constantUrl.getUrlGetPosition(user.get(0).getLogin());
        Position position = new Position(url, progress, hint);
        position.execute();
        String enterpriseText = user.get(0).getEnterprise();
        if (enterpriseText.equals("false"))
        {
            enterpriseText = "";
        }
        enterprise.setText(enterpriseText);
        getChangeKeyboard();
        return view;
    }

    private void getChangeKeyboard()
    {
        enterprise.setCursorVisible(false);
        KeyboardVisibilityEvent.setEventListener(getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen)
                        {
                            enterprise.setCursorVisible(true);
                        }
                        else
                        {
                            enterprise.setCursorVisible(false);
                        }
                    }
                });
    }

    public void update()
    {
        view.invalidate();
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.add_position:
                hint.setVisibility(View.GONE);
                List<Fragment> fragments =  getRealFragments();
                Fragment fragment = fragments.get(fragments.size() - 1);
                try
                {
                    FragmentPositionMap fragmentPositionMap = (FragmentPositionMap) fragment;
                    if (!fragmentPositionMap.getPosition().equals("") & fragmentPositionMap.getPath() != null)
                    {
                        activity.setUpdate(false);
                        createFragment("ADD", "", null, null, null);
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.hint_add),
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                catch (Exception e)
                {
                    createFragment("ADD", "", null, null, null);
                }
                break;

            case R.id.load_sample:
                    downloadFile("sample.csv");
                break;

            case R.id.load_template:
                    downloadFile("template.csv");
                    break;

            default:
                break;
        }
    }

    private String download_name;

    public void downloadFile(final String name)
    {
        if (isStoragePermissionGranted())
        {
            ConstantUrl constantUrl = new ConstantUrl();
            String url = "";
            try
            {
                url = constantUrl.getUrlDownloadCsv(name);
            }
            catch (Exception e)
            {}
            RxDownloader.getInstance(context)
                    .download(url, name)
                    .subscribeOn(AndroidSchedulers.mainThread());

        }
        else
        {
            download_name = name;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0)
        {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                downloadFile(download_name);
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

    public void createFragment(String TAG, String position, String path,  String code, String name_table)
    {
        count_created++;
        transaction = getFragmentManager().beginTransaction();
        FragmentPositionMap fragmentPositionMap = new FragmentPositionMap().newInstance(context, this, TAG, position, path, code, name_table);
        transaction.add(R.id.fragment_container, fragmentPositionMap);
        transaction.commit();
    }

    public String getEnterprise()
    {
        return enterprise.getText().toString();
    }

    public List<Fragment> getRealFragments()
    {
        List<Fragment> fragments = getFragmentManager().getFragments();
        List<Fragment> fragmentsReal = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++)
        {
            if (fragments.get(i) != null)
            {
                fragmentsReal.add(fragments.get(i));
            }
        }
        return  fragmentsReal;
    }

    class Position extends AsyncTask<Void, Void, Void> {

        private String url;
        private View progress;
        private TextView hint;

        public Position(String url, View progress, TextView hint)
        {
            this.url = url;
            this.progress = progress;
            this.hint = hint;
            progress.setVisibility(View.VISIBLE);
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
            if (dataPosition != null)
            {
                if (dataPosition.size() > 0)
                {
                    progress.setVisibility(View.GONE);
                    for (int i = 0; i < dataPosition.size(); i++)
                    {
                        createFragment("CREATED", dataPosition.get(i).getPosition(), "",
                                dataPosition.get(i).getCode(), dataPosition.get(i).getName_table());
                    }
                }
                else
                {
                    progress.setVisibility(View.GONE);
                    hint.setVisibility(View.VISIBLE);
                }
            }

        }
    }
}
