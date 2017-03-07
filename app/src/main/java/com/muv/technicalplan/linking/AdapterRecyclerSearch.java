package com.muv.technicalplan.linking;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
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
    private Handler handler = new Handler();

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
        if (enterprise.equals("null"))
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

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                headerHeight.add(holder.cardView.getHeight() - holder.detail.getHeight());
                detailHeight.add(holder.detail.getHeight());
                holder.detail.setVisibility(View.GONE);
                holder.layout_card.setVisibility(View.VISIBLE);
            }
        });

        holder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                System.out.println("DDD " + enterprise + " " + type_account);
                if (!enterprise.equals("null") || type_account == 1)
                {
                    holder.detail.setVisibility(View.VISIBLE);
                    onProductDescriptionClicked(holder.cardView, position);
                    if (state_expand)
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
                }
            }
        });

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
                    String enterprise = data.get(position).getEnterprise();
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

                    List<DataLinking> dataLinkings = new ArrayList<>();
                    DataLinking dataLinking = new DataLinking();
                    dataLinking.setWhere_user(where);
                    dataLinking.setFrom_user(from);
                    dataLinking.setEnterprise(enterprise);
                    dataLinking.setPosition(positions);
                    dataLinking.setCode(code);
                    dataLinking.setState("");
                    dataLinkings.add(dataLinking);

                    try
                    {
                        String urlLinking;
                        if (type_account == 1)
                        {
                            urlLinking = url.getUrlSetLinking(from, where, enterprise, positions, code);
                        }
                        else
                        {
                            urlLinking = url.getUrlSetLinking(where, from, enterprise, positions, code);
                        }
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


    public static class NBUViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        ImageView photo;
        TextView name;
        TextView name_enterprise;
        Button more;
        Spinner spinner;
        LinearLayout layout_card;
        LinearLayout detail;
        LinearLayout header;
        RelativeLayout progress;
        TextView hint;

        public NBUViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_search);
            photo = (ImageView)itemView.findViewById(R.id.manager_photo);
            name = (TextView)itemView.findViewById(R.id.name_manager);
            name_enterprise = (TextView)itemView.findViewById(R.id.name_enterprise);
            more = (Button)itemView.findViewById(R.id.manager_more);
            layout_card = (LinearLayout)itemView.findViewById(R.id.card_layout);
            detail = (LinearLayout)itemView.findViewById(R.id.detail_manager);
            header = (LinearLayout)itemView.findViewById(R.id.manager_header);
            progress = (RelativeLayout) itemView.findViewById(R.id.progress_position);
            spinner = (Spinner)itemView.findViewById(R.id.spinner_position);
            hint = (TextView) itemView.findViewById(R.id.position_hint);
        }
    }

    public void setRefresh()
    {
        headerHeight.clear();
        detailHeight.clear();
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


    private void onProductDescriptionClicked(CardView view, int pos)
    {
        toggleProductDescriptionHeight(view, pos);
    }

    private ArrayList<Integer> detailHeight = new ArrayList<>();
    private ArrayList<Integer> headerHeight = new ArrayList<>();
    private boolean state_expand;

    private void toggleProductDescriptionHeight(final View card, int pos)
    {
        int minHeight = headerHeight.get(pos);
        int maxHeight = minHeight + detailHeight.get(pos);
        int card_height = card.getHeight();
        if (card_height == minHeight)
        {
            ValueAnimator anim = ValueAnimator.ofInt(card.getMeasuredHeightAndState(), maxHeight);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = card.getLayoutParams();
                    layoutParams.height = val;
                    card.setLayoutParams(layoutParams);
                }
            });
            state_expand = true;
            anim.start();
        }
        else
        {
            ValueAnimator anim = ValueAnimator.ofInt(card.getMeasuredHeightAndState(), minHeight);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = card.getLayoutParams();
                    layoutParams.height = val;
                    card.setLayoutParams(layoutParams);
                }
            });
            state_expand = false;
            anim.start();
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
