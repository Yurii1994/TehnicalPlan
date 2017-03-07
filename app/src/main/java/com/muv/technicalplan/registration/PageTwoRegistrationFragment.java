package com.muv.technicalplan.registration;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.base.BaseUser;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.DialogFragmentProgress;
import com.muv.technicalplan.Internet;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.SaveLoadPreferences;
import com.muv.technicalplan.UploadMultipart;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class PageTwoRegistrationFragment extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.registration_page_two;
    private JsonParser jsonParser = new JsonParser();
    private ConstantUrl url = new ConstantUrl();
    private TabPagerFragmentAdapterRegistration fragmentTabPager;
    private Internet internet = new Internet();

    private EditText email;
    private EditText login;
    private EditText password;
    private EditText codeText;
    private Button codeBtn;
    private Button registrationBtn;

    public String code;
    public boolean send_code;
    public static boolean registered;


    public static PageTwoRegistrationFragment getInstance(Context context)
    {
        Bundle args = new Bundle();
        PageTwoRegistrationFragment fragment = new PageTwoRegistrationFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }

    public void setTabPagerFragment(TabPagerFragmentAdapterRegistration fragmentTabPager)
    {
        this.fragmentTabPager = fragmentTabPager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        email = (EditText)view.findViewById(R.id.registration_email);
        login = (EditText)view.findViewById(R.id.registration_login);
        password = (EditText)view.findViewById(R.id.registration_password);
        codeText = (EditText)view.findViewById(R.id.registration_code);
        getChangeKeyboard();
        codeBtn = (Button) view.findViewById(R.id.registration_send_code);
        registrationBtn = (Button) view.findViewById(R.id.registration);return view;

    }

    private void getChangeKeyboard()
    {
        email.setCursorVisible(false);
        login.setCursorVisible(false);
        password.setCursorVisible(false);
        codeText.setCursorVisible(false);
        KeyboardVisibilityEvent.setEventListener(getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen)
                        {
                            if (registered)
                            {
                                close_openKey();
                            }
                            email.setCursorVisible(true);
                            login.setCursorVisible(true);
                            password.setCursorVisible(true);
                            codeText.setCursorVisible(true);
                        }
                        else
                        {
                            email.setCursorVisible(false);
                            login.setCursorVisible(false);
                            password.setCursorVisible(false);
                            codeText.setCursorVisible(false);
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

    public String getPasswordUser()
    {
        return password.getText().toString();
    }

    public String getCodeUser()
    {
        return codeText.getText().toString();
    }

    public void setOnClickListener(final DataUser dataUser, final String path)
    {
        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = getEmailUser();
                if (email.length() > 0)
                {
                    if (email.contains("@"))
                    {
                        if (internet.isOnline(context))
                        {
                            parseCode parseCode = new parseCode(email);
                            parseCode.execute();
                        }
                        else
                        {
                            Toast toast = Toast.makeText(context, getResources().getText(R.string.not_network),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    else
                    {
                        Toast toast = Toast.makeText(context, getResources().getText(R.string.not_email),
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(context, getResources().getText(R.string.email_code),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (send_code)
                {
                    String email = getEmailUser();
                    String login = getLoginUser();
                    String password = getPasswordUser();
                    String codeText = getCodeUser();
                    if (email.length() > 0 & login.length() > 0 & password.length() > 0 & codeText.length() > 0)
                    {
                        if (code.equals(codeText))
                        {
                            String surname = dataUser.getSurname();
                            String name = dataUser.getName();
                            String surname_father = dataUser.getSurname_father();
                            int type_account = dataUser.getType_account();
                            String enterprise = dataUser.getEnterprise();
                            String position = dataUser.getPosition();
                            String url_reg = "";
                            try
                            {
                                url_reg = url.getUrlRegistrationOrUpdate(surname, name, surname_father,
                                        login, password, email, type_account, enterprise, position, false, "");
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
                                parseRegistration parseRegistration = new parseRegistration(url_reg, data, path);
                                parseRegistration.execute();
                            }
                            else
                            {
                                Toast toast = Toast.makeText(context, getResources().getText(R.string.not_network),
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        else
                        {
                            Toast toast = Toast.makeText(context, getResources().getText(R.string.error_code),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    else
                    {
                        Toast toast = Toast.makeText(context, getResources().getText(R.string.fill_fields),
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(context, getResources().getText(R.string.not_code),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    class parseRegistration extends AsyncTask<Void, Void, Void> {

        DialogFragmentProgress dialog = new DialogFragmentProgress().newInstance(getResources().getString(R.string.register_name));
        private String url;
        private String state_registration;
        private DataUser dataUser;
        private String path;

        public parseRegistration(String url, DataUser dataUser, String path)
        {
            this.url = url;
            this.dataUser = dataUser;
            this.path = path;
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
            dialog.dismiss();
            if (state_registration.contains("false"))
            {
                if (state_registration.contains("false_emailANDfalse_login"))
                {
                    Toast toast = Toast.makeText(context, getResources().getText(R.string.login_email_too),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                if (state_registration.contains("false_email"))
                {
                    Toast toast = Toast.makeText(context, getResources().getText(R.string.email_too),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    Toast toast = Toast.makeText(context, getResources().getText(R.string.login_too),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            else
            {
                if (fragmentTabPager != null)
                {
                    Toast toast = Toast.makeText(context, getResources().getText(R.string.registered),
                            Toast.LENGTH_LONG);
                    toast.show();
                    BaseUser baseUser = new BaseUser();
                    baseUser.createBase(dataUser);
                    SaveLoadPreferences saveLoadPreferences = new SaveLoadPreferences();
                    registered = true;
                    saveLoadPreferences.saveBooleanPreferences("SING_IN", "REGISTERED", true, context);
                    if (path != null)
                    {
                        ConstantUrl url = new ConstantUrl();
                        UploadMultipart uploadMultipart = new UploadMultipart();
                        uploadMultipart.uploadImage(context, path, url.getUrlUploadImage(dataUser.getLogin(), dataUser.getLogin()));
                    }
                    fragmentTabPager.onPressedBack();
                }
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
            Toast toast = Toast.makeText(context, getResources().getText(R.string.sended_code),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }
}