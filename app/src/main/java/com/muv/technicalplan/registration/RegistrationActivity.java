package com.muv.technicalplan.registration;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.muv.technicalplan.ConstantTab;
import com.muv.technicalplan.CustomViewPager;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.R;

public class RegistrationActivity extends AppCompatActivity
{
    private static final int LAYOUT = R.layout.activity_registration;

    private Toolbar toolbar;
    private CustomViewPager viewPager;
    private TabPagerFragmentAdapterRegistration adapter;
    private DataUser dataUser = new DataUser();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);
        initToolbar(getResources().getText(R.string.register_name).toString());
        initTabs();
    }

    private void initToolbar(String title)
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar_registration);
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
                }
                return true;
            }
        });
    }

    private void nextTab()
    {
        String surname = adapter.getSurNameUser();
        String name = adapter.getNameUser();
        String surname_father = adapter.getSurNameFatherUser();
        int type = adapter.getTypeAccountUser();
        if (type != 0 & surname.length() > 0 & name.length() > 0 & surname_father.length() > 0)
        {
            dataUser = getDataUserPageOne(name, surname, surname_father, type);
            adapter.setOnClickListener(dataUser, adapter.getPath());
            showNextTab();
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.fill_fields),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private DataUser getDataUserPageOne(String name, String surname, String surname_father, int type_account)
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

    @Override
    public void onBackPressed()
    {
        if (!PageTwoRegistrationFragment.registered)
        {
            showPreviousTab();
        }
        if (state_back)
        {
            super.onBackPressed();
            PageTwoRegistrationFragment.registered = false;
        }
    }

    private boolean last_tab;

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem register = menu.findItem(R.id.menu_go);
        MenuItem delete = menu.findItem(R.id.menu_delete);
        delete.setVisible(false);
        if(last_tab)
        {
            register.setVisible(false);
        }
        else
        {
            register.setVisible(true);
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
        viewPager = (CustomViewPager) findViewById(R.id.view_pager_registration);
        adapter = new TabPagerFragmentAdapterRegistration(this, getSupportFragmentManager(), this);
        adapter.setTabPagerFragmentAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_registration);
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
