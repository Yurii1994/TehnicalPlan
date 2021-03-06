package com.muv.technicalplan.profile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.muv.technicalplan.ConstantTab;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.CustomViewPager;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.data.DataUser;

import java.util.List;

public class ProfileActivity extends AppCompatActivity
{
    private static final int LAYOUT = R.layout.activity_profile;
    private Toolbar toolbar;
    private CustomViewPager viewPager;
    private TabPagerFragmentAdapterProfile adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);
        initToolbar(getResources().getText(R.string.profile_name).toString(), this);
        initTabs();
    }

    private void initToolbar(String title, final ProfileActivity activity)
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        toolbar.setTitle(title);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.inflateMenu(R.menu.registration_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_go:
                        nextTab();
                        break;
                    case R.id.menu_delete:
                        DialogFragmentDeleteAccount deleteAccount = new DialogFragmentDeleteAccount().newInstance(activity);
                        deleteAccount.setDialog(deleteAccount);
                        deleteAccount.setCancelable(false);
                        deleteAccount.show(getSupportFragmentManager(), "dialogFragment");
                        break;
                }
                return true;
            }
        });
    }

    public void deleteAccount()
    {
        List<DataUser> user = DataUser.listAll(DataUser.class);
        removeAccount removeAccount = new removeAccount(user.get(0).getLogin());
        removeAccount.execute();
    }

    class removeAccount extends AsyncTask<Void, Void, Void> {

        private String login;
        private boolean remove;

        public removeAccount(String login)
        {
            this.login = login;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                JsonParser jsonParser = new JsonParser();
                ConstantUrl url = new ConstantUrl();
                remove = jsonParser.parseRemoveRefresh(url.getUrlDeleteAccount(login));
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
            if (remove)
            {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.deleted_account),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void nextTab()
    {
        String surname = getSurNameUser();
        String name = getNameUser();
        String surname_father = getSurNameFatherUser();
        int type = getTypeAccountUser();
        if (type != 0 & surname.length() > 0 & name.length() > 0 & surname_father.length() > 0)
        {
            getChangeTypeAccount();
            showNextTab();
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.fill_fields),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public String getPath()
    {
        return getPageOneFragment().getPath();
    }

    public PageOneProfileFragment getPageOneFragment()
    {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        PageOneProfileFragment pageOneProfileFragment = null;
        for (int i = 0; i < fragments.size(); i++)
        {
            try
            {
                pageOneProfileFragment = (PageOneProfileFragment)fragments.get(i);
                break;
            }
            catch (Exception e)
            {}
        }
        return pageOneProfileFragment;
    }

    public PageTwoProfileFragment getPageTwoFragment()
    {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        PageTwoProfileFragment pageTwoProfileFragment = null;
        for (int i = 0; i < fragments.size(); i++)
        {
            try
            {
                pageTwoProfileFragment = (PageTwoProfileFragment) fragments.get(i);
                break;
            }
            catch (Exception e)
            {}
        }
        return pageTwoProfileFragment;
    }

    public void getChangeTypeAccount()
    {
        getPageTwoFragment().getChangeTypeAccount();
    }

    public String getSurNameUser()
    {
        return getPageOneFragment().getSurNameUser();
    }

    public String getNameUser()
    {
        return getPageOneFragment().getNameUser();
    }

    public String getSurNameFatherUser()
    {
        return getPageOneFragment().getSurNameFatherUser();
    }

    public int getTypeAccountUser()
    {
        return getPageOneFragment().getTypeAccountUser();
    }

    public DataUser getDataUserPageOne(String name, String surname, String surname_father, int type_account)
    {
        DataUser dataUser = new DataUser();
        dataUser.setName(name);
        dataUser.setSurname(surname);
        dataUser.setSurname_father(surname_father);
        dataUser.setType_account(type_account);
        return dataUser;
    }

    private void showNextTab()
    {
        int tab = viewPager.getCurrentItem();
        if (tab == 0)
        {
            viewPager.setCurrentItem(ConstantTab.TAB_TWO);
            last_tab = true;
            invalidateOptionsMenu();
        }
    }

    private boolean state_back;

    private void showPreviousTab()
    {
        int tab = viewPager.getCurrentItem();
        if (tab == 1)
        {
            viewPager.setCurrentItem(ConstantTab.TAB_ONE);
            last_tab = false;
            invalidateOptionsMenu();
        }
        else
        if (tab == 0)
        {
            setState_back(true);
        }
    }

    public void setState_back(boolean state)
    {
        state_back = state;
    }

    public void onPressedBack()
    {
        setState_back(true);
        onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        if (!PageTwoProfileFragment.changed_profile)
        {
            showPreviousTab();
        }
        if (state_back)
        {
            super.onBackPressed();
            PageTwoProfileFragment.changed_profile = false;
        }
    }

    private boolean last_tab;

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem next = menu.findItem(R.id.menu_go);
        MenuItem delete = menu.findItem(R.id.menu_delete);
        if(last_tab)
        {
            next.setVisible(false);
            delete.setVisible(true);
        }
        else
        {
            next.setVisible(true);
            delete.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registration_menu, menu);
        return true;
    }

    private void initTabs()
    {
        viewPager = (CustomViewPager) findViewById(R.id.view_pager_profile);
        adapter = new TabPagerFragmentAdapterProfile(this, getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_profile);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
