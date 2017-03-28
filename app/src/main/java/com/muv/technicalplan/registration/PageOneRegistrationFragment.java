package com.muv.technicalplan.registration;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.CircleImage;
import com.muv.technicalplan.R;
import com.muv.technicalplan.data.DataUser;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PageOneRegistrationFragment extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.registration_page_one;

    private EditText surname;
    private EditText name;
    private EditText surname_father;
    private RadioGroup type_account;
    private String path;
    private ImageView photo;
    private RegistrationActivity activity;
    private String uriText;
    private String type_account_text;

    public static PageOneRegistrationFragment getInstance(Context context)
    {
        Bundle args = new Bundle();
        PageOneRegistrationFragment fragment = new PageOneRegistrationFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("Surname", surname.getText().toString());
        outState.putString("Name", name.getText().toString());
        outState.putString("Surname_father", surname_father.getText().toString());
        outState.putString("Path", path);
        outState.putString("Uri", uriText);
        outState.putString("Type_account", type_account_text);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        context = getContext();
        activity = (RegistrationActivity) getActivity();

        surname = (EditText)view.findViewById(R.id.registration_surname);
        name = (EditText)view.findViewById(R.id.registration_name);
        surname_father = (EditText)view.findViewById(R.id.registration_surname_father);
        type_account = (RadioGroup)view.findViewById(R.id.registration_type_account);
        photo = (ImageView)view.findViewById(R.id.registration_photo);
        setRadioGroupListener();

        getChangeKeyboard();
        photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.registration_photo:
                        DialogFragmentImageRegistration dialogFragmentImageRegistration = new DialogFragmentImageRegistration().newInstance(
                                activity.getPageOneFragment());
                        dialogFragmentImageRegistration.show(getFragmentManager(), "dialogFragment");
                        break;
                }
            }
        });
        if (savedInstanceState != null)
        {
            surname.setText(savedInstanceState.getString("Surname"));
            name.setText(savedInstanceState.getString("Name"));
            surname_father.setText(savedInstanceState.getString("Surname_father"));
            path = savedInstanceState.getString("Path");
            uriText = savedInstanceState.getString("Uri");
            type_account_text = savedInstanceState.getString("Type_account");
            if (uriText != null)
            {
                if (uriText.length() > 0)
                {
                    try
                    {
                        Uri uri = Uri.parse(uriText);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                        photo.setImageBitmap(new CircleImage(context).transform(bitmap));
                    }
                    catch (Exception e)
                    {}
                }
            }
            if (type_account_text != null)
            {
                if (type_account_text.equals(context.getText(R.string.manager).toString()))
                {
                    RadioButton button = (RadioButton)view.findViewById(R.id.registration_button_manager);
                    button.setChecked(true);
                }
                else
                if (type_account_text.equals(context.getText(R.string.performer).toString()))
                {
                    RadioButton button = (RadioButton)view.findViewById(R.id.registration_button_performer);
                    button.setChecked(true);
                }
            }
        }
        return view;
    }

    public void setRadioGroupListener()
    {
        type_account.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton radioButton = (RadioButton)view.findViewById(checkedId);
                type_account_text = radioButton.getText().toString();
            }
        });
    }

    private void getChangeKeyboard()
    {
        surname.setCursorVisible(false);
        name.setCursorVisible(false);
        surname_father.setCursorVisible(false);
        KeyboardVisibilityEvent.setEventListener(getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen)
                        {
                            surname.setCursorVisible(true);
                            name.setCursorVisible(true);
                            surname_father.setCursorVisible(true);
                        }
                        else
                        {
                            surname.setCursorVisible(false);
                            name.setCursorVisible(false);
                            surname_father.setCursorVisible(false);
                        }
                    }
                });
    }

    public void getGallery()
    {
        if (isStoragePermissionGranted())
        {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0)
        {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                getGallery();
            }
        }
    }

    public  boolean isStoragePermissionGranted()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
            else
            {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    public void setDefaultPhoto()
    {
        path = null;
        photo.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.profile_img));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    uriText = selectedImage.toString();
                    path = selectedImage.getPath();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photo.setImageBitmap(new CircleImage(
                            context).transform(bitmap));

                }
        }
    }

    public String getPath() {
        return path;
    }

    public String getSurNameUser()
    {
        return surname.getText().toString();
    }

    public String getNameUser()
    {
        return name.getText().toString();
    }

    public String getSurNameFatherUser()
    {
        return surname_father.getText().toString();
    }

    public int getTypeAccountUser()
    {
        int selectedId = type_account.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)view.findViewById(selectedId);
        if (radioButton != null)
        {
            String type = radioButton.getText().toString();
            if (type.equals(getResources().getText(R.string.manager)))
            {
                return 1;
            }
            else
            if (type.equals(getResources().getText(R.string.performer)))
            {
                return 2;
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return 0;
        }
    }
}