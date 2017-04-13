package com.muv.technicalplan.enterprise;


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
import com.muv.technicalplan.profile.PageOneProfileFragment;
import com.muv.technicalplan.profile.PageTwoProfileFragment;

public class DialogFragmentTemplate extends DialogFragment implements View.OnClickListener
{
    private  FragmentReport fragmentReport;

    public static DialogFragmentTemplate newInstance(FragmentReport pageOneFragment){

        DialogFragmentTemplate dialogFragment = new DialogFragmentTemplate();
        Bundle bundle = new Bundle();
        dialogFragment.setArguments(bundle);
        dialogFragment.setFragmentReport(pageOneFragment);
        return dialogFragment;
    }

    public void setFragmentReport(FragmentReport fragmentReport)
    {
        this.fragmentReport = fragmentReport;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_template, new LinearLayout(getActivity()), false);
        Button download = (Button)layout.findViewById(R.id.download_file);
        Button delete = (Button)layout.findViewById(R.id.delete_file);

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
            case R.id.download_file:
                fragmentReport.getFileDownload();
                dismiss();
                break;

            case R.id.delete_file:
                fragmentReport.getFileDelete();
                dismiss();
                break;
        }
    }
}
