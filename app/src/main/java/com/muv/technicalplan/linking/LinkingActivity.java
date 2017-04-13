package com.muv.technicalplan.linking;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.main.MainActivity;
import com.muv.technicalplan.R;
import com.muv.technicalplan.data.DataSearch;
import com.muv.technicalplan.data.DataUser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class LinkingActivity extends AppCompatActivity
{
    private static final int LAYOUT = R.layout.activity_linking;

    private ViewPager viewPager;
    private Toolbar toolbar;
    private SearchView searchView;
    private MenuItem searchItem;
    private MenuItem refresh;
    private MainActivity mainActivity;
    public TabsPagerFragmentAdapterLinking adapter;
    private boolean stateLinking;

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("StateLinking", getStateLinking());
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initToolbar();
        initTabs();

        if (savedInstanceState != null)
        {
            invalidateOptionsMenu();
            stateLinking = savedInstanceState.getBoolean("StateLinking");
        }
        mainActivity = MainActivity.getMainActivity();
    }


    public void getCloseSearch()
    {
        if (!searchView.isIconified())
        {
            searchView.setIconified(true);
        }
        if (!searchView.isIconified())
        {
            getCloseSearch();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                mainActivity.LinkingNotificationCounter();
                mainActivity.updateStateViewPager();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        disableSearchViewActionMode(searchView);

        AutoCompleteTextView searchTextView = (AutoCompleteTextView)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try
        {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.color_cursor);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                setDataSearches(new ArrayList<DataSearch>());
                getSearch(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        getMenuInflater().inflate(R.menu.menu_linking, menu);
        refresh = menu.findItem(R.id.menu_refresh_linking);
        if (viewPager.getCurrentItem() == 0)
        {
            searchItem.setVisible(false);
            refresh.setVisible(true);
        }
        else
        {
            searchItem.setVisible(true);
            refresh.setVisible(false);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                if (position == 0)
                {
                    if (getStateLinking() || stateLinking)
                    {
                        setStateLinking(false);
                        createActivityLinking(true);
                        stateLinking = false;
                    }
                    setDataSearches(new ArrayList<DataSearch>());
                    refresh.setVisible(true);
                    searchItem.setVisible(false);
                    getCloseSearch();
                }
                else
                {
                    searchItem.setVisible(true);
                    refresh.setVisible(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return true;
    }

    public void searchViewClearFocus()
    {
        if (searchView != null)
        {
            searchView.clearFocus();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void disableSearchViewActionMode(SearchView searchView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ((EditText) searchView.findViewById(R.id.search_src_text)).setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
    }

    private void initToolbar()
    {
        if (toolbar == null)
        {
            toolbar = (Toolbar) findViewById(R.id.toolbar_linking);
            toolbar.setTitle(R.string.linking_name);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null)
            {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            toolbar.inflateMenu(R.menu.menu_linking);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.menu_refresh_linking:
                            createActivityLinking(true);
                            break;
                    }
                    return true;
                }
            });
        }
    }

    public FragmentSearch getFragmentSearch()
    {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        FragmentSearch fragmentSearch = null;
        for (int i = 0; i < fragments.size(); i++)
        {
            try
            {
                fragmentSearch = (FragmentSearch) fragments.get(i);
                break;
            }
            catch (Exception e)
            {}
        }
        return fragmentSearch;
    }

    public FragmentLinking getFragmentLinking()
    {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        FragmentLinking fragmentLinking = null;
        for (int i = 0; i < fragments.size(); i++)
        {
            try
            {
                fragmentLinking = (FragmentLinking) fragments.get(i);
                break;
            }
            catch (Exception e)
            {}
        }
        return fragmentLinking;
    }

    public void getSearch(String query)
    {
        getFragmentSearch().getSearch(query);
    }

    public void createActivityLinking(boolean update)
    {
        getFragmentLinking().createActivity(update);
    }

    public boolean getStateLinking()
    {
        return getFragmentSearch().getStateLinking();
    }

    public void setStateLinking(boolean state)
    {
        getFragmentSearch().setStateLinking(state);
    }

    public void setDataSearches(List<DataSearch> list)
    {
        getFragmentSearch().setDataSearches(list);
    }

    private void initTabs()
    {
        viewPager = (ViewPager)findViewById(R.id.view_pager_linking);
        adapter = new TabsPagerFragmentAdapterLinking(this, getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout_linking);
        tabLayout.setupWithViewPager(viewPager);

    }
}
