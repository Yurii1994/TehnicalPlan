package com.muv.technicalplan.linking;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muv.technicalplan.CircleImage;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.data.DataLinking;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.data.DataUsers;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;


public class AdapterRecyclerLinking  extends RecyclerView.Adapter<AdapterRecyclerLinking.ViewHolder>
{
    private ConstantUrl url = new ConstantUrl();
    private List<DataUsers> dataUser = new ArrayList<>();
    private List<DataLinking> dataLinking = new ArrayList<>();
    private JsonParser jsonParser = new JsonParser();
    private Context context;
    private LinkingActivity activity;
    private List<DataUser> user = DataUser.listAll(DataUser.class);
    private int type_account = user.get(0).getType_account();
    private String login = user.get(0).getLogin();
    private Handler handler = new Handler();
    private FragmentLinking fragmentLinking;

    @Override
    public AdapterRecyclerLinking.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_linked, parent, false);
        return new AdapterRecyclerLinking.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final AdapterRecyclerLinking.ViewHolder holder, final int position)
    {
        String nameImage = dataUser.get(position).getImage();
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
        final String name = dataUser.get(position).getSurname() + " " + dataUser.get(position).getName() + " " + dataUser.get(position).getSurname_father();
        holder.name.setText(name);

        String enterprise = dataUser.get(position).getEnterprise();
        if (enterprise == null || enterprise.equals("null"))
        {
            if (type_account == 1)
            {
                enterprise = context.getText(R.string.not_enterprise).toString();
            }
            else
            if (type_account == 2)
            {
                enterprise = dataLinking.get(position).getEnterprise();
            }
        }
        if (dataLinking.get(position).getState().equals(""))
        {
            enterprise = enterprise + " " +context.getText(R.string.not_link);
        }
        if (dataLinking.get(position).getState().equals("false"))
        {
            enterprise = enterprise + " " +context.getText(R.string.link_error);
        }
        if (dataLinking.get(position).getState().equals("true"))
        {
            enterprise = enterprise + " " +context.getText(R.string.link);
        }
        holder.name_enterprise.setText(enterprise);

        holder.position.setText(context.getText(R.string.position).toString() + " " + "\""
                + dataLinking.get(position).getPosition() + "\"");


        if (dataLinking.get(position).getState().equals("false"))
        {
            holder.button_refuse.setText(context.getText(R.string.link_four).toString());//видалити
            holder.button.setVisibility(View.GONE);
        }
        else
        if (dataLinking.get(position).getState().equals(""))
        {
            if (dataLinking.get(position).getFrom_user().equals(login))
            {
                holder.button_refuse.setText(context.getText(R.string.link_five).toString());//відвязатись
                holder.button.setVisibility(View.GONE);
            }
            else
            {
                holder.button_refuse.setText(context.getText(R.string.link_three).toString()); //відмовити
                holder.button.setText(context.getText(R.string.link_one).toString()); //привязати
            }
        }
        else
        if (dataLinking.get(position).getState().equals("true"))
        {
            if (dataLinking.get(position).getFrom_user().equals(login))
            {
                holder.button_refuse.setText(context.getText(R.string.link_five).toString());//відвязатись
                holder.button.setVisibility(View.GONE);
            }
            else
            {
                holder.button_refuse.setText(context.getText(R.string.link_two).toString());//відвязати
                holder.button.setVisibility(View.GONE);
            }
        }

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {

                headerHeight.add(holder.cardView.getHeight() - holder.detail.getHeight());
                detailHeight.add(holder.detail.getHeight());
                holder.detail.setVisibility(View.GONE);
                holder.card_layout.setVisibility(View.VISIBLE);
            }
        });

        holder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                holder.detail.setVisibility(View.VISIBLE);
                onProductDescriptionClicked(holder.cardView, position);
            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String where = dataLinking.get(position).getWhere_user();
                String from = dataLinking.get(position).getFrom_user();
                String linkUrl = url.getUrlSetLinkingState(where, from, "true");
                holder.button_refuse.setText(context.getText(R.string.link_two).toString());
                holder.button.setVisibility(View.GONE);
                LinkingStateRemove linking = new LinkingStateRemove(linkUrl, position, false, holder.name_enterprise);
                linking.execute();
            }
        });

        holder.button_refuse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(holder.button_refuse.getText().equals(context.getText(R.string.link_three).toString()))
                {
                    String where = dataLinking.get(position).getWhere_user();
                    String from = dataLinking.get(position).getFrom_user();
                    String linkUrl = url.getUrlSetLinkingState(where, from, "false");
                    LinkingStateRemove linking = new LinkingStateRemove(linkUrl, position, true, holder.name_enterprise);
                    linking.execute();
                }
                else
                {
                    String where = dataLinking.get(position).getWhere_user();
                    String from = dataLinking.get(position).getFrom_user();
                    String linkUrl = url.getUrlRemoveLinkingFrom(where, from);
                    LinkingStateRemove linking = new LinkingStateRemove(linkUrl, position, true, holder.name_enterprise);
                    linking.execute();
                }
            }
        });
    }

    class LinkingStateRemove extends AsyncTask<Void, Integer, Void>
    {
        private boolean state;
        private String url;
        private int position;
        private boolean type;
        private TextView name_enterprise;

        public LinkingStateRemove(String url, int position, boolean type, TextView name_enterprise)
        {
            this.name_enterprise = name_enterprise;
            this.type = type;
            this.url = url;
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                state = jsonParser.parseSetLinking(url);
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
            if (state)
            {
                if (type)
                {
                    removeCard(position);
                }
                else
                {
                    String enterprise = dataUser.get(position).getEnterprise();
                    if (enterprise == null || enterprise.equals("null"))
                    {
                        if (type_account == 1)
                        {
                            enterprise = context.getText(R.string.not_enterprise).toString();
                        }
                        else
                        if (type_account == 2)
                        {
                            enterprise = dataLinking.get(position).getEnterprise();
                        }
                    }
                    name_enterprise.setText(enterprise + " " + context.getText(R.string.link));
                    Toast toast = Toast.makeText(context, context.getText(R.string.link_hint),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            else
            {
                Toast toast = Toast.makeText(context, context.getText(R.string.not_network),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void removeCard(int position)
    {
        dataUser.remove(position);
        dataLinking.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
        recyclerView.getRecycledViewPool().clear();
        fragmentLinking.saveBase(dataLinking, dataUser);
        if (dataUser.size() == 0)
        {
            fragmentLinking.setVisibleHint();
        }
    }

    @Override
    public int getItemCount() {
        return dataUser.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        ImageView photo;
        TextView name;
        TextView name_enterprise;
        Button button;
        Button button_refuse;
        TextView position;
        LinearLayout card_layout;
        LinearLayout header;
        LinearLayout detail;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_linked);
            photo = (ImageView)itemView.findViewById(R.id.linked_photo);
            name = (TextView)itemView.findViewById(R.id.name_linked);
            name_enterprise = (TextView)itemView.findViewById(R.id.name_enterprise_linked);
            button = (Button)itemView.findViewById(R.id.linked_button);
            position = (TextView) itemView.findViewById(R.id.position_linked);
            card_layout = (LinearLayout)itemView.findViewById(R.id.card_layout_linked);
            header = (LinearLayout)itemView.findViewById(R.id.linked_header);
            detail = (LinearLayout)itemView.findViewById(R.id.detail_linked);
            button_refuse = (Button)itemView.findViewById(R.id.linked_refuse_button);
        }
    }

    private void onProductDescriptionClicked(CardView view, int pos)
    {
        toggleProductDescriptionHeight(view, pos);
    }

    private ArrayList<Integer> detailHeight = new ArrayList<>();
    private ArrayList<Integer> headerHeight = new ArrayList<>();

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
            anim.start();
        }
    }
    private RecyclerView recyclerView;

    public AdapterRecyclerLinking(List<DataUsers> dataUser, List<DataLinking> dataLinking,
           Context context, LinkingActivity activity, FragmentLinking fragmentLinking, RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
        this.fragmentLinking = fragmentLinking;
        this.dataUser = dataUser;
        this.dataLinking = dataLinking;
        this.context = context;
        this.activity = activity;
    }

    public void setData(List<DataUsers> dataUser, List<DataLinking> dataLinking)
    {
        this.dataUser = dataUser;
        this.dataLinking = dataLinking;
    }
}
