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
import com.muv.technicalplan.base.BaseLinking;
import com.muv.technicalplan.base.BaseUsers;
import com.muv.technicalplan.data.DataLinking;
import com.muv.technicalplan.data.DataMaps;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.data.DataUsers;
import com.muv.technicalplan.main.MapParcelable;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

public class FragmentLinking extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.fragment_linking;
    private RecyclerView recyclerView;
    private AdapterRecyclerLinking adapter;
    private ProgressWheel progressWheel;
    private TextView hint;
    private Internet internet = new Internet();
    private ConstantUrl url = new ConstantUrl();
    private JsonParser jsonParser = new JsonParser();
    private List<DataUser> user = DataUser.listAll(DataUser.class);
    private String login = user.get(0).getLogin();
    private RecyclerViewMargin decoration;
    private ArrayList<UsersParcelable> usersParcelables = new ArrayList<>();
    private ArrayList<LinkingParcelable> linkingParcelables = new ArrayList<>();
    private Bundle savedInstanceState;

    public static FragmentLinking getInstance(Context context)
    {
        Bundle args = new Bundle();
        FragmentLinking fragment = new FragmentLinking();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.links));
        return fragment;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("UsersParcelable", usersParcelables);
        outState.putParcelableArrayList("LinkingParcelable", linkingParcelables);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        context = getContext();

        progressWheel = (ProgressWheel)view.findViewById(R.id.progress_linked);
        hint = (TextView)view.findViewById(R.id.hint_linked);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_linked);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new AdapterRecyclerLinking(new ArrayList<DataUsers>(), new ArrayList<DataLinking>(), context, this, recyclerView);
        recyclerView.setAdapter(adapter);

        this.savedInstanceState = savedInstanceState;
        createActivity(false);
        return view;
    }


    public void setVisibleHint()
    {
        hint.setVisibility(View.VISIBLE);
        progressWheel.setVisibility(View.GONE);
    }

    public void createActivity(boolean update)
    {
        recyclerView.removeAllViews();
        adapter.setData(new ArrayList<DataUsers>(), new ArrayList<DataLinking>());
        adapter.notifyDataSetChanged();
        hint.setVisibility(View.VISIBLE);
        progressWheel.setVisibility(View.GONE);
        if (savedInstanceState != null & !update)
        {
            usersParcelables = savedInstanceState.getParcelableArrayList("UsersParcelable");
            linkingParcelables = savedInstanceState.getParcelableArrayList("LinkingParcelable");
            List<DataUsers> users = getDataUsers(usersParcelables);
            List<DataLinking> linking = getDataLinking(linkingParcelables);
            getLinked(linking, users);
        }
        else
        {
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
    }

    private List<DataUsers> getDataUsers(ArrayList<UsersParcelable> usersParcelables)
    {
        List<DataUsers> dataUsers = new ArrayList<>();
        for (int i = 0; i < usersParcelables.size(); i++)
        {
            DataUsers dataUser = new DataUsers();
            dataUser.setName(usersParcelables.get(i).name);
            dataUser.setSurname(usersParcelables.get(i).surname);
            dataUser.setSurname_father(usersParcelables.get(i).surname_father);
            dataUser.setEnterprise(usersParcelables.get(i).enterprise);
            dataUser.setPosition(usersParcelables.get(i).position);
            dataUser.setLogin(usersParcelables.get(i).login);
            dataUser.setEmail(usersParcelables.get(i).email);
            dataUser.setImage(usersParcelables.get(i).image);
            dataUsers.add(dataUser);

        }
        return dataUsers;
    }

    private List<DataLinking> getDataLinking(ArrayList<LinkingParcelable> linkingParcelables)
    {
        List<DataLinking> dataLinkings = new ArrayList<>();
        for (int i = 0; i < usersParcelables.size(); i++)
        {
            DataLinking dataLinking = new DataLinking();
            dataLinking.setWhere_user(linkingParcelables.get(i).where_user);
            dataLinking.setFrom_user(linkingParcelables.get(i).from_user);
            dataLinking.setEnterprise(linkingParcelables.get(i).enterprise);
            dataLinking.setPosition(linkingParcelables.get(i).position);
            dataLinking.setCode(linkingParcelables.get(i).code);
            dataLinking.setState(linkingParcelables.get(i).state);
            dataLinking.setName_table(linkingParcelables.get(i).name_table);
            dataLinkings.add(dataLinking);
        }
        return dataLinkings;
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
        usersParcelables = new ArrayList<>();
        linkingParcelables = new ArrayList<>();
        for (int i = 0; i < linking.size(); i++)
        {
            String where_user = linking.get(i).getWhere_user();
            String from_user = linking.get(i).getFrom_user();
            String enterprise = linking.get(i).getEnterprise();
            String position = linking.get(i).getPosition();
            String code = linking.get(i).getCode();
            String state = linking.get(i).getState();
            String name_table = linking.get(i).getName_table();
            linkingParcelables.add(new LinkingParcelable(where_user, from_user, enterprise, position, code, state, name_table));
        }
        for (int i = 0; i < linkedUser.size(); i++)
        {
            String name = linkedUser.get(i).getName();
            String surname = linkedUser.get(i).getSurname();
            String surname_father = linkedUser.get(i).getSurname_father();
            String enterprise = linkedUser.get(i).getEnterprise();
            String position = linkedUser.get(i).getPosition();
            String login = linkedUser.get(i).getLogin();
            String email = linkedUser.get(i).getEmail();
            String image = linkedUser.get(i).getImage();
            usersParcelables.add(new UsersParcelable(name, surname, surname_father, enterprise, position, login, email, image));
        }
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
        recyclerView.removeItemDecoration(decoration);
        decoration = new RecyclerViewMargin(pxFromDp(10), linkedUser.size());
        recyclerView.addItemDecoration(decoration);
        adapter.setData(linkedUser, linking);
        adapter.notifyDataSetChanged();
    }

    private int pxFromDp(float dp)
    {
        return (int) Math.ceil(dp * context.getApplicationContext().getResources().getDisplayMetrics().density);
    }
}