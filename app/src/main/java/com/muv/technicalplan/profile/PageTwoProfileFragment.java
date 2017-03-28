package com.muv.technicalplan.profile;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.base.BaseLinking;
import com.muv.technicalplan.base.BaseMap;
import com.muv.technicalplan.base.BaseUser;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.base.BaseUsers;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.DialogFragmentProgress;
import com.muv.technicalplan.Internet;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.SaveLoadPreferences;
import com.muv.technicalplan.UploadMultipart;
import com.muv.technicalplan.main.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.List;

public class PageTwoProfileFragment extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.profile_page_two;
    private JsonParser jsonParser = new JsonParser();
    private ConstantUrl url = new ConstantUrl();
    private Internet internet = new Internet();

    private EditText email;
    private EditText login;
    private EditText password_old;
    private EditText password_new;
    private EditText codeView;
    private Button codeBtn;
    private Button registrationBtn;

    private boolean send_code;
    private List<DataUser> user;
    private boolean change_type_account;
    public static boolean changed_profile;
    private final String UPDATE = "com.muv.action.UPDATE";
    private Toast toast;
    private String email_text;
    private String login_text;
    private String password_old_text;
    private String password_new_text;
    private String code;
    private String code_edit;
    private ProfileActivity activity;
    private DialogFragmentProgress dialog;

    private void showToastMessage(String a)
    {
        if (toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(getContext(), a, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static PageTwoProfileFragment getInstance(Context context)
    {
        Bundle args = new Bundle();
        PageTwoProfileFragment fragment = new PageTwoProfileFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("Email", email.getText().toString());
        outState.putString("Login", login.getText().toString());
        outState.putString("Password_old", password_old.getText().toString());
        outState.putString("Password_new", password_new.getText().toString());
        outState.putString("CodeEdit", codeView.getText().toString());
        outState.putString("Code", code);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        context = getContext();

        email = (EditText)view.findViewById(R.id.profile_email);
        login = (EditText)view.findViewById(R.id.profile_login);
        password_old = (EditText)view.findViewById(R.id.profile_password_old);
        password_new = (EditText)view.findViewById(R.id.profile_password_new);
        codeView = (EditText)view.findViewById(R.id.profile_code);
        codeBtn = (Button) view.findViewById(R.id.profile_send_code);
        registrationBtn = (Button) view.findViewById(R.id.profile_change);
        OnClickListener onClickListener = new OnClickListener();
        codeBtn.setOnClickListener(onClickListener);
        registrationBtn.setOnClickListener(onClickListener);

        user = DataUser.listAll(DataUser.class);
        email_text = user.get(0).getEmail();
        login_text = user.get(0).getLogin();
        if (savedInstanceState != null)
        {
            email_text = savedInstanceState.getString("Email");
            login_text = savedInstanceState.getString("Login");
            password_old_text = savedInstanceState.getString("Password_old");
            password_new_text = savedInstanceState.getString("Password_new");
            code_edit = savedInstanceState.getString("CodeEdit");
            code = savedInstanceState.getString("Code");
            password_old.setText(password_old_text);
            password_new.setText(password_new_text);
            codeView.setText(code_edit);
        }
        email.setText(email_text);
        login.setText(login_text);

        getChangeKeyboard();
        setLoginChangeListener();
        codeView.setVisibility(View.GONE);
        codeBtn.setVisibility(View.GONE);
        setEmailChangeListener();
        activity = (ProfileActivity)getActivity();
        return view;
    }

    public void getChangeTypeAccount()
    {
        List<DataUser> user = DataUser.listAll(DataUser.class);
        if (user.get(0).getType_account() != activity.getTypeAccountUser())
        {
            codeView.setVisibility(View.VISIBLE);
            codeBtn.setVisibility(View.VISIBLE);
            change_type_account = true;
        }
        else
        {
            codeView.setVisibility(View.GONE);
            codeBtn.setVisibility(View.GONE);
            change_type_account = false;
        }
    }

    private void getChangeKeyboard()
    {
        email.setCursorVisible(false);
        login.setCursorVisible(false);
        password_old.setCursorVisible(false);
        password_new.setCursorVisible(false);
        codeView.setCursorVisible(false);
        KeyboardVisibilityEvent.setEventListener(getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen)
                        {
                            email.setCursorVisible(true);
                            login.setCursorVisible(true);
                            password_old.setCursorVisible(true);
                            password_new.setCursorVisible(true);
                            codeView.setCursorVisible(true);
                            if (changed_profile)
                            {
                                close_openKey();
                            }
                        }
                        else
                        {
                            email.setCursorVisible(false);
                            login.setCursorVisible(false);
                            password_old.setCursorVisible(false);
                            password_new.setCursorVisible(false);
                            codeView.setCursorVisible(false);
                        }
                    }
                });
    }

    public String getEmailUser()
    {
        return email.getText().toString().toLowerCase();
    }

    public String getLoginUser()
    {
        return login.getText().toString().toLowerCase();
    }

    public String getOldPasswordUser()
    {
        return password_old.getText().toString();
    }

    public String getNewPasswordUser()
    {
        return password_new.getText().toString();
    }

    public String getCodeUser()
    {
        return codeView.getText().toString();
    }

    private String emailBefore;
    private Boolean changeEmail = false;

    private void setEmailChangeListener()
    {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!change_type_account)
                {
                    if (emailBefore.equals(s.toString()))
                    {
                        codeView.setVisibility(View.GONE);
                        codeBtn.setVisibility(View.GONE);
                        changeEmail = false;
                    }
                    else
                    {
                        codeView.setVisibility(View.VISIBLE);
                        codeBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                if (!changeEmail & !change_type_account)
                {
                    emailBefore = s.toString();
                    changeEmail = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private String loginBefore;
    private Boolean changeLogin = false;

    private void setLoginChangeListener()
    {
        login.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (loginBefore.equals(s.toString()))
                {
                    changeLogin = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                if (!changeLogin)
                {
                    loginBefore = s.toString();
                    changeLogin = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private class OnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.profile_change:
                    DataUser dataUser = activity.getDataUserPageOne(activity.getNameUser(),
                            activity.getSurNameUser(), activity.getSurNameFatherUser(), activity.getTypeAccountUser());
                    String path = activity.getPath();

                    String email = getEmailUser();
                    String login = getLoginUser();
                    String password = user.get(0).getPassword();
                    String password_old = getOldPasswordUser();
                    String password_new = getNewPasswordUser();
                    String codeText = getCodeUser();
                    String surname = dataUser.getSurname();
                    String name = dataUser.getName();
                    String surname_father = dataUser.getSurname_father();
                    int type_account = dataUser.getType_account();
                    List<DataUser> user = DataUser.listAll(DataUser.class);
                    String enterprise = user.get(0).getEnterprise();
                    String position = user.get(0).getPosition();
                    String name_table = user.get(0).getName_table();
                    String new_login;
                    if (!changeEmail & !change_type_account)
                    {
                        if (email.length() > 0 & login.length() > 0)
                        {
                            if (changeLogin)
                            {
                                new_login = login;
                                login = loginBefore;
                            }
                            else
                            {
                                new_login = login;
                            }
                            if (password_new.length() > 0)
                            {
                                if (password_old.length() > 0)
                                {
                                    if (password.equals(password_old))
                                    {
                                        password = password_new;
                                        setChangeUpload(surname, name, surname_father, login, password, email, type_account,
                                                enterprise, position, name_table, new_login, path);
                                    }
                                    else
                                    {
                                        showToastMessage(getResources().getText(R.string.password_error).toString());
                                    }
                                }
                                else
                                {
                                    showToastMessage(getResources().getText(R.string.password_old_not).toString());
                                }
                            }
                            else
                            {
                                setChangeUpload(surname, name, surname_father, login, password, email, type_account,
                                        enterprise, position, name_table, new_login, path);
                            }
                        }
                        else
                        {
                            showToastMessage(getResources().getText(R.string.fill_fields).toString());
                        }
                    }
                    else
                    {
                        if (send_code)
                        {
                            if (email.length() > 0 & login.length() > 0 & codeText.length() > 0)
                            {
                                if (code.equals(codeText))
                                {
                                    if (changeLogin)
                                    {
                                        new_login = login;
                                        login = loginBefore;
                                    }
                                    else
                                    {
                                        new_login = login;
                                    }
                                    if (password_new.length() > 0)
                                    {
                                        if (password_old.length() > 0)
                                        {
                                            if (password.equals(password_old))
                                            {
                                                password = password_new;
                                                setChangeUpload(surname, name, surname_father, login, password, email, type_account,
                                                        enterprise, position, name_table, new_login, path);
                                            }
                                            else
                                            {
                                                showToastMessage(getResources().getText(R.string.password_error).toString());
                                            }
                                        }
                                        else
                                        {
                                            showToastMessage(getResources().getText(R.string.password_old_not).toString());
                                        }
                                    }
                                    else
                                    {
                                        setChangeUpload(surname, name, surname_father, login, password, email, type_account,
                                                enterprise, position, name_table, new_login, path);
                                    }
                                }
                                else
                                {
                                    showToastMessage(getResources().getText(R.string.error_code).toString());
                                }
                            }
                            else
                            {
                                showToastMessage(getResources().getText(R.string.fill_fields).toString());
                            }
                        }
                        else
                        {
                            showToastMessage(getResources().getText(R.string.not_code).toString());
                        }
                    }
                    break;

                case R.id.profile_send_code:
                    String email_user = getEmailUser();
                    if ( email_user.length() > 0)
                    {
                        if ( email_user.contains("@"))
                        {
                            if (internet.isOnline(context))
                            {
                                parseCode parseCode = new parseCode( email_user);
                                parseCode.execute();
                            }
                            else
                            {
                                showToastMessage(getResources().getText(R.string.not_network).toString());
                            }
                        }
                        else
                        {
                            showToastMessage(getResources().getText(R.string.not_email).toString());
                        }
                    }
                    else
                    {
                        showToastMessage(getResources().getText(R.string.email_code).toString());
                    }
                    break;

            }
        }
    }

    private void setChangeUpload(String surname, String name, String surname_father, String login,
                                 String password, String email, int type_account, String enterprise,
                                 String position, String name_table, String new_login, String path)
    {
        String url_reg = "";
        try
        {
            url_reg = url.getUrlRegistrationOrUpdate(surname, name, surname_father,
                    login, password, email, type_account, enterprise, position, name_table, true, new_login);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        DataUser data = new DataUser();
        data.setName(name);
        data.setSurname(surname);
        data.setSurname_father(surname_father);
        data.setType_account(type_account);
        data.setEmail(email);
        data.setLogin(login);
        data.setEnterprise(enterprise);
        data.setPosition(position);
        data.setPassword(password);
        if (internet.isOnline(context))
        {
            parseRegistration parseRegistration = new parseRegistration(url_reg, data, path, new_login);
            parseRegistration.execute();
        }
        else
        {
            showToastMessage(getResources().getText(R.string.not_network).toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }

    class parseRegistration extends AsyncTask<Void, Void, Void> {


        private String url;
        private String state_registration;
        private DataUser dataUser;
        private String path;
        private String new_login;

        public parseRegistration(String url, DataUser dataUser, String path, String new_login)
        {
            dialog = new DialogFragmentProgress().newInstance(getResources().getString(R.string.save));
            this.url = url;
            this.dataUser = dataUser;
            this.path = path;
            this.new_login = new_login;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                dialog.show(getActivity().getFragmentManager(), "dialogFragment");
                dialog.setCancelable(false);
                state_registration = jsonParser.parseRegistration(url);
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
            if (state_registration != null)
            {
                if (state_registration.contains("false"))
                {
                    if (state_registration.contains("false_emailANDfalse_login"))
                    {
                        showToastMessage(getResources().getText(R.string.login_email_too).toString());
                    }
                    else
                    if (state_registration.contains("false_email"))
                    {
                        showToastMessage(getResources().getText(R.string.email_too).toString());
                    }
                    else
                    {
                        showToastMessage(getResources().getText(R.string.login_too).toString());
                    }
                }
                else
                {
                    BaseUser baseUser = new BaseUser();
                    baseUser.createBase(dataUser);
                    changed_profile = true;

                    Intent intent = new Intent(UPDATE);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    intent.putExtra("Update", "account");
                    if (change_type_account)
                    {
                        intent.putExtra("Full_update", "true");
                    }
                    else
                    {
                        intent.putExtra("Full_update", "false");
                    }
                    intent.putExtra("change", "true");
                    getActivity().sendBroadcast(intent);

                    ConstantUrl url = new ConstantUrl();
                /*якщо тип акаута змінився удалить всі дані привязані до акаунта автоматов в php*/
                    if (path != null)
                    {
                        UploadMultipart uploadMultipart = new UploadMultipart();
                        uploadMultipart.uploadImage(context, path, url.getUrlUploadImage(dataUser.getLogin(), new_login));
                    }
                    else
                    {
                        if (delete_image)
                        {
                            parseRemoveImage parseRemoveImage = new parseRemoveImage(url.getUrlDeleteImage(dataUser.getLogin()));
                            parseRemoveImage.execute();
                        }
                        else
                        if (!dataUser.getLogin().equals(new_login))
                        {
                            parseRefreshImage parseRefreshImage = new parseRefreshImage(url.getUrlUpdateImageLogin(dataUser.getLogin(), new_login));
                            parseRefreshImage.execute();
                        }
                    }
                    if (change_type_account)
                    {
                        try
                        {
                            Runnable runnable = new Runnable() {
                                public void run() {
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
                    }
                    activity.onPressedBack();
                }
            }
        }
    }

    public static boolean delete_image;

    class parseRefreshImage extends AsyncTask<Void, Void, Void> {

        private String url;

        public parseRefreshImage(String url)
        {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                jsonParser.parseRemoveRefresh(url);
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
        }
    }

    class parseRemoveImage extends AsyncTask<Void, Void, Void> {

        private String url;
        private boolean remove;

        public parseRemoveImage(String url)
        {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                remove = jsonParser.parseRemoveRefresh(url);
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
            if (remove)
            {
                PicassoTools.clearCache(Picasso.with(context));
            }
        }
    }

    private void close_openKey()
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    class parseCode extends AsyncTask<Void, Void, Void> {

        DialogFragmentProgress dialog = new DialogFragmentProgress().newInstance(getResources().getString(R.string.processing));
        private String email;

        public parseCode(String email)
        {
            this.email = email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                dialog.show(getActivity().getFragmentManager(), "dialogFragment");
                dialog.setCancelable(false);
                send_code = false;
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
            dialog.dismiss();
            send_code = true;
            showToastMessage(getResources().getText(R.string.sended_code).toString());
        }
    }
}