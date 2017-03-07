package com.muv.technicalplan.registration;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.muv.technicalplan.R;


public class DialogFragmentImageRegistration extends DialogFragment implements View.OnClickListener
{
    private  PageOneRegistrationFragment pageOneFragment;

    public static DialogFragmentImageRegistration newInstance(PageOneRegistrationFragment pageOneFragment){

        DialogFragmentImageRegistration dialogFragment = new DialogFragmentImageRegistration();
        Bundle bundle = new Bundle();
        dialogFragment.setArguments(bundle);
        dialogFragment.setPageOneFragment(pageOneFragment);
        return dialogFragment;
    }

    public void setPageOneFragment(PageOneRegistrationFragment pageOneFragment)
    {
        this.pageOneFragment = pageOneFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image, new LinearLayout(getActivity()), false);
        Button download = (Button)layout.findViewById(R.id.download_gallery);
        Button delete = (Button)layout.findViewById(R.id.delete_image);

        download.setOnClickListener(this);
        delete.setOnClickListener(this);

        return new MaterialDialog.Builder(getActivity())
            .customView(layout, false)
            .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.download_gallery:
                pageOneFragment.getGallery();
                dismiss();
                break;

            case R.id.delete_image:
                pageOneFragment.setDefaultPhoto();
                dismiss();
                break;
        }
    }
}
