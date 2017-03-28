package com.muv.technicalplan.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.DialogFragmentProgress;
import com.muv.technicalplan.Internet;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.data.DataMaps;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.expandableLayout.ExpandableLayout;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class AdapterRecycler  extends RecyclerView.Adapter<AdapterRecycler.ViewHolder>
{
    private List<DataMaps> dataMap = new ArrayList<>();
    private Context context;
    HashSet<Integer> mExpandedPositionSet = new HashSet<>();
    private Handler handler = new Handler();
    private List<DataUser> user = DataUser.listAll(DataUser.class);
    private AdapterRecycler.ViewHolder holder;
    private MainActivity activity;
    private boolean state_key_board;
    private ConstantUrl constantUrl = new ConstantUrl();
    private Internet internet = new Internet();
    private JsonParser jsonParser = new JsonParser();
    private RecyclerView recyclerView;
    private FragmentMap fragmentMap;
    private DialogFragmentProgress dialog;
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    public void setExpandedPositionSet(HashSet<Integer> mExpandedPositionSet) {
        this.mExpandedPositionSet = mExpandedPositionSet;
    }

    public  ArrayList<Integer> getExpandedPositionSet()
    {
        ArrayList<Integer> list = new ArrayList<>();
        for (Iterator<Integer> it = mExpandedPositionSet.iterator(); it.hasNext();)
        {
            list.add(it.next());
        }
        return list;
    }

    public List<DataMaps> getDataMap()
    {
        return dataMap;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public AdapterRecycler(List<DataMaps> dataUser, Context context,  HashSet<Integer> expandCard,
                           RecyclerView recyclerView, FragmentMap fragmentMap)
    {
        dataMap = new ArrayList<>();
        mExpandedPositionSet = expandCard;
        this.dataMap = dataUser;
        this.context = context;
        this.recyclerView = recyclerView;
        this.fragmentMap = fragmentMap;
    }

    @Override
    public AdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main, parent, false);
        return new AdapterRecycler.ViewHolder(view, new CommentManagerListener(), new CommentPerformerListener(),
                new OnClickListenerPopupMenu());
    }

    @Override
    public void onBindViewHolder(final AdapterRecycler.ViewHolder holder, final int position)
    {
        this.holder = holder;
        holder.position.setText(dataMap.get(position).getPosition());
        final String code = dataMap.get(position).getCode().replace(",", ".");
        holder.code.setText(code);
        holder.general.setText(dataMap.get(position).getGeneral());
        holder.relative.setText(dataMap.get(position).getRelative());
        holder.description.setText(dataMap.get(position).getDescription());
        if (dataMap.get(position).getDate() != null)
        {
            if (!dataMap.get(position).getDate().equals(""))
            {
                holder.date.setText(context.getText(R.string.date_of_last)
                        + " " + dataMap.get(position).getDate() + ".");
            }
            else
            {
                holder.date.setText(context.getText(R.string.date_of_last)
                        + " " + context.getText(R.string.not_known) + ".");
            }
        }

        holder.date_expected.setText(context.getText(R.string.date_of_future)
                + " " + dataMap.get(position).getState_performance() + ".");


        holder.onClickListenerPopupMenu.updatePositionPopupMenu(holder.getAdapterPosition(), holder);

        holder.commentManagerListener.updatePosition(holder.getAdapterPosition(), holder);
        holder.commentPerformerListener.updatePosition(holder.getAdapterPosition(), holder);
        String comment_manager = dataMap.get(holder.getAdapterPosition()).getComment_manager();
        String comment_performer = dataMap.get(holder.getAdapterPosition()).getComment_performer();
        holder.comment_manager.setText(comment_manager);
        holder.comment_performer.setText(comment_performer);

        if (user.size() > 0)
        {
            if (user.get(0).getType_account() == 1)
            {
                if (comment_performer.length() == 0)
                {
                    holder.comment_performer_layout.setVisibility(View.GONE);
                }
            }
            else
            if (user.get(0).getType_account() == 2)
            {
                if (comment_manager.length() == 0)
                {
                    holder.comment_manager_layout.setVisibility(View.GONE);
                }
            }
        }

        if (holder.comment_performer_layout.getVisibility() == View.VISIBLE &
                holder.comment_manager_layout.getVisibility() == View.VISIBLE)
        {
            params.bottomMargin = pxFromDp(10);
            holder.comment_manager_layout.setLayoutParams(params);
        }

        EditText comment_manager_edit = holder.comment_manager;
        comment_manager_edit.setSelection(comment_manager.length());
        EditText comment_performer_edit = holder.comment_performer;
        comment_performer_edit.setSelection(comment_performer.length());
        holder.comment_manager.setCursorVisible(false);
        holder.comment_performer.setCursorVisible(false);
        if (user.size() > 0)
        {
            if (user.get(0).getType_account() == 1)
            {
                comment_performer_edit.setFocusable(false);
                comment_performer_edit.setEnabled(false);
            }
            else
            {
                comment_manager_edit.setFocusable(false);
                comment_manager_edit.setEnabled(false);
            }
        }
    }

    private class OnClickListenerPopupMenu implements View.OnClickListener
    {
        private int position;
        private AdapterRecycler.ViewHolder holder;

        public void updatePositionPopupMenu(int position, AdapterRecycler.ViewHolder holder)
        {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void onClick(View v)
        {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.menu_card);
            popupMenu.setGravity(Gravity.END|Gravity.BOTTOM);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    switch (item.getItemId())
                    {
                        case R.id.menu_mark_as_completed:
                            if (internet.isOnline(context))
                            {
                                activity.OrientationLock();
                                Date date = new Date();
                                Calendar calendar_real = Calendar.getInstance();
                                calendar_real.setTime(date);
                                int year = calendar_real.get(Calendar.YEAR);
                                int month = calendar_real.get(Calendar.MONTH) + 1;
                                int days = calendar_real.get(Calendar.DAY_OF_MONTH);
                                String monthReal = month + "";
                                if (monthReal.length() == 1)
                                {
                                    monthReal = "0" + month;
                                }
                                String daysReal = days + "";
                                if (monthReal.length() == 1)
                                {
                                    daysReal = "0" + days;
                                }
                                String dateNew =  daysReal + "." + monthReal + "." + year;
                                String stitched = dataMap.get(position).getStitched();
                                stitched = stitched.replace("[false]", "");
                                stitched = stitched.replace("[true]", "");
                                String url = constantUrl.getUrlCompletedStitched(dataMap.get(holder.getAdapterPosition()).getName_table(),
                                        dateNew, stitched, dataMap.get(position).getIdMap() + "");
                                Completed completed = new Completed(url, position, dateNew, dataMap.get(position).getDescription(),
                                        dataMap.get(position).getPosition());
                                completed.execute();
                            }
                            else
                            {
                                Toast.makeText(context, context.getText(R.string.not_network), Toast.LENGTH_SHORT).show();
                            }
                            break;

                        case R.id.menu_save_comment:
                            if (internet.isOnline(context))
                            {
                                activity.OrientationLock();
                                String comment_manger = dataMap.get(position).getComment_manager();
                                String comment_performer = dataMap.get(position).getComment_performer();
                                String url = constantUrl.getUrlComment(dataMap.get(holder.getAdapterPosition()).getName_table(),
                                        comment_manger, comment_performer, dataMap.get(position).getIdMap() + "");
                                Comment comment = new Comment(url);
                                comment.execute();
                            }
                            else
                            {
                                Toast.makeText(context, context.getText(R.string.not_network), Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }

                    return false;
                }
            });
            popupMenu.show();
        }
    }

    class Comment extends AsyncTask<Void, Integer, Void>
    {
        private String url;

        public Comment(String url)
        {
            dialog = new DialogFragmentProgress().newInstance(context.getResources().getString(R.string.save));
            dialog.setCancelable(false);
            dialog.show(activity.getFragmentManager(), "dialogFragment");
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                jsonParser.getCompletedStitchedComment(url);
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
            if (dialog != null)
            {
                dialog.dismiss();
            }
            activity.OrientationUnlock();
        }
    }

    class Completed extends AsyncTask<Void, Integer, Void>
    {
        private String url;
        private boolean state;
        private int position;
        private String date;
        private String descriptionText;
        private String positionText;

        public Completed(String url, int position, String date, String descriptionText, String positionText)
        {
            dialog = new DialogFragmentProgress().newInstance(context.getResources().getString(R.string.save));
            dialog.setCancelable(false);
            dialog.show(activity.getFragmentManager(), "dialogFragment");
            this.descriptionText = descriptionText;
            this.positionText = positionText;
            this.url = url;
            this.position = position;
            this.date = date;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                state = jsonParser.getCompletedStitchedComment(url);
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
                dataMap.get(position).setDate(date);
                activity.updateDataMap(descriptionText, positionText, fragmentMap.getTitle());
            }
            else
            {
                if (dialog != null)
                {
                    dialog.dismiss();
                }
                activity.OrientationUnlock();
            }
        }
    }

    public void setListUpdateItem(final List<DataMaps> list, final String descriptionText, final String positionText)
    {
        final List<DataMaps> dataMapOld = dataMap;
        dataMap = list;
        handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int positionItem = -1;
                        for (int i = 0; i < dataMap.size(); i++)
                        {
                            String description = dataMap.get(i).getDescription();
                            String position = dataMap.get(i).getPosition();
                            if (position.equals(positionText) & description.equals(descriptionText))
                            {
                                positionItem = i;
                                break;
                            }
                        }
                        if (positionItem >= 0)
                        {
                            notifyItemChanged(positionItem);
                            notifyItemRangeChanged(positionItem, getItemCount());
                            recyclerView.getRecycledViewPool().clear();
                        }
                        else
                        if (positionItem == - 1)
                        {
                            for (int i = 0; i < dataMapOld.size(); i++)
                            {
                                String description = dataMapOld.get(i).getDescription();
                                String position = dataMapOld.get(i).getPosition();
                                if (position.equals(positionText) & description.equals(descriptionText))
                                {
                                    positionItem = i;
                                    break;
                                }
                            }
                            if (positionItem >= 0)
                            {
                                notifyItemRemoved(positionItem);
                                notifyItemRangeChanged(positionItem, getItemCount());
                                recyclerView.getRecycledViewPool().clear();
                            }
                        }
                        if (dialog != null)
                        {
                            dialog.dismiss();
                        }
                        activity.OrientationUnlock();
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void setScrollListener(RecyclerView recyclerView)
    {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                holder.updateItem(holder.getAdapterPosition());
                if (dpFromPx(dy) > 30 || dpFromPx(dy) < -30)
                {
                    if (state_key_board)
                    {
                        close_openKey();
                        state_key_board = false;
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void getChangeKeyboard()
    {
        KeyboardVisibilityEvent.setEventListener(activity, new KeyboardVisibilityEventListener()
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
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private int dpFromPx(float px)
    {
        return (int) Math.ceil(px / context.getApplicationContext().getResources().getDisplayMetrics().density);
    }

    private int pxFromDp(float dp)
    {
        return (int) Math.ceil(dp * context.getApplicationContext().getResources().getDisplayMetrics().density);
    }

    private class CommentManagerListener implements TextWatcher {
        private int position;
        private AdapterRecycler.ViewHolder holder;

        public void updatePosition(int position, AdapterRecycler.ViewHolder holder)
        {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, int i, int i2, int i3)
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.comments_layout.setLayoutParams(params);

            final int height = dataMap.get(position).getExpand_height();
            final boolean change_comment = dataMap.get(position).getComment_manager().equals(charSequence.toString());

            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    if (mExpandedPositionSet.contains(position))
                    {
                        if (height > 0 & change_comment)
                        {
                            holder.expandableLayout.setExpandedViewHeight(height);
                        }
                        else
                        if (holder.comments_layout.getHeight() > 0)
                        {
                            holder.expandableLayout.setExpandedViewHeight(holder.comments_layout.getHeight());
                            dataMap.get(position).setExpand_height(holder.comments_layout.getHeight());
                        }
                    }
                    holder.comment_manager.setCursorVisible(true);
                    dataMap.get(position).setComment_manager(charSequence.toString());
                    holder.updateItem(position);
                }
            });
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    private class CommentPerformerListener implements TextWatcher {
        private int position;
        private AdapterRecycler.ViewHolder holder;

        public void updatePosition(int position, AdapterRecycler.ViewHolder holder)
        {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, int i, int i2, int i3)
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.comments_layout.setLayoutParams(params);

            final int height = dataMap.get(position).getExpand_height();
            final boolean change_comment = dataMap.get(position).getComment_performer().equals(charSequence.toString());

            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    if (mExpandedPositionSet.contains(position))
                    {
                        if (height > 0 & change_comment)
                        {
                            holder.expandableLayout.setExpandedViewHeight(height);
                        }
                        else
                        if (holder.comments_layout.getHeight() > 0)
                        {
                            holder.expandableLayout.setExpandedViewHeight(holder.comments_layout.getHeight());
                            dataMap.get(position).setExpand_height(holder.comments_layout.getHeight());
                        }
                    }
                    holder.comment_performer.setCursorVisible(true);
                    dataMap.get(position).setComment_performer(charSequence.toString());
                    holder.updateItem(position);
                }
            });
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public int getItemCount() {
        return dataMap.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView position;
        TextView code;
        TextView general;
        TextView relative;
        TextView date;
        TextView date_expected;
        TextView description;
        EditText comment_manager;
        EditText comment_performer;
        LinearLayout card_menu;
        LinearLayout comments_layout;
        LinearLayout comment_manager_layout;
        LinearLayout comment_performer_layout;
        ExpandableLayout expandableLayout;
        CommentManagerListener commentManagerListener;
        CommentPerformerListener commentPerformerListener;
        OnClickListenerPopupMenu onClickListenerPopupMenu;

        public ViewHolder(View itemView, CommentManagerListener commentManagerListener,
                          CommentPerformerListener commentPerformerListener, OnClickListenerPopupMenu onClickListenerPopupMenu) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_main);
            position = (TextView)itemView.findViewById(R.id.position_map);
            code = (TextView)itemView.findViewById(R.id.code_map);
            general = (TextView)itemView.findViewById(R.id.general_map);
            relative = (TextView)itemView.findViewById(R.id.relative_map);
            date = (TextView)itemView.findViewById(R.id.date_map);
            date_expected = (TextView)itemView.findViewById(R.id.date_map_expected);
            description = (TextView)itemView.findViewById(R.id.description_map);
            card_menu = (LinearLayout) itemView.findViewById(R.id.card_menu);
            comment_manager = (EditText) itemView.findViewById(R.id.comment_manager_text);
            comment_performer = (EditText) itemView.findViewById(R.id.comment_performer_text);
            comments_layout = (LinearLayout)itemView.findViewById(R.id.comments_layout);
            comment_manager_layout = (LinearLayout)itemView.findViewById(R.id.comment_manager_layout);
            comment_performer_layout = (LinearLayout)itemView.findViewById(R.id.comment_performer_layout);
            expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout_main);
            this.commentManagerListener = commentManagerListener;
            this.commentPerformerListener = commentPerformerListener;
            comment_manager.addTextChangedListener(commentManagerListener);
            comment_performer.addTextChangedListener(commentPerformerListener);
            getChangeKeyboard();
            description.setTag(this);
            this.onClickListenerPopupMenu = onClickListenerPopupMenu;
            card_menu.setOnClickListener(onClickListenerPopupMenu);
        }

        private void updateItem(final int position)
        {
            expandableLayout.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
                @Override
                public void onExpand(boolean expanded)
                {
                    registerExpand(position);
                }
            });
            expandableLayout.setExpand(mExpandedPositionSet.contains(position));
        }
    }

    private void registerExpand(int position)
    {
        if (mExpandedPositionSet.contains(position))
        {
            removeExpand(position);
        }
        else
        {
            addExpand(position);
        }
    }

    private void removeExpand(int position)
    {
        mExpandedPositionSet.remove(position);
    }

    private void addExpand(int position)
    {
        mExpandedPositionSet.add(position);
    }
}
