package com.example.food_notes.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentAddImageOptionsDialogBinding;

public class AddImageOptionsDialogFragment extends DialogFragment {

    private FragmentAddImageOptionsDialogBinding binding;

    public AddImageOptionsDialogFragment(){
        super(R.layout.fragment_add_image_options_dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddImageOptionsDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public static String TAG = "AddImageOptionsDialog";

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
