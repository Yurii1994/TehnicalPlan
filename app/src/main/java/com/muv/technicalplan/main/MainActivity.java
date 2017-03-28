package com.muv.technicalplan.main;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.ViewTreeObserver;
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
import com.muv.technicalplan.base.BaseMap;
import com.muv.technicalplan.base.BaseUser;
import com.muv.technicalplan.data.DataMaps;
import com.muv.technicalplan.data.DataPosition;
import com.muv.technicalplan.data.DataSearch;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.enterprise.EnterpriseActivity;
import com.muv.technicalplan.linking.LinkingActivity;
import com.muv.technicalplan.linking.LinkingNotificationCounter;
import com.muv.technicalplan.profile.ProfileActivity;
import com.muv.technicalplan.registration.RegistrationActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity  extends AppCompatActivity implements View.OnClickListener
{
    private static final int LAYOUT = R.layout.activity_main;
    private SaveLoadPreferences saveLoadPreferences = new SaveLoadPreferences();;
    private Intent intent;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    public static DrawerLayout drawerLayout;
    private TabsPagerFragmentAdapterMain adapter;
    private EditText login;
    private EditText password;
    private CheckBox save_account;
    private boolean state_save_account;
    private boolean changed_profile;
    private LinearLayout sing_in_layout;
    private LinearLayout main_layout;
    private JsonParser jsonParser = new JsonParser();
    private Internet internet = new Internet();
    private ConstantUrl constantUrl = new ConstantUrl();
    private NavigationView navigationView;
    private static MainActivity mainActivity;
    private List<DataUser> user;
    private List<DataMaps> dataMaps = new ArrayList<>();
    private List<DataPosition> dataPosition = new ArrayList<>();
    private final String UPDATE = "com.muv.action.UPDATE";
    private BroadcastReceiver broadcastReceiver;
    private ImageView photo;
    private Map<Integer, String> positionMenu = new LinkedHashMap<>();
    private ArrayList<PositionMenuParcelable> positionMenuParcelable = new ArrayList<>();
    private boolean save_filter;
    private int day = 0;
    private boolean state_map_full = false;
    private String type = "all";
    private int position = 1;
    private Toast toast;
    private boolean state_key_board;
    private Bundle savedInstanceState;
    private ArrayList<MapParcelable> mapParcelablesFragment1 = new ArrayList<>();
    private ArrayList<MapParcelable> mapParcelablesFragment2 = new ArrayList<>();
    private ArrayList<MapParcelable> mapParcelablesFragment3 = new ArrayList<>();
    private List<DataMaps> normal = new ArrayList<>();
    private List<DataMaps> lightweight = new ArrayList<>();
    private List<DataMaps> light = new ArrayList<>();
    private boolean full_update;
    private boolean account_update;
    private int count_start;
    private DialogFragmentProgress dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.savedInstanceState = savedInstanceState;
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

        if (savedInstanceState != null)
        {
            count_start = savedInstanceState.getInt("CountStart");
        }
        count_start++;
        createActivity(savedInstanceState);

        setSoftInputMode();
    }

    private void showToastMessage(String a)
    {
        if (toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("MapParcelableFragment1", mapParcelablesFragment1);
        outState.putParcelableArrayList("MapParcelableFragment2", mapParcelablesFragment2);
        outState.putParcelableArrayList("MapParcelableFragment3", mapParcelablesFragment3);
        outState.putParcelableArrayList("PositionMenu", positionMenuParcelable);
        outState.putInt("CountStart", count_start);
    }

    private void createActivity(Bundle savedInstanceState)
    {
        if (savedInstanceState == null)
        {
            state_save_account = saveLoadPreferences.loadBooleanPreferences("SING_IN", "SAVE_ACCOUNT", this);
            save_account.setChecked(state_save_account);
            if (!state_save_account & !changed_profile)
            {
                BaseUser baseUser = new BaseUser();
                baseUser.deleteBase();
            }
            user = DataUser.listAll(DataUser.class);
            if (user.size() == 0)
            {
                sing_in_layout.setVisibility(View.VISIBLE);
                setOrientation();
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
                    singIn(user.get(0));
                }
            }
        }
        else
        {
            restoreMap();
            restorePositionMenu();
            user = DataUser.listAll(DataUser.class);
            if (normal.size() == 0 & lightweight.size() == 0 & light.size() == 0)
            {
                if (user.size() == 0)
                {
                    sing_in_layout.setVisibility(View.VISIBLE);
                    setOrientation();
                }
                else
                {
                    sing_in_layout.setVisibility(View.GONE);
                    main_layout.setVisibility(View.VISIBLE);
                    initToolbar();
                    initTabs();
                    initNavigationView();
                    getDownloadMap();
                }
            }
            else
            {
                sing_in_layout.setVisibility(View.GONE);
                main_layout.setVisibility(View.VISIBLE);
                initToolbar();
                initTabs();
                initNavigationView();
                final Handler handler = new Handler();
                Runnable runnable = new Runnable()
                {
                    public void run()
                    {
                        handler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                adapter.setList(normal, lightweight, light);
                                adapter.notifyDataSetChanged();
                                adapter.TableNull();
                            }
                        });
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
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
                String full_update_text = intent.getStringExtra("Full_update");
                if (full_update_text != null)
                {
                    if (full_update_text.equals("true"))
                    {
                        full_update = true;
                    }
                }
                String change = intent.getStringExtra("change");
                String registration = intent.getStringExtra("registration");
                if (update != null)
                {
                    switch (update)
                    {
                        case "account":
                            account_update = true;
                            if (registration != null)
                            {
                                if (registration.equals("true"))
                                {
                                    account_update = false;
                                }
                            }
                            changed_profile = false;
                            if (change != null)
                            {
                                if (change.equals("true"))
                                {
                                    changed_profile = true;
                                }
                                else
                                {
                                    changed_profile = false;
                                }
                            }
                            createActivity(null);
                            break;

                        case "image":
                            String url = constantUrl.getUrlSingInLogin(user.get(0).getLogin());
                            DownloadUser downloadUser = new DownloadUser(url);
                            downloadUser.execute();
                            break;
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, filter);
        RestoreFilter();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SaveFilter();
        if (adapter != null)
        {
            List<List<DataMaps>> dataMap = adapter.getDataMap();
            if (dataMap.size() > 0)
            {
                saveMap(dataMap);
            }
        }
        try
        {
            dialog.dismiss();
        }
        catch (Exception E)
        {}
    }

    private void savePositionMenu()
    {
        if (positionMenu.size() > 0)
        {
            for (Map.Entry entry : positionMenu.entrySet())
            {
                int key = (int)entry.getKey();
                String value = entry.getValue().toString();
                positionMenuParcelable.add(new PositionMenuParcelable(key, value));
            }
        }
    }

    private void restorePositionMenu()
    {
        positionMenuParcelable = savedInstanceState.getParcelableArrayList("PositionMenu");
        positionMenu.clear();
        if (positionMenuParcelable != null)
        {
            for (int i = 0; i < positionMenuParcelable.size(); i++)
            {
                positionMenu.put(positionMenuParcelable.get(i).key, positionMenuParcelable.get(i).position);
            }
        }
    }

    private void saveMap(List<List<DataMaps>> dataMaps)
    {
        if (dataMaps.size() > 0)
        {
            for (int j = 0; j < dataMaps.size(); j++)
            {
                List<DataMaps> dataMap = dataMaps.get(j);
                switch (j)
                {
                    case 0:
                        mapParcelablesFragment1 = new ArrayList<>();
                        break;

                    case 1:
                        mapParcelablesFragment2 = new ArrayList<>();
                        break;

                    case 2:
                        mapParcelablesFragment3 = new ArrayList<>();
                        break;
                }
                for (int i = 0; i < dataMap.size(); i++)
                {
                    int id = dataMap.get(i).getIdMap();
                    String code = dataMap.get(i).getCode();
                    String general = dataMap.get(i).getGeneral();
                    String relative = dataMap.get(i).getRelative();
                    String description = dataMap.get(i).getDescription();
                    String normal = dataMap.get(i).getNormal();
                    String lightweight = dataMap.get(i).getLightweight();
                    String light = dataMap.get(i).getLight();
                    String date = dataMap.get(i).getDate();
                    String comment_manager = dataMap.get(i).getComment_manager();
                    String comment_performer = dataMap.get(i).getComment_performer();
                    String position = dataMap.get(i).getPosition();
                    String name_table = dataMap.get(i).getName_table();
                    String state_performance = dataMap.get(i).getState_performance();
                    String stitched = dataMap.get(i).getStitched();
                    switch (j)
                    {
                        case 0:
                            mapParcelablesFragment1.add(new MapParcelable(id,code, general, relative, description, normal, lightweight, light, date,
                                    comment_manager, comment_performer, position, name_table, state_performance, stitched));
                            break;

                        case 1:
                            mapParcelablesFragment2.add(new MapParcelable(id,code, general, relative, description, normal, lightweight, light, date,
                                    comment_manager, comment_performer, position, name_table, state_performance, stitched));
                            break;

                        case 2:
                            mapParcelablesFragment3.add(new MapParcelable(id,code, general, relative, description, normal, lightweight, light, date,
                                    comment_manager, comment_performer, position, name_table, state_performance, stitched));
                            break;
                    }
                }
            }
        }
    }

    private void restoreMap()
    {
        mapParcelablesFragment1 = savedInstanceState.getParcelableArrayList("MapParcelableFragment1");
        mapParcelablesFragment2 = savedInstanceState.getParcelableArrayList("MapParcelableFragment2");
        mapParcelablesFragment3 = savedInstanceState.getParcelableArrayList("MapParcelableFragment3");
        if (mapParcelablesFragment1 != null)
        {
            for (int i = 0; i < mapParcelablesFragment1.size(); i++)
            {
                normal.add(getDataRestore(mapParcelablesFragment1, i));
            }
            for (int i = 0; i < mapParcelablesFragment2.size(); i++)
            {
                lightweight.add(getDataRestore(mapParcelablesFragment2, i));
            }
            for (int i = 0; i < mapParcelablesFragment3.size(); i++)
            {
                light.add(getDataRestore(mapParcelablesFragment3, i));
            }
        }
    }

    private DataMaps getDataRestore(ArrayList<MapParcelable> mapParcelables, int i)
    {
        DataMaps dataMaps = new DataMaps();
        dataMaps.setIdMap(mapParcelables.get(i).id);
        dataMaps.setCode(mapParcelables.get(i).code);
        dataMaps.setGeneral(mapParcelables.get(i).general);
        dataMaps.setRelative(mapParcelables.get(i).relative);
        dataMaps.setDescription(mapParcelables.get(i).description);
        dataMaps.setNormal(mapParcelables.get(i).normal);
        dataMaps.setLightweight(mapParcelables.get(i).lightweight);
        dataMaps.setLight(mapParcelables.get(i).light);
        dataMaps.setDate(mapParcelables.get(i).date);
        dataMaps.setComment_manager(mapParcelables.get(i).comment_manager);
        dataMaps.setComment_performer(mapParcelables.get(i).comment_performer);
        dataMaps.setPosition(mapParcelables.get(i).position);
        dataMaps.setName_table(mapParcelables.get(i).name_table);
        dataMaps.setState_performance(mapParcelables.get(i).state_performance);
        dataMaps.setStitched(mapParcelables.get(i).stitched);
        return dataMaps;
    }

    private void RestoreFilter()
    {
        save_filter =  saveLoadPreferences.loadBooleanPreferences("Filter", "Save", this);
        if (save_filter)
        {
            day = saveLoadPreferences.loadIntegerPreferences("Filter", "Day", this);
            state_map_full = saveLoadPreferences.loadBooleanPreferences("Filter", "Full", this);
            type = saveLoadPreferences.loadStringPreferences("Filter", "Type", this);
            if (type == null || type.equals(""))
            {
                type = "all";
            }
            position = saveLoadPreferences.loadIntegerPreferences("Filter", "Position", this);
        }
    }

    private void SaveFilter()
    {
        saveLoadPreferences.saveBooleanPreferences("Filter", "Save", save_filter, this);
        if (save_filter)
        {
            saveLoadPreferences.saveIntegerPreferences("Filter", "Day", day, this);
            saveLoadPreferences.saveBooleanPreferences("Filter", "Full", state_map_full, this);
            saveLoadPreferences.saveStringPreferences("Filter", "Type", type, this);
            saveLoadPreferences.saveIntegerPreferences("Filter", "Position", position, this);
        }
    }

    class DownloadUser extends AsyncTask<Void, Void, Void>
    {
        private String url;
        private DataUser dataUser;

        public DownloadUser(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                dataUser = jsonParser.parseUser(url);
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
            BaseUser baseUser = new BaseUser();
            baseUser.createBase(dataUser);
            user = DataUser.listAll(DataUser.class);
            getUploadImage(user, photo);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
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
                            showToastMessage(getResources().getText(R.string.sing_in_not).toString());
                        }
                        else
                        if (login.length() == 0)
                        {
                            showToastMessage(getResources().getText(R.string.sing_in_not_login).toString());
                        }
                        else
                        {
                            showToastMessage(getResources().getText(R.string.sing_in_not_password).toString());
                        }
                    }
                }
                else
                {
                    showToastMessage(getResources().getText(R.string.not_network).toString());
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
                    showToastMessage(getResources().getText(R.string.not_network).toString());
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
                    showToastMessage(getResources().getText(R.string.not_network).toString());
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

        public SingIn(String url_login, String url_email, String password)
        {
            dialog = new DialogFragmentProgress().newInstance(getResources().getString(R.string.sing));
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
                try
                {
                    dialog.dismiss();
                }
                catch (Exception E)
                {}
            }
            try
            {
                if (dataUserLogin.getUser_id() == 0 & dataUserEmail.getUser_id() == 0)
                {
                    showToastMessage(getResources().getText(R.string.sing_in_error_login).toString());
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
                            if (state_key_board)
                            {
                                close_openKey();
                            }
                        }
                        else
                        {
                            showToastMessage(getResources().getText(R.string.sing_in_error_password).toString());
                            singInError();
                        }
                    }
                    else
                    {
                        if (passwordLogin.equals(password))
                        {
                            singIn(dataUserLogin);
                            getChangeKeyboard();
                            if (state_key_board)
                            {
                                close_openKey();
                            }
                        }
                        else
                        {
                            showToastMessage(getResources().getText(R.string.sing_in_error_password).toString());
                            singInError();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                showToastMessage(getResources().getText(R.string.not_network).toString());
                singInError();
            }
        }
    }

    private void singInError()
    {
        sing_in_layout.setVisibility(View.VISIBLE);
        setOrientation();
    }

    private void singIn(DataUser dataUser)
    {
        saveLoadPreferences.saveBooleanPreferences("SING_IN", "SAVE_ACCOUNT", save_account.isChecked(), getApplicationContext());
        sing_in_layout.setVisibility(View.GONE);
        main_layout.setVisibility(View.VISIBLE);

        BaseUser baseUser = new BaseUser();
        baseUser.createBase(dataUser);

        user = DataUser.listAll(DataUser.class);

        if (!account_update)
        {
            initToolbar();
            initTabs();
            getDownloadMap();
        }
        else
        {
            if (full_update)
            {
                initToolbar();
                initTabs();
                getDownloadMap();
            }
        }
        initNavigationView();
        setOrientation();
    }

    public static void setDrawerLockClosed(DrawerLayout drawerLayout)
    {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void exitAccount()
    {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        account_update = false;
                        full_update = false;
                        dataMaps = new ArrayList<>();
                        mapParcelablesFragment1 = new ArrayList<>();
                        mapParcelablesFragment2 = new ArrayList<>();
                        mapParcelablesFragment3 = new ArrayList<>();
                        positionMenuParcelable = new ArrayList<>();
                        state_save_account = false;
                        positionMenuParcelable.clear();
                        saveLoadPreferences.saveBooleanPreferences("SING_IN", "SAVE_ACCOUNT", false, getApplicationContext());
                        sing_in_layout.setVisibility(View.VISIBLE);
                        main_layout.setVisibility(View.GONE);
                        login.setText("");
                        password.setText("");
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        setColorStatusBar(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        BaseUser baseUser = new BaseUser();
                        baseUser.deleteBase();
                        BaseMap baseMap = new BaseMap();
                        baseMap.deleteBase();
                        setOrientation();
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void getChangeKeyboard()
    {
        KeyboardVisibilityEvent.setEventListener(this,
                new KeyboardVisibilityEventListener()
                {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        state_key_board = false;
                        if (isOpen)
                        {
                            state_key_board = true;
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
            setSupportActionBar(toolbar);
        }
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
                        if (internet.isOnline(getApplicationContext()))
                        {
                            intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            showToastMessage(getResources().getText(R.string.not_network).toString());
                        }
                        break;

                    case R.id.menu_exit:
                        exitAccount();
                        break;

                    case R.id.menu_linking:
                        if (internet.isOnline(getApplicationContext()))
                        {
                            intent = new Intent(getApplicationContext(), LinkingActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            showToastMessage(getResources().getText(R.string.not_network).toString());
                        }
                        break;

                    case R.id.menu_enterprise:
                        if (internet.isOnline(getApplicationContext()))
                        {
                            intent = new Intent(getApplicationContext(), EnterpriseActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            showToastMessage(getResources().getText(R.string.not_network).toString());
                        }
                        break;

                }
                return true;
            }
        });

        View navigation_header =  navigationView.getHeaderView(0);
        TextView nameView = (TextView)navigation_header.findViewById(R.id.name);
        TextView emailView = (TextView)navigation_header.findViewById(R.id.email);
        String name = "";
        String surname = "";
        String email = "";
        if (user.size() > 0)
        {
            name = user.get(0).getName();
            surname = user.get(0).getSurname();
            email = user.get(0).getEmail();
        }
        nameView.setText(name + " " + surname);
        emailView.setText(email);

        LinkingNotificationCounter();
        getMenuNavigation();

        photo = (ImageView)navigation_header.findViewById(R.id.account_image);
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
        if (internet.isOnline(this))
        {
            LinkingNotificationCounter counter = new LinkingNotificationCounter(navigationView, R.id.menu_linking);
            counter.getNotificationCount();
        }
    }

    private void getUploadImage(List<DataUser> user, ImageView imageView)
    {
        if (user.size() > 0)
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

    private void initTabs()
    {
        viewPager = (ViewPager)findViewById(R.id.view_pager_main);
        adapter = new TabsPagerFragmentAdapterMain(this, getSupportFragmentManager(), dataMaps, dataMaps, dataMaps);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                if (state_key_board)
                {
                    close_openKey();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setOrientation()
    {
        if (sing_in_layout.getVisibility() == View.VISIBLE)
        {
            OrientationLock();
        }
        else
        {
            OrientationUnlock();
        }
    }

    public void OrientationLock()
    {
        if (count_start == 1)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
        else
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        else
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    public void OrientationUnlock()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void getDownloadMap()
    {
        dataMaps.clear();
        dataPosition.clear();
        if (adapter != null)
        {
            adapter.getProgressWheelVisible();
            adapter.setList(new ArrayList<DataMaps>(), new ArrayList<DataMaps>(), new ArrayList<DataMaps>());
            adapter.notifyDataSetChanged();
        }
        if (user.size() > 0)
        {
            if (user.get(0).getName_table() != null)
            {
                if (!user.get(0).getName_table().equals("false"))
                {
                    if (internet.isOnline(this))
                    {
                        if (!user.get(0).getName_table().equals("null") & !user.get(0).getName_table().equals("false"))
                        {
                            try
                            {
                                DownloadPosition downloadPosition = new DownloadPosition();
                                downloadPosition.execute();
                            }
                            catch (Exception e)
                            {}
                        }
                    }
                    else
                    {
                        if (adapter != null)
                        {
                            final Handler handler = new Handler();
                            Runnable runnable = new Runnable()
                            {
                                public void run()
                                {
                                    handler.post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            dataMaps = DataMaps.listAll(DataMaps.class);
                                            positionMenu = getPositionMenu(dataMaps);
                                            savePositionMenu();
                                            if (!getStatePositionMenu(getPositionMenu(DataMaps.listAll(DataMaps.class)), positionMenu))
                                            {
                                                position = 1;
                                            }
                                            List<DataMaps> normal = getDataMapReal(dataMaps, getResources().getText(R.string.normal).toString());
                                            List<DataMaps> lightweight = getDataMapReal(dataMaps, getResources().getText(R.string.lightweight).toString());
                                            List<DataMaps> light = getDataMapReal(dataMaps, getResources().getText(R.string.light).toString());
                                            normal = getDataMapOfDate(normal, day, "normal", state_map_full);
                                            lightweight = getDataMapOfDate(lightweight, day, "lightweight", state_map_full);
                                            light = getDataMapOfDate(light, day, "light", state_map_full);
                                            normal = getDataMapOfType(normal, type);
                                            lightweight = getDataMapOfType(lightweight, type);
                                            light = getDataMapOfType(light, type);
                                            normal = getDataMapOfPosition(normal, position, positionMenu);
                                            lightweight = getDataMapOfPosition(lightweight, position, positionMenu);
                                            light  = getDataMapOfPosition(light, position, positionMenu);
                                            adapter.setList(normal, lightweight, light);
                                            adapter.notifyDataSetChanged();
                                            adapter.TableNull();
                                            invalidateOptionsMenu();
                                        }
                                    });
                                }
                            };
                            Thread thread = new Thread(runnable);
                            thread.start();
                        }
                    }
                }
            }
            else
            {
                positionMenu = new LinkedHashMap<>();
                invalidateOptionsMenu();
            }
        }
    }

    class DownloadPosition extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                if (user.get(0).getType_account() == 1)
                {
                    dataPosition = jsonParser.parseGetPosition(constantUrl.getUrlGetPosition(user.get(0).getLogin()));
                }
                else
                {
                    DataPosition dataPositions = new DataPosition();
                    dataPositions.setName_table(user.get(0).getName_table());
                    dataPositions.setPosition(user.get(0).getPosition());
                    dataPosition.add(dataPositions);
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
            DownloadMap downloadMap = new DownloadMap();
            downloadMap.execute();
        }
    }

    private List<DataMaps> getRealDataMap(List<DataMaps> dataMap)
    {
        List<DataMaps> dataRes = new ArrayList<>();
        for (int i = 0; i < dataMap.size(); i++)
        {
            if (i > 0)
            {
                dataRes.add(dataMap.get(i));
            }
        }
        return dataRes;
    }

    class DownloadMap extends AsyncTask<Void, Integer, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                for (int i = 0; i < dataPosition.size(); i++)
                {
                    List<DataMaps> dataMapses = getRealDataMap(jsonParser.parseMaps(
                            constantUrl.getUrlTablePosition(dataPosition.get(i).getName_table()),
                            dataPosition.get(i).getPosition(), dataPosition.get(i).getName_table()));
                    for (int j = 0; j < dataMapses.size(); j++)
                    {
                        dataMaps.add(dataMapses.get(j));
                    }
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
            final Handler handler = new Handler();
            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            positionMenu = getPositionMenu(dataMaps);
                            savePositionMenu();
                            if (!getStatePositionMenu(getPositionMenu(DataMaps.listAll(DataMaps.class)), positionMenu))
                            {
                                position = 1;
                            }
                            List<DataMaps> normal = getDataMapReal(dataMaps, getResources().getText(R.string.normal).toString());
                            List<DataMaps> lightweight = getDataMapReal(dataMaps, getResources().getText(R.string.lightweight).toString());
                            List<DataMaps> light = getDataMapReal(dataMaps, getResources().getText(R.string.light).toString());
                            normal = getDataMapOfDate(normal, day, "normal", state_map_full);
                            lightweight = getDataMapOfDate(lightweight, day, "lightweight", state_map_full);
                            light = getDataMapOfDate(light, day, "light", state_map_full);
                            normal = getDataMapOfType(normal, type);
                            lightweight = getDataMapOfType(lightweight, type);
                            light = getDataMapOfType(light, type);
                            normal = getDataMapOfPosition(normal, position, positionMenu);
                            lightweight = getDataMapOfPosition(lightweight, position, positionMenu);
                            light  = getDataMapOfPosition(light, position, positionMenu);
                            adapter.setExpandedPositionSet(new HashSet<Integer>());
                            adapter.setList(normal, lightweight, light);
                            adapter.TableNull();
                            invalidateOptionsMenu();
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    public void setFilter()
    {
        dataMaps.clear();
        dataPosition.clear();
        if (adapter != null)
        {
            adapter.getProgressWheelVisible();
            adapter.setList(new ArrayList<DataMaps>(), new ArrayList<DataMaps>(), new ArrayList<DataMaps>());
            adapter.notifyDataSetChanged();
        }
        final Handler handler = new Handler();
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        dataMaps = DataMaps.listAll(DataMaps.class);
                        List<DataMaps> normal = getDataMapReal(dataMaps, getResources().getText(R.string.normal).toString());
                        List<DataMaps> lightweight = getDataMapReal(dataMaps, getResources().getText(R.string.lightweight).toString());
                        List<DataMaps> light = getDataMapReal(dataMaps, getResources().getText(R.string.light).toString());
                        normal = getDataMapOfDate(normal, day, "normal", state_map_full);
                        lightweight = getDataMapOfDate(lightweight, day, "lightweight", state_map_full);
                        light = getDataMapOfDate(light, day, "light", state_map_full);
                        normal = getDataMapOfType(normal, type);
                        lightweight = getDataMapOfType(lightweight, type);
                        light = getDataMapOfType(light, type);
                        normal = getDataMapOfPosition(normal, position, positionMenu);
                        lightweight = getDataMapOfPosition(lightweight, position, positionMenu);
                        light  = getDataMapOfPosition(light, position, positionMenu);
                        adapter.setExpandedPositionSet(new HashSet<Integer>());
                        adapter.setList(normal, lightweight, light);
                        adapter.notifyDataSetChanged();
                        adapter.TableNull();
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void updateDataMap(final String descriptionText, final String positionText, final String title)
    {

        Runnable runnable = new Runnable()
        {
            public void run()
            {
                dataMaps = adapter.updateDataMap(DataMaps.listAll(DataMaps.class), title);
                if (dataMaps.size() > 0)
                {
                    BaseMap base = new BaseMap();
                    base.createBase(dataMaps);
                    List<DataMaps> normal = getDataMapReal(dataMaps, getResources().getText(R.string.normal).toString());
                    List<DataMaps> lightweight = getDataMapReal(dataMaps, getResources().getText(R.string.lightweight).toString());
                    List<DataMaps> light = getDataMapReal(dataMaps, getResources().getText(R.string.light).toString());
                    normal = getDataMapOfDate(normal, day, "normal", state_map_full);
                    lightweight = getDataMapOfDate(lightweight, day, "lightweight", state_map_full);
                    light = getDataMapOfDate(light, day, "light", state_map_full);
                    normal = getDataMapOfType(normal, type);
                    lightweight = getDataMapOfType(lightweight, type);
                    light = getDataMapOfType(light, type);
                    normal = getDataMapOfPosition(normal, position, positionMenu);
                    lightweight = getDataMapOfPosition(lightweight, position, positionMenu);
                    light  = getDataMapOfPosition(light, position, positionMenu);
                    adapter.setListUpdateItem(normal, lightweight, light, descriptionText, positionText);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem;
        switch (day)
        {
            case 0:
                menuItem = menu.findItem(R.id.on_today_time);
                menuItem.setChecked(true);
                break;

            case 3:
                menuItem = menu.findItem(R.id.on_three_days_time);
                menuItem.setChecked(true);
                break;

            case 7:
                menuItem = menu.findItem(R.id.on_seven_days_time);
                menuItem.setChecked(true);
                break;
        }
        if (state_map_full)
        {
            menuItem = menu.findItem(R.id.all_time);
            menuItem.setChecked(true);
        }

        if (save_filter)
        {
            menuItem = menu.findItem(R.id.save_filter);
            menuItem.setChecked(true);
        }

        switch (type)
        {
            case "all":
                menuItem = menu.findItem(R.id.all_type);
                menuItem.setChecked(true);
                break;

            case "on_today":
                menuItem = menu.findItem(R.id.on_today_type);
                menuItem.setChecked(true);
                break;

            case "overdue":
                menuItem = menu.findItem(R.id.overdue_type);
                menuItem.setChecked(true);
                break;

            case "completed":
                menuItem = menu.findItem(R.id.completed_type);
                menuItem.setChecked(true);
                break;
        }

        if (positionMenu.size() > 2)
        {
            int SUB_MENU = menu.size();
            menu.addSubMenu(getResources().getText(R.string.position_menu));
            Menu subMenu = menu.getItem(SUB_MENU).getSubMenu();
            int SUB_GROUP = subMenu.size();
            for (Map.Entry entry : positionMenu.entrySet())
            {
                String position = entry.getValue().toString();
                int key = (int)entry.getKey();
                subMenu.add(SUB_GROUP, key, Menu.NONE, position);
            }
            subMenu.setGroupCheckable(SUB_GROUP,true,true);
            if (!getStatePositionMenu(getPositionMenu(DataMaps.listAll(DataMaps.class)), positionMenu))
            {
                position = 1;
            }
            for (Map.Entry entry : positionMenu.entrySet())
            {
                int key = (int)entry.getKey();
                if (key == position)
                {
                    menuItem = subMenu.findItem(key);
                    menuItem.setChecked(true);
                }
            }
        }
        if (dataMaps.size() > 0)
        {
            BaseMap base = new BaseMap();
            base.createBase(dataMaps);
        }
        return true;
    }

    private Map<Integer, String> getPositionMenu(List<DataMaps> dataMap)
    {
        Map<Integer, String> list = new LinkedHashMap<>();
        int count = 0;
        for (int i = 0; i < dataMap.size(); i++)
        {
            String position = dataMap.get(i).getPosition();
            if (list.size() == 0)
            {
                count++;
                String all = getResources().getText(R.string.all).toString();
                list.put(count, all);
                count++;
                list.put(count, position);
            }
            else
            {
                String positionOld = "";
                for (Map.Entry entry : list.entrySet())
                {
                    positionOld = entry.getValue().toString();
                }
                if (!positionOld.equals(position))
                {
                    count++;
                    list.put(count, position);
                }
            }
        }
        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_refresh_main:
                if (internet.isOnline(getApplicationContext()))
                {
                    getDownloadMap();
                }
                else
                {
                    showToastMessage(getResources().getText(R.string.not_network).toString());
                }
                break;

            case R.id.on_today_time:
                day = 0;
                state_map_full = false;
                item.setChecked(true);
                setFilter();
                break;

            case R.id.on_three_days_time:
                day = 3;
                state_map_full = false;
                item.setChecked(true);
                setFilter();
                break;

            case R.id.on_seven_days_time:
                day = 7;
                state_map_full = false;
                item.setChecked(true);
                setFilter();
                break;

            case R.id.all_time:
                state_map_full = true;
                item.setChecked(true);
                setFilter();
                break;

            case R.id.all_type:
                type = "all";
                item.setChecked(true);
                setFilter();
                break;

            case R.id.on_today_type:
                type = "on_today";
                item.setChecked(true);
                setFilter();
                break;

            case R.id.overdue_type:
                type = "overdue";
                item.setChecked(true);
                setFilter();
                break;

            case R.id.completed_type:
                type = "completed";
                item.setChecked(true);
                setFilter();
                break;

            case R.id.save_filter:
                if (item.isChecked())
                {
                    save_filter = false;
                    item.setChecked(false);
                }
                else
                {
                    save_filter = true;
                    item.setChecked(true);
                }
                break;

            default:
                if (id > 0)
                {
                    for (Map.Entry entry : positionMenu.entrySet())
                    {
                        int key = (int)entry.getKey();
                        if (key == id)
                        {
                            position = id;
                            item.setChecked(true);
                            setFilter();
                        }
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean getStatePositionMenu(Map<Integer, String> positionMenuOld, Map<Integer, String> positionMenu)
    {
        if (positionMenuOld.size() == positionMenu.size())
        {
            for (Map.Entry entry : positionMenu.entrySet())
            {
                int key = (int)entry.getKey();
                String value = entry.getValue().toString();
                if (value.equals(positionMenuOld.get(key)))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
        return false;
    }

    private List<DataMaps> getDataMapOfPosition(List<DataMaps> list, int pos, Map<Integer, String> positionMenu)
    {
        List<DataMaps> listRez = new ArrayList<>();
        if (pos > 1)
        {
            String position = positionMenu.get(pos);
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).getPosition().equals(position))
                {
                    listRez.add(list.get(i));
                }
            }
        }
        else
        {
            listRez = list;
        }
        return listRez;
    }

    private List<DataMaps> getDataMapOfType(List<DataMaps> list, String type)
    {
        List<DataMaps> listRez = new ArrayList<>();
        if (!type.equals("all"))
        {
            switch (type)
            {
                case "on_today":
                    for (int i = 0; i < list.size(); i++)
                    {
                        String stitchedText = list.get(i).getStitched();
                        if (stitchedText.contains("[true]"))
                        {
                            listRez.add(list.get(i));
                        }
                    }
                    break;

                case "overdue":
                    for (int i = 0; i < list.size(); i++)
                    {
                        try
                        {
                            String stitchedText = list.get(i).getStitched();
                            stitchedText = stitchedText.replace("[true]", "");
                            int stitched = 0;
                            if (stitchedText.length() > 0)
                            {
                                stitched = Integer.parseInt(stitchedText);
                            }
                            if (stitched > 0)
                            {
                                listRez.add(list.get(i));
                            }
                        }
                        catch (Exception e)
                        {}
                    }
                    break;

                case "completed":
                    for (int i = 0; i < list.size(); i++)
                    {
                        String stitchedText = list.get(i).getStitched();
                        if (stitchedText.contains("[false]"))
                        {
                            listRez.add(list.get(i));
                        }
                    }
                    break;
            }
        }
        else
        {
            listRez = list;
        }
        return listRez;
    }

    private List<DataMaps> getDataMapOfDate(List<DataMaps> dataMap, int day, String type, boolean state_full)
    {
        List<DataMaps> dataRez = new ArrayList<>();

        Date date = new Date();
        Calendar calendar_real = Calendar.getInstance();
        calendar_real.setTime(date);
        calendar_real.set(Calendar.HOUR_OF_DAY, 0);
        calendar_real.set(Calendar.MINUTE, 0);
        calendar_real.set(Calendar.SECOND, 0);
        calendar_real.set(Calendar.MILLISECOND, 0);

        Calendar calendar_plus = Calendar.getInstance();
        calendar_plus.setTime(date);
        calendar_plus.add(Calendar.DATE, day);
        calendar_plus.set(Calendar.HOUR_OF_DAY, 0);
        calendar_plus.set(Calendar.MINUTE, 0);
        calendar_plus.set(Calendar.SECOND, 0);
        calendar_plus.set(Calendar.MILLISECOND, 0);

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.UK);
        Date convertedDate;
        for (int i = 0; i < dataMap.size(); i++)
        {
            String dateMap = dataMap.get(i).getDate();
            String normal = dataMap.get(i).getNormal().replace(",", ".");
            String lightweight = dataMap.get(i).getLightweight().replace(",", ".");
            String light = dataMap.get(i).getLight().replace(",", ".");
            float period = 0;
            if (type.equals("normal"))
            {
                if (normal.length() > 0)
                {
                    period = Float.parseFloat(normal);
                }
            }
            if (type.equals("lightweight"))
            {
                if (lightweight.length() > 0)
                {
                    period = Float.parseFloat(lightweight);
                }
            }
            if (type.equals("light"))
            {
                if (lightweight.length() > 0)
                {
                    period = Float.parseFloat(light);
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
                int id = dataMap.get(i).getIdMap();
                String code = dataMap.get(i).getCode();
                String general = dataMap.get(i).getGeneral();
                String relative = dataMap.get(i).getRelative();
                String description = dataMap.get(i).getDescription();
                normal = dataMap.get(i).getNormal();
                lightweight = dataMap.get(i).getLightweight();
                light = dataMap.get(i).getLight();
                String dateOld = dataMap.get(i).getDate();
                String comment_manager = dataMap.get(i).getComment_manager();
                String comment_performer = dataMap.get(i).getComment_performer();
                String position = dataMap.get(i).getPosition();
                String name_table = dataMap.get(i).getName_table();

                if (dateMap.length() == 0)
                {
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
                    String dateNew =  daysReal + "." + monthReal + "." + year;

                    String state_performance = dateNew + " (" + getResources().getText(R.string.today) + ")";
                    dataRez.add(setDataMap(id, code, general, relative, description, normal, lightweight, light,
                            dateOld, comment_manager, comment_performer, position, name_table, state_performance, "[true]"));//виконати сьогодні
                }
                else
                {
                    try
                    {
                        convertedDate = dateFormat.parse(dateMap);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(convertedDate);
                        calendar.add(Calendar.DATE, day_period);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        int state_map = calendar.compareTo(calendar_real);

                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int days = calendar.get(Calendar.DAY_OF_MONTH);
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
                        String dateNew =  daysReal + "." + monthReal + "." + year;

                        if (state_map == -1)
                        {
                            year = calendar_real.get(Calendar.YEAR);
                            month = calendar_real.get(Calendar.MONTH) + 1;
                            days = calendar_real.get(Calendar.DAY_OF_MONTH);
                            monthReal = month + "";
                            if (monthReal.length() == 1)
                            {
                                monthReal = "0" + month;
                            }
                            daysReal = days + "";
                            if (daysReal.length() == 1)
                            {
                                daysReal = "0" + days;
                            }

                            long milliseconds = calendar_real.getTime().getTime() - calendar.getTime().getTime();
                            int day_stitched = (int) (milliseconds / (24 * 60 * 60 * 1000));

                            dateNew =  daysReal + "." + monthReal + "." + year;
                            String state_performance = dateNew + " (" + getResources().getText(R.string.today) + ", "
                                + getResources().getText(R.string.stitched) + " - " + day_stitched + " "
                                    + getResources().getText(R.string.days)+ ")";

                            dataRez.add(setDataMap(id, code, general, relative, description, normal, lightweight, light,
                                    dateOld, comment_manager, comment_performer, position, name_table, state_performance, day_stitched + "[true]")); //не винонано
                        }
                        else
                        if (state_map == 0)
                        {
                            String state_performance = dateNew + " (" + getResources().getText(R.string.today) + ")";

                            dataRez.add(setDataMap(id, code, general, relative, description, normal, lightweight, light,
                                    dateOld, comment_manager, comment_performer, position, name_table, state_performance, "[true]"));//виконати сьогодні
                        }
                        else
                        if (state_map == 1)
                        {
                            if (day > 0 || state_full)
                            {
                                int state_map_plus = calendar.compareTo(calendar_plus);
                                if (state_map_plus == -1 || state_map_plus == 0 || state_full)
                                {
                                    dataRez.add(setDataMap(id, code, general, relative, description, normal, lightweight, light,
                                            dateOld, comment_manager, comment_performer, position, name_table, dateNew, "[false]"));//виконати пізніше на day днів
                                }
                            }
                        }
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return dataRez;
    }

    private DataMaps setDataMap(int id, String code, String general, String relative, String description, String normal,
    String lightweight, String light, String dateOld, String comment_manager,String comment_prerformer , String position, String name_table,
                                String state_performance, String stitched)
    {
        DataMaps dataMapReal = new DataMaps();

        dataMapReal.setIdMap(id);
        dataMapReal.setCode(code);
        dataMapReal.setGeneral(general);
        dataMapReal.setRelative(relative);
        dataMapReal.setDescription(description);
        dataMapReal.setNormal(normal);
        dataMapReal.setLightweight(lightweight);
        dataMapReal.setLight(light);
        dataMapReal.setDate(dateOld);
        dataMapReal.setComment_manager(comment_manager);
        dataMapReal.setComment_performer(comment_prerformer);
        dataMapReal.setPosition(position);
        dataMapReal.setName_table(name_table);
        dataMapReal.setState_performance(state_performance);
        dataMapReal.setStitched(stitched);
        return dataMapReal;
    }

    private List<DataMaps> getDataMapReal(List<DataMaps> dataMap, String title)
    {
        List<DataMaps> dataRez = new ArrayList<>();
        for (int i = 0; i < dataMap.size(); i++)
        {
            if (title.equals(this.getResources().getText(R.string.normal)))
            {
                if (dataMap.get(i).getNormal() != null)
                {
                    if (dataMap.get(i).getNormal().length() > 0)
                    {
                        dataRez.add(dataMap.get(i));
                    }
                }
            }
            else
            if (title.equals(this.getResources().getText(R.string.lightweight)))
            {
                if (dataMap.get(i).getLightweight() != null)
                {
                    if (dataMap.get(i).getLightweight().length() > 0)
                    {
                        dataRez.add(dataMap.get(i));
                    }
                }
            }
            else
            if (title.equals(this.getResources().getText(R.string.light)))
            {
                if (dataMap.get(i).getLight() != null)
                {
                    if (dataMap.get(i).getLight().length() > 0)
                    {
                        dataRez.add(dataMap.get(i));
                    }
                }
            }
        }
        return dataRez;
    }
}
