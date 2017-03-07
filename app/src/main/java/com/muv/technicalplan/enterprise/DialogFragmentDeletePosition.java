package com.muv.technicalplan.enterprise;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.muv.technicalplan.R;

public class DialogFragmentDeletePosition extends DialogFragment implements View.OnClickListener
{
    private FragmentPositionMap fragmentPositionMap;

    public static DialogFragmentDeletePosition newInstance(FragmentPositionMap fragmentPositionMap){
        DialogFragmentDeletePosition dialogFragment = new DialogFragmentDeletePosition();
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
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_position, new LinearLayout(getActivity()), false);

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
                        fragmentPositionMap.deleteFragment();
                        dismiss();
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

                break;
        }
    }

}