package com.muv.technicalplan;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

public class DialogFragmentProgress extends DialogFragment
{
    private String text_progress;
    private boolean showing;

    public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
    }

    public static DialogFragmentProgress newInstance(String text_progress){

        DialogFragmentProgress dialogFragment = new DialogFragmentProgress();
        Bundle bundle = new Bundle();
        dialogFragment.setArguments(bundle);
        dialogFragment.setTextProgress(text_progress);
        return dialogFragment;
    }

    private void setTextProgress(String text_progress)
    {
        this.text_progress = text_progress;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_load, new LinearLayout(getActivity()), false);
        TextView text_progress = (TextView) layout.findViewById(R.id.text_progress);
        text_progress.setText(this.text_progress);

        return new MaterialDialog.Builder(getActivity())
                .customView(layout, false)
                .build();
    }
}
