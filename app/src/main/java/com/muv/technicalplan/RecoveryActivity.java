package com.muv.technicalplan;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RecoveryActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int LAYOUT = R.layout.activity_recovery;

    private Toolbar toolbar;
    private Button send;
    private EditText email;

    private JsonParser jsonParser = new JsonParser();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initToolbar(getResources().getText(R.string.recovery_name).toString());

        send = (Button)findViewById(R.id.send);
        send.setOnClickListener(this);
        email = (EditText)findViewById(R.id.email);

    }


    private void initToolbar(String title)
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar_recovery);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class Recovery extends AsyncTask<Void, Void, Void> {

        DialogFragmentProgress dialog = new DialogFragmentProgress().newInstance(getResources().getString(R.string.processing));
        private String url;
        private boolean state_email;

        public Recovery(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                dialog.show(getFragmentManager(), "dialogFragment");
                dialog.setCancelable(false);
                state_email = jsonParser.parseRecovery(url);
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
            if (state_email)
            {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.send_email),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.send_email_error),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.send:
                String email = this.email.getText().toString().toLowerCase();
                Internet internet = new Internet();
                if (internet.isOnline(getApplicationContext()))
                {
                    if (email.contains("@") & email.contains("."))
                    {
                        ConstantUrl url = new ConstantUrl();
                        Recovery recovery = new Recovery(url.getUrlRecovery(email));
                        recovery.execute();
                    }
                    else
                    {
                        if (email.length() > 0)
                        {
                            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.not_email),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else
                        {
                            Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.not_string),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.not_network),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }
}
