package com.example.food_notes.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.food_notes.R;


public class AddImageOptionsDialogFragment extends DialogFragment {

    public interface AddImageOptionsListener {
        void onDialogStorageOptionClick(DialogFragment dialog);
        void onDialogCameraOptionClick(DialogFragment dialog);
    }

    AddImageOptionsListener listener;

    public AddImageOptionsDialogFragment(){
        super(R.layout.fragment_add_image_options_dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_option_a, (dialog, which) -> {
                    listener.onDialogStorageOptionClick(this);
                })
                .setNegativeButton(R.string.dialog_option_b, (dialog, which) -> {
                    listener.onDialogCameraOptionClick(this);
                });

        return builder.create();
    }


    public static String TAG = "AddImageOptionsDialog";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.listener = (AddImageOptionsListener) getParentFragment();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: ERROR must implement AddImageOptionsListener");
        }
    }
}
