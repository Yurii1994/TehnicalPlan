package com.muv.technicalplan.linking;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.muv.technicalplan.CircleImage;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.base.BaseLinking;
import com.muv.technicalplan.data.DataLinking;
import com.muv.technicalplan.data.DataPosition;
import com.muv.technicalplan.data.DataSearch;
import com.muv.technicalplan.data.DataUser;
import com.silencedut.expandablelayout.ExpandableLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AdapterRecyclerSearch extends RecyclerView.Adapter<AdapterRecyclerSearch.NBUViewHolder>
{
    private ConstantUrl url = new ConstantUrl();
    private List<DataSearch> data = new ArrayList<>();
    private List<DataPosition> dataPosition = new ArrayList<>();
    private List<List<DataPosition>> dataPositions = new ArrayList<>();
    private JsonParser jsonParser = new JsonParser();
    private Context context;
    private LinkingActivity activity;
    private List<DataUser> user = DataUser.listAll(DataUser.class);
    private int type_account = user.get(0).getType_account();
    private boolean state_linking;
    private HashSet<Integer> mExpandedPositionSet = new HashSet<>();

    public boolean getStateLinking()
    {
        return state_linking;
    }

    public void setStateLinking(boolean state)
    {
        state_linking = state;
    }

    @Override
    public NBUViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_search, parent, false);
        return new NBUViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NBUViewHolder holder, final int position)
    {
        String nameImage = data.get(position).getImage();
        if (!nameImage.equals(""))
        {
            Picasso.with(context)
                    .load(url.getUrlDownloadImage(nameImage))
                    .placeholder(R.drawable.profile_img)
                    .error(R.drawable.profile_img)
                    .transform(new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {

                            Bitmap result = new CircleImage(context).transform(source);
                            if (result != source) {
                                source.recycle();
                            }
                            return result;
                        }

                        @Override
                        public String key() {
                            return  "circle()";
                        }
                    })
                    .into(holder.photo);
        }
        final String name = data.get(position).getSurname() + " " + data.get(position).getName() + " " + data.get(position).getSurname_father();
        holder.name.setText(name);
        final String enterprise = data.get(position).getEnterprise();
        if (enterprise.equals("false"))
        {
            if (type_account == 1)
            {
                holder.name_enterprise.setText(context.getText(R.string.not_linked));
            }
            else
            {
                holder.name_enterprise.setText(context.getText(R.string.not_enterprise));
            }
        }
        else
        {
            holder.name_enterprise.setText(enterprise);
        }

        if (type_account == 2)
        {
            holder.more.setText(context.getText(R.string.more_performer));
        }
        else
        {
            holder.more.setText(context.getText(R.string.more_manager));
        }

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String positions = "";
                int code_position = 0;
                try
                {
                    positions =  holder.spinner.getSelectedItem().toString();
                    code_position = holder.spinner.getSelectedItemPosition();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                if (positions.equals(context.getText(R.string.not_position)))
                {
                    Toast toast = Toast.makeText(context, context.getText(R.string.linking_error),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                if (positions.equals(""))
                {
                    Toast toast = Toast.makeText(context, context.getText(R.string.position_error),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    String where = data.get(position).getLogin();
                    String from = user.get(0).getLogin();
                    String enterprise = "";
                    if (type_account == 1)
                    {
                        enterprise = user.get(0).getEnterprise();
                    }
                    else
                    if (type_account == 2)
                    {
                        enterprise = data.get(position).getEnterprise();
                    }
                    if (type_account == 1)
                    {
                        dataPosition = getDataPosition(dataPositions, from);
                    }
                    else
                    if (type_account == 2)
                    {
                        dataPosition = getDataPosition(dataPositions, where);
                    }
                    String code = dataPosition.get(code_position).getCode();
                    String name_table = dataPosition.get(code_position).getName_table();

                    List<DataLinking> dataLinkings = new ArrayList<>();
                    DataLinking dataLinking = new DataLinking();
                    dataLinking.setWhere_user(where);
                    dataLinking.setFrom_user(from);
                    dataLinking.setEnterprise(enterprise);
                    dataLinking.setPosition(positions);
                    dataLinking.setCode(code);
                    dataLinking.setState("");
                    dataLinking.setName_table(name_table);
                    dataLinkings.add(dataLinking);

                    try
                    {
                        String urlLinking;
                        urlLinking = url.getUrlSetLinking(where, from, enterprise, positions, code, name_table);
                        Linking linking = new Linking(urlLinking, dataLinkings);
                        linking.execute();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        holder.updateItem(position, enterprise, holder);

    }

    private List<DataPosition> getDataPosition(List<List<DataPosition>> data, String login)
    {
        List<DataPosition> listRes = new ArrayList<>();
        for (int i = 0; i < data.size(); i++)
        {
            List<DataPosition> list = data.get(i);
            for (int j = 0; j < list.size(); j++)
            {
                if (login.equals(list.get(j).getLogin()))
                {
                    listRes = list;
                }
                else
                {
                    break;
                }
            }
        }
        return listRes;
    }

    class Linking extends AsyncTask<Void, Void, Void> {

        private String url;
        private boolean linking;
        private List<DataLinking> dataLinking;

        public Linking(String url, List<DataLinking> dataLinking)
        {
            this.url = url;
            this.dataLinking = dataLinking;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                linking = jsonParser.parseSetLinking(url);
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
            if (linking)
            {
                activity.getCloseSearch();
                state_linking = true;
                Toast toast = Toast.makeText(context, context.getText(R.string.linked),
                        Toast.LENGTH_SHORT);
                toast.show();
                BaseLinking baseLinking = new BaseLinking();
                baseLinking.createBase(dataLinking);
            }
            else
            {
                state_linking = false;
                Toast toast = Toast.makeText(context, context.getText(R.string.not_linking),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class NBUViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        ImageView photo;
        TextView name;
        TextView name_enterprise;
        Button more;
        Spinner spinner;
        RelativeLayout progress;
        TextView hint;
        ExpandableLayout expandableLayout;

        public NBUViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_search);
            photo = (ImageView)itemView.findViewById(R.id.manager_photo);
            name = (TextView)itemView.findViewById(R.id.name_manager);
            name_enterprise = (TextView)itemView.findViewById(R.id.name_enterprise);
            more = (Button)itemView.findViewById(R.id.manager_more);
            progress = (RelativeLayout) itemView.findViewById(R.id.progress_position);
            spinner = (Spinner)itemView.findViewById(R.id.spinner_position);
            hint = (TextView) itemView.findViewById(R.id.position_hint);
            expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout_search);
        }

        private void updateItem(final int position, final String enterprise, final NBUViewHolder holder) {
            expandableLayout.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
                @Override
                public void onExpand(boolean expanded)
                {
                    if (!enterprise.equals("false") || type_account == 1)
                    {
                        if (type_account == 1)
                        {
                            String login = user.get(0).getLogin();
                            Position position = new Position(url.getUrlGetPosition(login), holder.progress, holder.spinner, holder.hint);
                            position.execute();
                        }
                        else
                        if (type_account == 2)
                        {
                            String login = data.get(position).getLogin();
                            Position position = new Position(url.getUrlGetPosition(login), holder.progress, holder.spinner, holder.hint);
                            position.execute();
                        }
                    }
                    registerExpand(position);
                }
            });
            expandableLayout.setExpand(mExpandedPositionSet.contains(position));
        }
    }


    private void registerExpand(int position) {
        if (mExpandedPositionSet.contains(position)) {
            removeExpand(position);
        }else {
            addExpand(position);
        }
    }

    private void removeExpand(int position) {
        mExpandedPositionSet.remove(position);
    }

    private void addExpand(int position) {
        mExpandedPositionSet.add(position);
    }

    public void setRefresh()
    {
        dataPositions.clear();
    }

    class Position extends AsyncTask<Void, Void, Void> {

        private String url;
        private View progress;
        private Spinner spinner;
        private TextView hint;

        public Position(String url, View progress, Spinner spinner, TextView hint)
        {
            this.url = url;
            this.progress = progress;
            this.spinner = spinner;
            this.hint = hint;
            progress.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
            hint.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                dataPosition = jsonParser.parseGetPosition(url);
                dataPositions.add(dataPosition);
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
            progress.setVisibility(View.GONE);
            ArrayList<String> position = new ArrayList<>();
            for (int i = 0;i < dataPosition.size(); i++)
            {
                position.add(dataPosition.get(i).getPosition());
                if (i == dataPosition.size() - 1)
                {
                    position.add(context.getText(R.string.not_position).toString());
                }
            }
            if (position.size() > 0)
            {
                spinner.setVisibility(View.VISIBLE);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, position){

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        View v = super.getView(position, convertView, parent);
                        if (position == getCount())
                        {
                            ((TextView)v.findViewById(R.id.text1)).setText("");
                            ((TextView)v.findViewById(R.id.text1)).setHint(getItem(getCount()));
                        }
                        return v;
                    }
                    @Override
                    public int getCount() {
                        return super.getCount()-1;
                    }

                };
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(adapter.getCount());
            }
            else
            {
                hint.setVisibility(View.VISIBLE);
            }
        }
    }


    public AdapterRecyclerSearch(List<DataSearch> data, Context context, LinkingActivity activity)
    {
        this.data = data;
        this.context = context;
        this.activity = activity;
    }

    public void setData(List<DataSearch> data)
    {
        this.data = data;
    }
}
