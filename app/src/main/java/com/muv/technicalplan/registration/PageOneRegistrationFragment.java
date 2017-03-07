package com.muv.technicalplan.registration;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.CircleImage;
import com.muv.technicalplan.R;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.IOException;
import static android.app.Activity.RESULT_OK;

public class PageOneRegistrationFragment extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.registration_page_one;

    private EditText surname;
    private EditText name;
    private EditText surname_father;
    private RadioGroup type_account;
    private String path;
    private TabPagerFragmentAdapterRegistration adapterRegistration;
    private ImageView photo;

    public static PageOneRegistrationFragment getInstance(Context context)
    {
        Bundle args = new Bundle();
        PageOneRegistrationFragment fragment = new PageOneRegistrationFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }

    public void setTabPagerFragment(TabPagerFragmentAdapterRegistration adapterRegistration)
    {
        this.adapterRegistration = adapterRegistration;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        surname = (EditText)view.findViewById(R.id.registration_surname);
        name = (EditText)view.findViewById(R.id.registration_name);
        surname_father = (EditText)view.findViewById(R.id.registration_surname_father);
        type_account = (RadioGroup)view.findViewById(R.id.registration_type_account);
        photo = (ImageView)view.findViewById(R.id.registration_photo);

        getChangeKeyboard();
        photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.registration_photo:
                        DialogFragmentImageRegistration dialogFragmentImageRegistration = new DialogFragmentImageRegistration().newInstance(
                                adapterRegistration.getPageOneFragment());
                        dialogFragmentImageRegistration.show(getFragmentManager(), "dialogFragment");
                        break;
                }
            }
        });
        return view;
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