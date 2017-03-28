package com.muv.technicalplan.profile;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.muv.technicalplan.base.BaseLinking;
import com.muv.technicalplan.base.BaseMap;
import com.muv.technicalplan.base.BaseUser;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.base.BaseUsers;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.main.MainActivity;
import com.muv.technicalplan.R;
import com.muv.technicalplan.SaveLoadPreferences;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.util.List;

public class DialogFragmentDeleteAccount extends DialogFragment implements View.OnClickListener
{
    private ProfileActivity activity;
    private Button send_code;
    private EditText codeView;
    private String code = "";
    private DialogFragmentDeleteAccount deleteAccount;
    private final String UPDATE = "com.muv.action.UPDATE";

    public static DialogFragmentDeleteAccount newInstance(ProfileActivity activity){
        DialogFragmentDeleteAccount dialogFragment = new DialogFragmentDeleteAccount();
        Bundle bundle = new Bundle();
        dialogFragment.setArguments(bundle);
        dialogFragment.setActivity(activity);
        return dialogFragment;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("Code", code);
        outState.putString("Code_edit", codeView.getText().toString());
    }

    public void setDialog(DialogFragmentDeleteAccount dialog)
    {
        deleteAccount = dialog;
    }

    private void setActivity(ProfileActivity activity)
    {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_account, new LinearLayout(getActivity()), false);
        send_code = (Button)layout.findViewById(R.id.delete_send_code);
        codeView = (EditText)layout.findViewById(R.id.delete_code);
        send_code.setOnClickListener(this);

        if (savedInstanceState != null)
        {
            code = savedInstanceState.getString("Code");
            codeView.setText(savedInstanceState.getString("Code_edit"));
        }

        return new MaterialDialog.Builder(getActivity())
                .customView(layout, false)
                .positiveText(R.string.cancel)
                .positiveColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .negativeText(R.string.delete)
                .negativeColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (code.length() > 0)
                        {
                            if (codeView.length() > 0)
                            {
                                if (codeView.getText().toString().equals(code))
                                {
                                    PageTwoProfileFragment.changed_profile = true;
                                    MainActivity.setDrawerLockClosed(MainActivity.drawerLayout);
                                    activity.deleteAccount();
                                    activity.setState_back(true);
                                    Intent intent = new Intent(UPDATE);
                                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                    intent.putExtra("Update", "account");
                                    intent.putExtra("Full_update", "true");
                                    intent.putExtra("change", "true");
                                    getActivity().sendBroadcast(intent);
                                    try
                                    {
                                        Runnable runnable = new Runnable()
                                        {
                                            public void run() {

                                                BaseUser baseUser = new BaseUser();
                                                baseUser.deleteBase();
                                                BaseMap baseMap = new BaseMap();
                                                baseMap.deleteBase();
                                                BaseLinking baseLinking = new BaseLinking();
                                                baseLinking.deleteBase();
                                                BaseUsers baseUsers = new BaseUsers();
                                                baseUsers.deleteBase();
                                            }
                                        };
                                        Thread thread = new Thread(runnable);
                                        thread.start();
                                    }
                                    catch (Exception e)
                                    {}

                                    SaveLoadPreferences saveLoadPreferences = new SaveLoadPreferences();
                                    saveLoadPreferences.saveBooleanPreferences("SING_IN", "CHANGE_PROFILE", true, getContext());
                                    PicassoTools.clearCache(Picasso.with(getContext()));
                                    activity.onBackPressed();
                                    Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.deleted_account),
                                            Toast.LENGTH_LONG);
                                    toast.show();
                                }
                                else
                                {
                                    Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.error_code),
                                            Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                            else
                            {
                                Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.code_not),
                                        Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                        else
                        {
                            Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.not_code),
                                    Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                })
                .build();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.delete_send_code:
                List<DataUser> user = DataUser.listAll(DataUser.class);
                parseCode parseCode = new parseCode(user.get(0).getEmail());
                parseCode.execute();
                break;
        }
    }

    class parseCode extends AsyncTask<Void, Void, Void> {

        private String email;

        public parseCode(String email)
        {
            this.email = email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                JsonParser jsonParser = new JsonParser();
                ConstantUrl url = new ConstantUrl();
                code = jsonParser.parseCode(url.getUrlCode(email)).replace("\"","");
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
            Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.sended_code),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
