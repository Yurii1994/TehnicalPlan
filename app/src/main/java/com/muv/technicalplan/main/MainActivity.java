package com.muv.technicalplan.main;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muv.technicalplan.CircleImage;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.DialogFragmentProgress;
import com.muv.technicalplan.Internet;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.RecoveryActivity;
import com.muv.technicalplan.SaveLoadPreferences;
import com.muv.technicalplan.Service;
import com.muv.technicalplan.base.BaseUser;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.enterprise.EnterpriseActivity;
import com.muv.technicalplan.linking.LinkingActivity;
import com.muv.technicalplan.linking.LinkingNotificationCounter;
import com.muv.technicalplan.linking.TabsPagerFragmentAdapterLinking;
import com.muv.technicalplan.profile.ProfileActivity;
import com.muv.technicalplan.registration.RegistrationActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.List;

public class MainActivity  extends AppCompatActivity implements View.OnClickListener
{
    private static final int LAYOUT = R.layout.activity_main;
    private SaveLoadPreferences saveLoadPreferences = new SaveLoadPreferences();;
    private Intent intent;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    public static DrawerLayout drawerLayout;
    private TextView notLinked;
    private TabsPagerFragmentAdapterMain adapter;

    private EditText login;
    private EditText password;
    private CheckBox save_account;
    private boolean state_save_account;
    private boolean load_photo;
    private boolean changed_profile;
    private boolean registered;

    private LinearLayout sing_in_layout;
    private LinearLayout main_layout;

    private JsonParser jsonParser = new JsonParser();
    private Internet internet = new Internet();
    private ConstantUrl constantUrl = new ConstantUrl();
    private NavigationView navigationView;
    private static MainActivity mainActivity;
    private List<DataUser> user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        startService();
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        mainActivity = this;

        sing_in_layout = (LinearLayout)findViewById(R.id.layout_sing_in);
        main_layout = (LinearLayout)findViewById(R.id.main_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        login = (EditText)findViewById(R.id.login);
        password = (EditText)findViewById(R.id.password);
        Button sing_in = (Button)findViewById(R.id.sing_in);
        Button recovery = (Button)findViewById(R.id.recovery);
        Button registration = (Button)findViewById(R.id.registration);
        save_account = (CheckBox)findViewById(R.id.save_account);

        sing_in.setOnClickListener(this);
        recovery.setOnClickListener(this);
        registration.setOnClickListener(this);

        createActivity();
        setSoftInputMode();
    }

    public void startService() {
        startService(new Intent(this, Service.class));
    }

    private void createActivity()
    {
        state_save_account = saveLoadPreferences.loadBooleanPreferences("SING_IN", "SAVE_ACCOUNT", this);
        save_account.setChecked(state_save_account);
        if (!state_save_account & !registered)
        {
            BaseUser baseUser = new BaseUser();
            baseUser.deleteBase();
        }
        user = DataUser.listAll(DataUser.class);
        if (user.size() == 0)
        {
            sing_in_layout.setVisibility(View.VISIBLE);
        }
        else
        {
            if (internet.isOnline(this))
            {
                String login = user.get(0).getLogin();
                String email = user.get(0).getEmail();
                String password = user.get(0).getPassword();

                SingIn singIn = new SingIn(constantUrl.getUrlSingInLogin(login), constantUrl.getUrlSingInEmail(email), password);
                singIn.execute();
            }
            else
            {
                sing_in_layout.setVisibility(View.VISIBLE);
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.not_network),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        changed_profile = saveLoadPreferences.loadBooleanPreferences("SING_IN", "CHANGE_PROFILE", this);
        registered = saveLoadPreferences.loadBooleanPreferences("SING_IN", "REGISTERED", this);
        if (registered | changed_profile)
        {
            createActivity();
            saveLoadPreferences.saveBooleanPreferences("SING_IN", "REGISTERED", false, getApplicationContext());
            saveLoadPreferences.saveBooleanPreferences("SING_IN", "CHANGE_PROFILE", false, getApplicationContext());
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.sing_in:
                String login = this.login.getText().toString().toLowerCase();
                String email = this.login.getText().toString().toLowerCase();
                String password = this.password.getText().toString();
                if (internet.isOnline(getApplicationContext()))
                {
                    if (login.length() > 0 & password.length() > 0)
                    {
                        SingIn singIn = new SingIn(constantUrl.getUrlSingInLogin(login), constantUrl.getUrlSingInEmail(email), password);
                        singIn.execute();
                    }
                    else
                    {
                        if (login.length() == 0 & password.length() == 0)
                        {
                            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.sing_in_not),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else
                        if (login.length() == 0)
                        {
                            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.sing_in_not_login),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else
                        {
                            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.sing_in_not_password),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.not_network),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;

            case R.id.recovery:
                if (internet.isOnline(getApplicationContext()))
                {
                    intent = new Intent(this, RecoveryActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.not_network),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;

            case R.id.registration:
                if (internet.isOnline(getApplicationContext()))
                {
                    intent = new Intent(this, RegistrationActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.not_network),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    class SingIn extends AsyncTask<Void, Void, Void> {

        private String url_login;
        private String url_email;
        DataUser dataUserLogin;
        DataUser dataUserEmail;
        private String password;
        DialogFragmentProgress dialog = new DialogFragmentProgress().newInstance(getResources().getString(R.string.sing));
        boolean changed_profile = saveLoadPreferences.loadBooleanPreferences("SING_IN", "CHANGE_PROFILE", getApplicationContext());

        public SingIn(String url_login, String url_email, String password)
        {
            this.url_login = url_login;
            this.url_email = url_email;
            this.password = password;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                if (!changed_profile)
                {
                    dialog.show(getFragmentManager(), "dialogFragment");
                    dialog.setCancelable(false);
                }
                dataUserLogin = jsonParser.parseUser(url_login);
                dataUserEmail = jsonParser.parseUser(url_email);
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
            if (!changed_profile)
            {
                dialog.dismiss();
            }
            try
            {
                if (dataUserLogin.getUser_id() == 0 & dataUserEmail.getUser_id() == 0)
                {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.sing_in_error_login),
                            Toast.LENGTH_SHORT);
                    toast.show();
                    singInError();
                }
                else
                {
                    String passwordLogin = dataUserLogin.getPassword();
                    String passwordEmail = dataUserEmail.getPassword();
                    if (passwordLogin == null)
                    {
                        if (passwordEmail.equals(password))
                        {
                            singIn(dataUserEmail);
                            getChangeKeyboard();
                        }
                        else
                        {
                            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.sing_in_error_password),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            singInError();
                        }
                    }
                    else
                    {
                        if (passwordLogin.equals(password))
                        {
                            singIn(dataUserLogin);
                            getChangeKeyboard();
                        }
                        else
                        {
                            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.sing_in_error_password),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            singInError();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.not_network),
                        Toast.LENGTH_SHORT);
                toast.show();
                sing_in_layout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void singInError()
    {
        sing_in_layout.setVisibility(View.VISIBLE);
    }

    private void singIn(DataUser dataUser)
    {
        saveLoadPreferences.saveBooleanPreferences("SING_IN", "SAVE_ACCOUNT", save_account.isChecked(), getApplicationContext());
        sing_in_layout.setVisibility(View.GONE);
        main_layout.setVisibility(View.VISIBLE);

        BaseUser baseUser = new BaseUser();
        baseUser.createBase(dataUser);

        initToolbar();
        initTabs();
        initNavigationView();
    }

    public static void setDrawerLockClosed(DrawerLayout drawerLayout)
    {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void exitAccount()
    {
        state_save_account = false;
        saveLoadPreferences.saveBooleanPreferences("SING_IN", "SAVE_ACCOUNT", false, getApplicationContext());
        sing_in_layout.setVisibility(View.VISIBLE);
        main_layout.setVisibility(View.GONE);
        login.setText("");
        password.setText("");
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setColorStatusBar(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        BaseUser baseUser = new BaseUser();
        baseUser.deleteBase();
    }

    private void getChangeKeyboard()
    {
        KeyboardVisibilityEvent.setEventListener(this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen)
                        {
                            List<DataUser> user = DataUser.listAll(DataUser.class);
                            if(sing_in_layout.getVisibility() != View.VISIBLE || user.size() > 0)
                            {
                                close_openKey();
                            }
                        }
                    }
                });
    }

    private void close_openKey()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public Integer getDisplayDpi()
    {
        int c;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        c = displayMetrics.densityDpi;
        return c;
    }

    private void setSoftInputMode()
    {
        if (getDisplayDpi() >= 320)
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        else
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private void initToolbar()
    {
        if (toolbar == null)
        {
            toolbar = (Toolbar) findViewById(R.id.toolbar_main);
            toolbar.setTitle(R.string.app_name);
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.menu_refresh_main:
                            viewPager.getAdapter().notifyDataSetChanged();
                            break;
                    }
                    return true;
                }
            });
        }
    }

    private void initTabs()
    {
        viewPager = (ViewPager)findViewById(R.id.view_pager_main);
        adapter = new TabsPagerFragmentAdapterMain(this, getSupportFragmentManager(), this);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);

        viewPager.getAdapter().notifyDataSetChanged();

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setColorStatusBar(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(color);
        }
    }

    private void initNavigationView()
    {
        setColorStatusBar(getResources().getColor(android.R.color.transparent));
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView)findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_profile:
                        intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.menu_exit:
                        exitAccount();
                        break;

                    case R.id.menu_linking:
                        intent = new Intent(getApplicationContext(), LinkingActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.menu_enterprise:
                        intent = new Intent(getApplicationContext(), EnterpriseActivity.class);
                        startActivity(intent);
                        break;

                }
                return true;
            }
        });

        View navigation_header =  navigationView.getHeaderView(0);
        TextView nameView = (TextView)navigation_header.findViewById(R.id.name);
        TextView emailView = (TextView)navigation_header.findViewById(R.id.email);
        user = DataUser.listAll(DataUser.class);
        String name = user.get(0).getName();
        String surname = user.get(0).getSurname();
        String email = user.get(0).getEmail();
        nameView.setText(name + " " + surname);
        emailView.setText(email);

        LinkingNotificationCounter();
        getMenuNavigation();

        ImageView photo = (ImageView)navigation_header.findViewById(R.id.account_image);
        getUploadImage(user, photo);
    }

    private void getMenuNavigation()
    {
        if (user.size() > 0)
        {
            if (navigationView != null)
            {
                Menu nav_Menu = navigationView.getMenu();
                if (user.get(0).getType_account() == 2)
                {
                    nav_Menu.findItem(R.id.menu_enterprise).setVisible(false);
                }
                else
                {
                    nav_Menu.findItem(R.id.menu_enterprise).setVisible(true);
                }
            }
        }
    }

    public static void updateStateViewPager()
    {
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public static MainActivity getMainActivity()
    {
        return mainActivity;
    }

    public void LinkingNotificationCounter()
    {
        LinkingNotificationCounter counter = new LinkingNotificationCounter(navigationView, R.id.menu_linking);
        counter.getNotificationCount();
    }

    private void getUploadImage(List<DataUser> user, ImageView imageView)
    {
        Picasso.with(getApplicationContext())
                .load(constantUrl.getUrlDownloadImage(user.get(0).getImage()))
                .placeholder(R.drawable.profile_img)
                .error(R.drawable.profile_img)
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {

                        Bitmap result = new CircleImage(getApplicationContext()).transform(source);
                        if (result != source) {
                            source.recycle();
                        }
                        return result;
                    }

                    @Override
                    public String key() {
                        return "circle()";
                    }
                })
                .into(imageView);
    }
}
