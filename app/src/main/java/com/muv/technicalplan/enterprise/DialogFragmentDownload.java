package com.muv.technicalplan.enterprise;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.muv.technicalplan.R;
import com.muv.technicalplan.data.DataUser;

import java.util.List;

public class DialogFragmentDownload extends DialogFragment
{
    private FragmentPositionMap fragmentPositionMap;

    public static DialogFragmentDownload newInstance(FragmentPositionMap fragmentPositionMap){
        DialogFragmentDownload dialogFragment = new DialogFragmentDownload();
        Bundle bundle = new Bundle();
        dialogFragment.setArguments(bundle);
        dialogFragment.setFragmentPositionMap(fragmentPositionMap);
        return dialogFragment;
    }

    private void setFragmentPositionMap(FragmentPositionMap fragmentPositionMap)
    {
        this.fragmentPositionMap = fragmentPositionMap;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_download, new LinearLayout(getActivity()), false);

        return new MaterialDialog.Builder(getActivity())
                .customView(layout, false)
                .positiveText(R.string.cancel)
                .positiveColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .negativeText(R.string.upload_map)
                .negativeColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        RadioGroup radioGroup = (RadioGroup)layout.findViewById(R.id.type_download_scv);
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton)layout.findViewById(selectedId);
                        String type = radioButton.getText().toString();
                        if (type.equals(getResources().getText(R.string.width_comment)))
                        {
                            fragmentPositionMap.DownloadScv(true);
                        }
                        else
                        {
                            fragmentPositionMap.DownloadScv(false);
                        }
                        dismiss();
                    }
                })
                .build();

    }
}