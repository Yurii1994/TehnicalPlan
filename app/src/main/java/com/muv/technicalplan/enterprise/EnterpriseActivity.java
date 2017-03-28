package com.muv.technicalplan.enterprise;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.DialogFragmentProgress;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.UploadMultipart;
import com.muv.technicalplan.data.DataMap;
import com.muv.technicalplan.data.DataPosition;
import com.muv.technicalplan.data.DataUser;

import java.util.ArrayList;
import java.util.List;

public class EnterpriseActivity extends AppCompatActivity
{
    private static final int LAYOUT = R.layout.activity_enterprise;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsPagerFragmentAdapterEnterprise adapter;
    private MenuItem save;
    private JsonParser jsonParser = new JsonParser();
    private List<DataUser> user = DataUser.listAll(DataUser.class);
    private boolean update;
    private DialogFragmentProgress dialog;
    private final String UPDATE = "com.muv.action.UPDATE";
    private BroadcastReceiver broadcastReceiver;
    private List<FragmentPositionMap> fragmentPositionMaps;
    private Toast toast;

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public DialogFragmentProgress getDialog() {
        return dialog;
    }

    public void dialogShow()
    {
        if (dialog != null)
        {
            if (!dialog.isShowing())
            {

                dialog.show(getFragmentManager(), "dialogFragment");
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
        initTabs();
        dialog = new DialogFragmentProgress().newInstance(getResources().getString(R.string.save));
    }

    private void initToolbar()
    {
        if (toolbar == null)
        {
            toolbar = (Toolbar) findViewById(R.id.toolbar_enterprise);
            toolbar.setTitle(R.string.menu_enterprise);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null)
            {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.inflateMenu(R.menu.menu_enterprise);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.menu_enterprise_save:
                            Save();
                            break;
                    }
                    return true;
                }
            });
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter filter = new IntentFilter(UPDATE);
        broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String update = intent.getStringExtra("Update");
                if (update.equals("position"))
                {
                    UpdatePositionInFragment updatePositionInFragment = new UpdatePositionInFragment(fragmentPositionMaps);
                    updatePositionInFragment.execute();
                }
                else
                if (update.equals("show_dialog"))
                {
                    dialogShow();
                }
                else
                if (update.equals("dismiss_dialog"))
                {
                    dialogDismiss();
                }
            }
        };
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    class UpdatePositionInFragment extends AsyncTask<Void, Void, Void> {

        private List<DataPosition> dataPosition;
        private List<FragmentPositionMap> fragmentPositionMaps;

        public UpdatePositionInFragment(List<FragmentPositionMap> fragmentPositionMaps)
        {
            this.fragmentPositionMaps = fragmentPositionMaps;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                ConstantUrl constantUrl = new ConstantUrl();
                List<DataUser> user = DataUser.listAll(DataUser.class);
                String url = constantUrl.getUrlGetPosition(user.get(0).getLogin());
                JsonParser jsonParser = new JsonParser();
                dataPosition = jsonParser.parseGetPosition(url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                dialogDismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            for (int i = 0; i < fragmentPositionMaps.size(); i++)
            {
                try
                {
                    fragmentPositionMaps.get(i).setCode(dataPosition.get(i).getCode());
                    fragmentPositionMaps.get(i).setName_table(dataPosition.get(i).getName_table());
                    fragmentPositionMaps.get(i).setPath("");
                    fragmentPositionMaps.get(i).setPositionOld(dataPosition.get(i).getPosition());
                    fragmentPositionMaps.get(i).setTAG("CREATED");
                }
                catch (Exception e)
                {}
            }
            dialogDismiss();
            setUpdate(true);
            MassageSaved();
        }
    }

    private void Save()
    {
        List<Fragment> fragments =  adapter.getFragmentSettings().getRealFragments();
        fragmentPositionMaps = getFragmentPosition(fragments);
        List<FragmentPositionMap> fragmentPositionMapsForUpdate = getMapFragmentForUpdate(fragmentPositionMaps);
        ConstantUrl constantUrl = new ConstantUrl();
        List<DataMap> dataMaps = getDataMap(fragmentPositionMapsForUpdate);
        List<DataUser> user = DataUser.listAll(DataUser.class);
        if (dataMaps.size() > 0)
        {
            for (int i = 0; i < dataMaps.size(); i++)
            {
                try
                {
                    if (dataMaps.get(i).getTAG().equals("ADD"))
                    {
                        if (!dataMaps.get(i).getEnterprise().equals(""))
                        {
                            String url = constantUrl.setUrlPositionMap(user.get(0).getLogin(),
                                    dataMaps.get(i).getEnterprise(), dataMaps.get(i).getPosition());
                            UploadMultipart uploadMultipart = new UploadMultipart();
                            uploadMultipart.uploadSCV(getApplicationContext(), dataMaps.get(i).getPath(), url);
                            fragmentPositionMapsForUpdate.get(i).setPath("");
                            fragmentPositionMapsForUpdate.get(i).setPositionOld(dataMaps.get(i).getPosition());
                        }
                        else
                        {
                            showToastMessage(getResources().getText(R.string.not_enterprise_created).toString());
                        }
                    }
                    else
                    {
                        if (!dataMaps.get(i).getPath().equals(""))
                        {
                            if (!dataMaps.get(i).getEnterprise().equals(""))
                            {
                                String url = constantUrl.setUrlPositionMapRefresh(user.get(0).getLogin(),
                                        dataMaps.get(i).getEnterprise(), dataMaps.get(i).getPosition(),
                                        dataMaps.get(i).getCode(), dataMaps.get(i).getName_table(), true);
                                UploadMultipart uploadMultipart = new UploadMultipart();
                                uploadMultipart.uploadSCV(getApplicationContext(), dataMaps.get(i).getPath(), url);
                                fragmentPositionMapsForUpdate.get(i).setPath("");
                                fragmentPositionMapsForUpdate.get(i).setPositionOld(dataMaps.get(i).getPosition());
                            }
                            else
                            {
                                showToastMessage(getResources().getText(R.string.not_enterprise_created).toString());
                            }

                        }
                        else
                        {
                            if (!dataMaps.get(i).getEnterprise().equals(""))
                            {
                                String url = constantUrl.setUrlPositionMapRefresh(user.get(0).getLogin(),
                                        dataMaps.get(i).getEnterprise(), dataMaps.get(i).getPosition(),
                                        dataMaps.get(i).getCode(), dataMaps.get(i).getName_table(), false);
                                UpdatePosition updatePosition = new UpdatePosition(url);
                                updatePosition.execute();
                                fragmentPositionMapsForUpdate.get(i).setPath("");
                                fragmentPositionMapsForUpdate.get(i).setPositionOld(dataMaps.get(i).getPosition());
                            }
                            else
                            {
                                showToastMessage(getResources().getText(R.string.not_enterprise_created).toString());
                            }
                        }
                    }
                }
                catch (Exception e)
                {}
            }
        }
        else
        {
            try
            {
                if (!adapter.getFragmentSettings().getEnterprise().equals(""))
                {
                    if (!user.get(0).getEnterprise().equals(adapter.getFragmentSettings().getEnterprise()))
                    {
                        UpdateEnterprise updateEnterprise = new UpdateEnterprise(constantUrl.getUrlUpdateEnterprise(
                                user.get(0).getLogin(), adapter.getFragmentSettings().getEnterprise()));
                        updateEnterprise.execute();
                    }
                }
                else
                {
                    showToastMessage(getResources().getText(R.string.not_enterprise_created).toString());
                }
            }
            catch (Exception e)
            {}
        }
        List<DataMap> dataMapsDelete = adapter.getFragmentSettings().getDataMapsDelete();
        adapter.notifyDataSetChanged();
        if (dataMapsDelete.size() > 0)
        {
            Delete delete = new Delete(dataMapsDelete);
            delete.execute();
        }
    }

    private void getMassageSaveChange()
    {
        int count = adapter.getFragmentSettings().getCount_created();
        List<Fragment> fragments =  adapter.getFragmentSettings().getRealFragments();
        List<FragmentPositionMap> fragmentPositionMaps = getFragmentPosition(fragments);
        List<DataMap> dataMaps = getDataMap(fragmentPositionMaps);
        List<DataUser> user = DataUser.listAll(DataUser.class);
        if (user.get(0).getEnterprise() != null & !adapter.getFragmentSettings().getEnterprise().equals(""))
        {
            if (dataMaps.size() > 0 || !user.get(0).getEnterprise().equals(adapter.getFragmentSettings().getEnterprise())
                    || count != fragmentPositionMaps.size())
            {
                showToastMessage(getResources().getText(R.string.not_save).toString());
            }
        }
    }

    class Delete extends AsyncTask<Void, Integer, Void>
    {
        private List<DataMap> dataMapDelete;

        public Delete(List<DataMap> dataMapDelete)
        {
            this.dataMapDelete = dataMapDelete;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                dialogShow();
                for (int i = 0; i < dataMapDelete.size(); i++)
                {
                    ConstantUrl constantUrl = new ConstantUrl();
                    String url = constantUrl.getUrlRemovePosition(user.get(0).getLogin(),
                            dataMapDelete.get(i).getCode(), dataMapDelete.get(i).getName_table());
                    update = jsonParser.getUpdateEnterprise(url);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                dialogDismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            MassageSaved();
            adapter.getFragmentSettings().setDataMapsDelete(new ArrayList<DataMap>());
            dialogDismiss();
        }
    }

    class UpdatePosition extends AsyncTask<Void, Void, Void> {

        private String url;

        public UpdatePosition(String url)
        {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                dialogShow();
                update = jsonParser.getUpdateEnterprise(url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                dialogDismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            MassageSaved();
            dialogDismiss();
        }
    }

    public void MassageSaved()
    {
        if (update)
        {
            showToastMessage(getResources().getText(R.string.saved).toString());
        }
        else
        {
            showToastMessage(getResources().getText(R.string.error_save).toString());
        }
    }

    class UpdateEnterprise extends AsyncTask<Void, Void, Void> {

        private String url;

        public UpdateEnterprise(String url)
        {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                dialogShow();
                update = jsonParser.getUpdateEnterprise(url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                dialogDismiss();
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

    public List<DataMap> getDataMap(List<FragmentPositionMap> fragments)
    {
        List<DataMap> path = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++)
        {
            if (!fragments.get(i).getPosition().equals("") & fragments.get(i).getPath() != null)
            {
                if (!fragments.get(i).getPath().equals("") || !fragments.get(i).getPositionOld().equals(fragments.get(i).getPosition()))
                {
                    DataMap dataMap = new DataMap();
                    dataMap.setEnterprise(adapter.getFragmentSettings().getEnterprise());
                    dataMap.setPosition(fragments.get(i).getPosition());
                    dataMap.setPath(fragments.get(i).getPath());
                    dataMap.setTAG(fragments.get(i).getTAG());
                    dataMap.setCode(fragments.get(i).getCode());
                    dataMap.setName_table(fragments.get(i).getName_table());
                    path.add(dataMap);
                }
            }
        }
        return path;
    }

    public List<FragmentPositionMap> getMapFragmentForUpdate(List<FragmentPositionMap> fragments)
    {
        List<FragmentPositionMap> result = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++)
        {
            if (!fragments.get(i).getPosition().equals("") & fragments.get(i).getPath() != null)
            {
                if (!fragments.get(i).getPath().equals("") || !fragments.get(i).getPositionOld().equals(fragments.get(i).getPosition()))
                {
                    result.add(fragments.get(i));
                }
            }
        }
        return result;
    }

    public List<FragmentPositionMap> getFragmentPosition(List<Fragment> fragments)
    {
        List<FragmentPositionMap> fragmentPosition = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++)
        {
            try
            {
                FragmentPositionMap fragmentPositionMap = (FragmentPositionMap) fragments.get(i);
                fragmentPosition.add(fragmentPositionMap);
            }
            catch (Exception e)
            {}
        }
        return fragmentPosition;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_enterprise, menu);
        save = menu.findItem(R.id.menu_enterprise_save);
        save.setVisible(false);
        return true;
    }

    private void initTabs()
    {
        viewPager = (ViewPager)findViewById(R.id.view_pager_enterprise);
        adapter = new TabsPagerFragmentAdapterEnterprise(this, getSupportFragmentManager(), this);

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout)findViewById(R.id.tab_enterprise);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                if (position == 0)
                {
                    save.setVisible(false);
                    if (!update)
                    {
                        getMassageSaveChange();
                    }
                }
                else
                {
                    save.setVisible(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (update)
        {
            super.onBackPressed();
        }
        int count = adapter.getFragmentSettings().getCount_created();
        List<Fragment> fragments =  adapter.getFragmentSettings().getRealFragments();
        List<FragmentPositionMap> fragmentPositionMaps = getFragmentPosition(fragments);
        List<DataMap> dataMaps = getDataMap(fragmentPositionMaps);
        List<DataUser> user = DataUser.listAll(DataUser.class);
        if (user.get(0).getEnterprise() != null & !adapter.getFragmentSettings().getEnterprise().equals(""))
        {
            if ((dataMaps.size() > 0 || !user.get(0).getEnterprise().equals(adapter.getFragmentSettings().getEnterprise())
                    || count != fragmentPositionMaps.size()) & !update )
            {
                showToastMessage(getResources().getText(R.string.not_save).toString());
                update = true;
            }
            else
            {
                super.onBackPressed();
            }
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void showToastMessage(String a)
    {
        if (toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT);
        toast.show();
    }
}
