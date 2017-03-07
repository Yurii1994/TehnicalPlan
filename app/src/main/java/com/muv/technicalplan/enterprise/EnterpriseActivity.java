package com.muv.technicalplan.enterprise;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.UploadMultipart;
import com.muv.technicalplan.data.DataMap;
import com.muv.technicalplan.data.DataSearch;
import com.muv.technicalplan.data.DataUser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
        initTabs();
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
                            List<Fragment> fragments =  adapter.getFragmentSettings().getRealFragments();
                            List<FragmentPositionMap> fragmentPositionMaps = getDataMapFragment(getFragmentPosition(fragments));
                            ConstantUrl constantUrl = new ConstantUrl();
                            List<DataMap> dataMaps = getDataMap(fragmentPositionMaps);
                            List<DataUser> user = DataUser.listAll(DataUser.class);
                            System.out.println("EEE " + dataMaps);
                            if (dataMaps.size() > 0)
                            {
                                for (int i = 0; i < dataMaps.size(); i++)
                                {
                                    try
                                    {
                                        if (dataMaps.get(i).getTAG().equals("ADD"))
                                        {
                                            String url = constantUrl.setUrlPositionMap(user.get(0).getLogin(),
                                                    dataMaps.get(i).getEnterprise(), dataMaps.get(i).getPosition());
                                            UploadMultipart uploadMultipart = new UploadMultipart();
                                            uploadMultipart.uploadSCV(getApplicationContext(), dataMaps.get(i).getPath(), url);
                                            fragmentPositionMaps.get(i).setPath("");
                                            fragmentPositionMaps.get(i).setPositionOld(dataMaps.get(i).getPosition());
                                            update = true;
                                        }
                                        else
                                        {
                                            if (!dataMaps.get(i).getPath().equals(""))
                                            {
                                                String url = constantUrl.setUrlPositionMapRefresh(user.get(0).getLogin(),
                                                        dataMaps.get(i).getEnterprise(), dataMaps.get(i).getPosition(), dataMaps.get(i).getCode());
                                                UploadMultipart uploadMultipart = new UploadMultipart();
                                                uploadMultipart.uploadSCV(getApplicationContext(), dataMaps.get(i).getPath(), url);
                                                fragmentPositionMaps.get(i).setPath("");
                                                fragmentPositionMaps.get(i).setPositionOld(dataMaps.get(i).getPosition());
                                                update = true;
                                            }
                                            else
                                            {
                                                String url = constantUrl.setUrlPositionMapRefresh(user.get(0).getLogin(),
                                                        dataMaps.get(i).getEnterprise(), dataMaps.get(i).getPosition(), dataMaps.get(i).getCode());
                                                UpdatePosition updatePosition = new UpdatePosition(url);
                                                updatePosition.execute();
                                                fragmentPositionMaps.get(i).setPath("");
                                                fragmentPositionMaps.get(i).setPositionOld(dataMaps.get(i).getPosition());
                                                update = true;
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
                                    if (!user.get(0).getEnterprise().equals(adapter.getFragmentSettings().getEnterprise()))
                                    {
                                        UpdateEnterprise updateEnterprise = new UpdateEnterprise(constantUrl.getUrlUpdateEnterprise(
                                                user.get(0).getLogin(), adapter.getFragmentSettings().getEnterprise()));
                                        updateEnterprise.execute();
                                    }
                                }
                                catch (Exception e)
                                {}
                            }
                            List<DataMap> dataMapsDelete = adapter.getFragmentSettings().getDataMapsDelete();
                            if (dataMapsDelete.size() > 0)
                            {
                                Delete delete = new Delete(dataMapsDelete);
                                delete.execute();
                            }
                            break;
                    }
                    return true;
                }
            });
        }
    }


    private void getMassageSaveChange()
    {
        int count = adapter.getFragmentSettings().getCount_created();
        List<Fragment> fragments =  adapter.getFragmentSettings().getRealFragments();
        List<FragmentPositionMap> fragmentPositionMaps = getFragmentPosition(fragments);
        List<DataMap> dataMaps = getDataMap(fragmentPositionMaps);
        List<DataUser> user = DataUser.listAll(DataUser.class);
        if (dataMaps.size() > 0 || !user.get(0).getEnterprise().equals(adapter.getFragmentSettings().getEnterprise())
                || count != fragmentPositionMaps.size())
        {
            Toast toast = Toast.makeText(this, getResources().getText(R.string.not_save),
                    Toast.LENGTH_SHORT);
            toast.show();
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
                for (int i = 0; i < dataMapDelete.size(); i++)
                {
                    ConstantUrl constantUrl = new ConstantUrl();
                    String url = constantUrl.getUrlRemovePosition(user.get(0).getLogin(), dataMapDelete.get(i).getCode());
                    update = jsonParser.getUpdateEnterprise(url);
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
            MassageSaved();
            adapter.getFragmentSettings().setDataMapsDelete(new ArrayList<DataMap>());
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
                update = jsonParser.getUpdateEnterprise(url);
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
            MassageSaved();
        }
    }

    private void MassageSaved()
    {
        if (update)
        {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.saved),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_save),
                    Toast.LENGTH_SHORT);
            toast.show();
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
                update = jsonParser.getUpdateEnterprise(url);
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
                    path.add(dataMap);
                }
            }
        }
        return path;
    }

    public List<FragmentPositionMap> getDataMapFragment(List<FragmentPositionMap> fragments)
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
        adapter = new TabsPagerFragmentAdapterEnterprise(this, getSupportFragmentManager());

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
        if ((dataMaps.size() > 0 || !user.get(0).getEnterprise().equals(adapter.getFragmentSettings().getEnterprise())
                || count != fragmentPositionMaps.size()) & !update )
        {
            Toast toast = Toast.makeText(this, getResources().getText(R.string.not_save),
                    Toast.LENGTH_SHORT);
            toast.show();
            update = true;
        }
        else
        {
            super.onBackPressed();
        }
    }
}
