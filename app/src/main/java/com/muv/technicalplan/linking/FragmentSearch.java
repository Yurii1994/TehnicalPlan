package com.muv.technicalplan.linking;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.Internet;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.RecyclerViewMargin;
import com.muv.technicalplan.data.DataSearch;
import com.muv.technicalplan.data.DataUser;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;


public class FragmentSearch extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.fragment_search;
    private RecyclerView recyclerView;
    private AdapterRecyclerSearch adapter;
    private List<DataSearch> dataSearches = new ArrayList<>();
    private LinkingActivity activity;
    private ProgressWheel progressWheel;
    private TextView hint;
    private Internet internet = new Internet();
    private ConstantUrl url = new ConstantUrl();
    private JsonParser jsonParser = new JsonParser();
    private List<DataUser> user = DataUser.listAll(DataUser.class);
    private int type_account = user.get(0).getType_account();
    private RecyclerViewMargin decoration;

    public boolean getStateLinking()
    {
        return adapter.getStateLinking();
    }

    public void setStateLinking(boolean state)
    {
        adapter.setStateLinking(state);
    }

    public static FragmentSearch getInstance(Context context, LinkingActivity activity)
    {
        Bundle args = new Bundle();
        FragmentSearch fragment = new FragmentSearch();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.search));
        fragment.setLinkingActivity(activity);
        return fragment;
    }

    private void setLinkingActivity(LinkingActivity activity)
    {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        progressWheel = (ProgressWheel)view.findViewById(R.id.progress_search);
        hint = (TextView)view.findViewById(R.id.hint_search);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new AdapterRecyclerSearch(dataSearches, context, activity);
        recyclerView.setAdapter(adapter);
        progressWheel.setVisibility(View.GONE);

        return view;
    }

    private int pxFromDp(float dp)
    {
        return (int) Math.ceil(dp * context.getApplicationContext().getResources().getDisplayMetrics().density);
    }

    public void getSearch(String query)
    {
        String name_one = getStringNameOne(query);
        String name_two = getStringNameTwo(query);
        String name_three = getStringNameThree(query);
        int type_search;
        if (type_account == 1)
        {
            type_search = 2;
        }
        else
        {
            type_search = 1;
        }
        if (internet.isOnline(context))
        {
            if (name_one.length() > 0 & name_two.length() > 0 & name_three.length() > 0)
            {
                progressWheel.setVisibility(View.VISIBLE);
                hint.setVisibility(View.GONE);
                try
                {
                    String urlSearch = url.getUrlSearchThree(name_one, name_two, name_three, type_search);
                    Search search = new Search(urlSearch);
                    search.execute();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            if (name_one.length() > 0 & name_two.length() > 0 & name_three.length() == 0)
            {
                progressWheel.setVisibility(View.VISIBLE);
                hint.setVisibility(View.GONE);
                try
                {
                    String urlSearch = url.getUrlSearchTwo(name_one, name_two, type_search);
                    Search search = new Search(urlSearch);
                    search.execute();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            if (name_one.length() > 0 & name_two.length() == 0 & name_three.length() == 0)
            {
                progressWheel.setVisibility(View.VISIBLE);
                hint.setVisibility(View.GONE);
                try
                {
                    String urlSearch = url.getUrlSearchOne(name_one, type_search);
                    Search search = new Search(urlSearch);
                    search.execute();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast toast = Toast.makeText(context, getResources().getText(R.string.not_string),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else
        {
            Toast toast = Toast.makeText(context, getResources().getText(R.string.not_network),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private String getStringNameOne(String text)
    {
        if (text.contains(" "))
        {
            String textRes = "";
            for (int i = 0; i < text.length(); i++)
            {
                char b = text.charAt(i);
                if (b == ' ')
                {
                    break;
                }
                else
                {
                    textRes = textRes + b;
                }
            }
            return  textRes;
        }
        else
        {
            return text;
        }
    }

    private String getStringNameTwo(String text)
    {
        String textRes = "";
        if (text.contains(" "))
        {
            int count = 0;
            for (int i = 0; i < text.length(); i++)
            {
                char b = text.charAt(i);
                if (b == ' ')
                {
                    count++;
                    if (count == 2)
                    {
                        break;
                    }
                }
                else
                if (count == 1)
                {
                    textRes = textRes + b;
                }
            }
        }
        return  textRes;
    }

    private String getStringNameThree(String text)
    {
        String textRes = "";
        if (text.contains(" "))
        {
            int count = 0;
            for (int i = 0; i < text.length(); i++)
            {
                char b = text.charAt(i);
                if (b == ' ')
                {
                    count++;
                }
                else
                if (count == 2)
                {
                    textRes = textRes + b;
                }
            }
        }
        return  textRes;
    }

    class Search extends AsyncTask<Void, Void, Void> {

        private String url;

        public Search(String url)
        {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                dataSearches = jsonParser.parseSearch(url);
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
            if (dataSearches.size() == 0)
            {
                progressWheel.setVisibility(View.GONE);
                hint.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                recyclerView.removeItemDecoration(decoration);
                decoration = new RecyclerViewMargin(pxFromDp(10), dataSearches.size());
                recyclerView.addItemDecoration(decoration);
                Toast toast = Toast.makeText(context, getResources().getText(R.string.search_error),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                adapter.setData(dataSearches);
                adapter.notifyDataSetChanged();
                progressWheel.setVisibility(View.GONE);
                recyclerView.removeItemDecoration(decoration);
                decoration = new RecyclerViewMargin(pxFromDp(10), dataSearches.size());
                recyclerView.addItemDecoration(decoration);
                recyclerView.setVisibility(View.VISIBLE);
                activity.searchViewClearFocus();
            }
        }
    }

    public void setDataSearches(List<DataSearch> list)
    {
        recyclerView.removeAllViews();
        recyclerView.removeItemDecoration(decoration);
        decoration = new RecyclerViewMargin(pxFromDp(10), dataSearches.size());
        recyclerView.addItemDecoration(decoration);
        adapter.setRefresh();
        adapter.setData(list);
        adapter.notifyDataSetChanged();
        hint.setVisibility(View.VISIBLE);
    }
}