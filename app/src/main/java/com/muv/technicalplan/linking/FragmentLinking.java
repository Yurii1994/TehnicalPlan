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
import com.muv.technicalplan.base.BaseLinking;
import com.muv.technicalplan.base.BaseUsers;
import com.muv.technicalplan.data.DataLinking;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.data.DataUsers;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

public class FragmentLinking extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.fragment_linking;
    private LinkingActivity activity;
    private RecyclerView recyclerView;
    private AdapterRecyclerLinking adapter;

    private ProgressWheel progressWheel;
    private TextView hint;
    private Internet internet = new Internet();
    private ConstantUrl url = new ConstantUrl();
    private JsonParser jsonParser = new JsonParser();
    private List<DataUser> user = DataUser.listAll(DataUser.class);
    private String login = user.get(0).getLogin();

    public static FragmentLinking getInstance(Context context, LinkingActivity activity)
    {
        Bundle args = new Bundle();
        FragmentLinking fragment = new FragmentLinking();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.links));
        fragment.setLinkingActivity(activity);
        return fragment;
    }

    private void setLinkingActivity(LinkingActivity activity)
    {
        this.activity = activity;
    }

    public void setVisibleHint()
    {
        hint.setVisibility(View.VISIBLE);
        progressWheel.setVisibility(View.GONE);
    }

    public void createActivity()
    {
        recyclerView.removeAllViews();
        adapter.setData(new ArrayList<DataUsers>(), new ArrayList<DataLinking>());
        adapter.notifyDataSetChanged();
        hint.setVisibility(View.VISIBLE);
        progressWheel.setVisibility(View.GONE);
        if (internet.isOnline(context))
        {
            String urlWhere = url.getUrlGetLinkingWhere(user.get(0).getLogin());
            String urlFrom = url.getUrlGetLinkingFrom(user.get(0).getLogin());
            Linking linking = new Linking(urlWhere, urlFrom);
            linking.execute();
        }
        else
        {
            List<DataUsers> users = DataUsers.listAll(DataUsers.class);
            List<DataLinking> linking = DataLinking.listAll(DataLinking.class);
            getLinked(linking, users);
            Toast toast = Toast.makeText(context, getResources().getText(R.string.not_network),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    class Linking extends AsyncTask<Void, Void, Void> {

        private String urlWhere;
        private String urlFrom;
        List<DataLinking> linking = new ArrayList<>();
        List<DataLinking> linkingWhere;
        List<DataLinking> linkingFrom;

        public Linking(String urlWhere, String urlFrom)
        {
            this.urlWhere = urlWhere;
            this.urlFrom = urlFrom;
            hint.setVisibility(View.GONE);
            progressWheel.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                linkingWhere = jsonParser.parseGetLinking(urlWhere);
                linkingFrom = jsonParser.parseGetLinking(urlFrom);
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
            for (int i = 0; i < linkingWhere.size(); i++)
            {
                linking.add(linkingWhere.get(i));
            }
            for (int i = 0; i < linkingFrom.size(); i++)
            {
                linking.add(linkingFrom.get(i));
            }
            linking = getSorted(linking);
            LinkingUser linkingUser = new LinkingUser(linking);
            linkingUser.execute();
        }
    }

    private List<DataLinking> getSorted(List<DataLinking> linking)
    {
        List<DataLinking> list = new ArrayList<>();
        for (int i = 0; i < linking.size(); i++)
        {
            if (linking.get(i).getState().equals(""))
            {
                list.add(linking.get(i));
            }
        }
        for (int i = 0; i < linking.size(); i++)
        {
            if (linking.get(i).getState().equals("false") && linking.get(i).getFrom_user().equals(login))
            {
                list.add(linking.get(i));
            }
        }
        for (int i = 0; i < linking.size(); i++)
        {
            if (linking.get(i).getState().equals("true"))
            {
                list.add(linking.get(i));
            }
        }
        return list;
    }

    class LinkingUser extends AsyncTask<Void, Integer, Void> {

        List<DataLinking> linking;
        DataUsers dataUser;
        List<DataUsers> linkedUser = new ArrayList<>();

        public LinkingUser(List<DataLinking> linking)
        {
            this.linking = linking;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                for (int i = 0; i < linking.size();i++)
                {
                    String loginWhere = linking.get(i).getWhere_user();
                    String loginFrom = linking.get(i).getFrom_user();
                    if (loginWhere.equals(user.get(0).getLogin()))
                    {

                        dataUser = jsonParser.parseUsers(url.getUrlSingInLogin(loginFrom));
                    }
                    else
                    {
                        dataUser = jsonParser.parseUsers(url.getUrlSingInLogin(loginWhere));
                    }
                    linkedUser.add(dataUser);
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
            saveBase(linking, linkedUser);
            getLinked(linking, linkedUser);
        }
    }

    public void saveBase(List<DataLinking> linking, List<DataUsers> linkedUser)
    {
        BaseLinking baseLinking = new BaseLinking();
        baseLinking.createBase(linking);
        BaseUsers baseUsers = new BaseUsers();
        baseUsers.createBase(linkedUser);
    }

    public void getLinked(List<DataLinking> linking, List<DataUsers> linkedUser)
    {
        if (linkedUser.size() > 0)
        {
            hint.setVisibility(View.GONE);
            progressWheel.setVisibility(View.GONE);
        }
        else
        {
            hint.setVisibility(View.VISIBLE);
            progressWheel.setVisibility(View.GONE);
        }
        recyclerView.removeAllViews();
        recyclerView.getRecycledViewPool().clear();
        adapter.setData(linkedUser, linking);
        adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        progressWheel = (ProgressWheel)view.findViewById(R.id.progress_linked);
        hint = (TextView)view.findViewById(R.id.hint_linked);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_linked);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new AdapterRecyclerLinking(new ArrayList<DataUsers>(), new ArrayList<DataLinking>(), context, activity, this, recyclerView);
        recyclerView.setAdapter(adapter);

        createActivity();
        return view;
    }
}