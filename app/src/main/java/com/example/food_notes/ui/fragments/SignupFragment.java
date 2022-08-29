package com.example.food_notes.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentSignupBinding;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.CustomHandlers;
import com.example.food_notes.ui.view.util.text.CustomToastMessage;

public class SignupFragment extends Fragment implements ApiClient {

    private static final String FRAGMENT_TAG = "register";
    private CustomHandlers mClickHandler;
    private CustomToastMessage toaster;
    private FragmentSignupBinding binding;

    private SignupFragment() {}

    public static SignupFragment getInstance() { return new SignupFragment(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leak
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void toLoginActivity() {
        //Bundle bundle = new Bundle();
        //bundle.putString("USERNAME", binding);
        //bundle.putString("PASSWORD", Objects.requireNonNull(editTextPassword.getText()).toString().trim());
        LoginFragment fragment = LoginFragment.getInstance(); //this can get params from previous fragment and pass the data
        //fragment.setArguments(bundle);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction().setReorderingAllowed(true).replace(
                R.id.userMainFragment, fragment, null
        ).addToBackStack(FRAGMENT_TAG).commit();
    }

    @Override
    public void onSuccess() {
        toaster.toast("Registered successfully.");
    }

    @Override
    public void onFailed(String log) {
        toaster.toast(log);
    }
}