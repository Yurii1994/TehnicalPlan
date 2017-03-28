package com.muv.technicalplan.linking;


import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.widget.TextView;

import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.data.DataLinking;
import com.muv.technicalplan.data.DataUser;

import java.util.ArrayList;
import java.util.List;

public class LinkingNotificationCounter
{
    private NavigationView navigationView;
    private int item_id;
    List<DataUser> user = DataUser.listAll(DataUser.class);

    public LinkingNotificationCounter(NavigationView navigationView, int item_id)
    {
        this.navigationView = navigationView;
        this.item_id = item_id;
    }

    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        view.setText(count > 0 ? String.valueOf(count) : null);
    }

    public void getNotificationCount()
    {
        ConstantUrl url = new ConstantUrl();
        if (user.size() > 0)
        {
            String urlWhere = url.getUrlGetLinkingWhere(user.get(0).getLogin());
            String urlFrom = url.getUrlGetLinkingFrom(user.get(0).getLogin());
            Linking linking = new Linking(urlWhere, urlFrom);
            linking.execute();
        }
    }

    class Linking extends AsyncTask<Void, Void, Void> {

        private String urlWhere;
        private String urlFrom;
        List<DataLinking> linking = new ArrayList<>();
        List<DataLinking> linkingWhere;
        List<DataLinking> linkingFrom;
        JsonParser jsonParser = new JsonParser();

        public Linking(String urlWhere, String urlFrom)
        {
            this.urlWhere = urlWhere;
            this.urlFrom = urlFrom;
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
            if (linkingWhere != null)
            {
                for (int i = 0; i < linkingWhere.size(); i++)
                {
                    linking.add(linkingWhere.get(i));
                }
                for (int i = 0; i < linkingFrom.size(); i++)
                {
                    linking.add(linkingFrom.get(i));
                }
                linking = getSorted(linking);
                List<DataLinking> old_linking = DataLinking.listAll(DataLinking.class);
                old_linking = getSorted(old_linking);
                if (linking.size() == 0 & old_linking.size() == 0)
                {
                    setMenuCounter(item_id, 0);
                }
                else
                {
                    setMenuCounter(item_id, linking.size() - old_linking.size());
                }
            }
        }
    }

    private List<DataLinking> getSorted(List<DataLinking> linking)
    {
        List<DataLinking> list = new ArrayList<>();
        for (int i = 0; i < linking.size(); i++)
        {
            if (linking.get(i).getState().equals("") & !linking.get(i).getFrom_user().equals(user.get(0).getLogin()))
            {
                list.add(linking.get(i));
            }
        }
        return list;
    }
}
