package com.muv.technicalplan.profile;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.CircleImage;
import com.muv.technicalplan.ConstantUrl;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.JsonParser;
import com.muv.technicalplan.R;
import com.muv.technicalplan.SaveLoadPreferences;
import com.muv.technicalplan.main.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PageOneProfileFragment extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.profile_page_one;

    private EditText surname;
    private EditText name;
    private EditText surname_father;
    private RadioGroup type_account;
    private String path;
    private ImageView photo;
    private ConstantUrl constantUrl = new ConstantUrl();
    private String surname_text;
    private String name_text;
    private String surname_father_text;
    private String uri;
    private String type_account_text;
    private ProfileActivity activity;

    public static PageOneProfileFragment getInstance(Context context)
    {
        Bundle args = new Bundle();
        PageOneProfileFragment fragment = new PageOneProfileFragment();
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
        outState.putString("Uri", uri);
        outState.putString("Type_account", type_account_text);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        context = getContext();
        activity = (ProfileActivity)getActivity();

        surname = (EditText)view.findViewById(R.id.profile_surname);
        name = (EditText)view.findViewById(R.id.profile_name);
        surname_father = (EditText)view.findViewById(R.id.profile_surname_father);
        type_account = (RadioGroup)view.findViewById(R.id.profile_type_account);
        photo = (ImageView)view.findViewById(R.id.profile_photo);

        getChangeKeyboard();

        List<DataUser> user = DataUser.listAll(DataUser.class);
        if (savedInstanceState == null)
        {
            surname_text = user.get(0).getSurname();
            name_text = user.get(0).getName();
            surname_father_text = user.get(0).getSurname_father();
            path = "";
        }
        else
        {
            surname_text = savedInstanceState.getString("Surname");
            name_text = savedInstanceState.getString("Name");
            surname_father_text = savedInstanceState.getString("Surname_father");
            path = savedInstanceState.getString("Path");
            uri = savedInstanceState.getString("Uri");
            type_account_text = savedInstanceState.getString("Type_account");
        }

        surname.setText(surname_text);
        name.setText(name_text);
        surname_father.setText(surname_father_text);
        surname.setSelection(surname.getText().length());
        int type_account = user.get(0).getType_account();
        if (type_account_text != null)
        {
            if (type_account_text.equals(context.getText(R.string.manager).toString()))
            {
                RadioButton button = (RadioButton)view.findViewById(R.id.button_manager);
                button.setChecked(true);
            }
            else
            {
                RadioButton button = (RadioButton)view.findViewById(R.id.button_performer);
                button.setChecked(true);
            }
        }
        else
        {
            if (type_account == 1)
            {
                RadioButton button = (RadioButton)view.findViewById(R.id.button_manager);
                button.setChecked(true);
            }
            else
            if (type_account == 2)
            {
                RadioButton button = (RadioButton)view.findViewById(R.id.button_performer);
                button.setChecked(true);
            }
        }

        getDownloadImage(user, photo, uri);

        photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.profile_photo:
                        DialogFragmentImageProfile dialogFragmentImage = new DialogFragmentImageProfile().newInstance(
                                activity.getPageOneFragment());
                        dialogFragmentImage.show(getFragmentManager(), "dialogFragment");
                        break;
                }
            }
        });
        setRadioGroupListener();
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
                List<DataUser> user = DataUser.listAll(DataUser.class);
                String type_account_old_text;
                if (user.get(0).getType_account() == 1)
                {
                    type_account_old_text = context.getText(R.string.manager).toString();
                }
                else
                {
                    type_account_old_text = context.getText(R.string.performer).toString();
                }
                if (!type_account_text.equals(type_account_old_text))
                {
                    Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.change_type_account),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public int getTypeAccountUser()
    {
        int selectedId = type_account.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)view.findViewById(selectedId);
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

    private void getDownloadImage(List<DataUser> user, ImageView imageView, String uriText)
    {
        if (uriText != null)
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
        else
        {
            Picasso.with(getContext())
                    .load(constantUrl.getUrlDownloadImage(user.get(0).getImage()))
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
                            return "circle()";
                        }
                    })
                    .into(imageView);
        }
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
                    uri = selectedImage.toString();
                    path = selectedImage.getPath();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photo.setImageBitmap(new CircleImage(
                            context).transform(bitmap));

                }
        }
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

    public String getPath() {
        return path;
    }
}