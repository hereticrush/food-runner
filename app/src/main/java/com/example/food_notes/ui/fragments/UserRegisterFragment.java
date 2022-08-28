package com.example.food_notes.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentUserRegisterBinding;
import com.example.food_notes.ui.view.ApiRegister;
import com.example.food_notes.ui.view.CustomHandlers;
import com.example.food_notes.ui.view.model.RegisterViewModel;
import com.example.food_notes.ui.view.util.text.CustomToastMessage;

import io.reactivex.rxjava3.core.Completable;

public class UserRegisterFragment extends Fragment implements ApiRegister  {

    private static final String FRAGMENT_TAG = "register";
    private RegisterViewModel mRegisterViewModel;
    private FragmentUserRegisterBinding binding;
    private CustomHandlers mClickHandler;
    private CustomToastMessage toaster;

    public UserRegisterFragment() {}

    public static UserRegisterFragment getInstance() { return new UserRegisterFragment(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this.requireActivity(), R.layout.fragment_user_register);
        binding.setLifecycleOwner(requireActivity());
        binding.setRegViewModel(mRegisterViewModel);
        binding.setHandler(mClickHandler);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leak
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRegisterViewModel.onRegisterButtonClicked();
    }

    private void toLoginActivity() {
        //Bundle bundle = new Bundle();
        //bundle.putString("USERNAME", binding);
        //bundle.putString("PASSWORD", Objects.requireNonNull(editTextPassword.getText()).toString().trim());
        LoginFragment fragment = LoginFragment.getInstance(); //this can get params from previous fragment and pass the data
        //fragment.setArguments(bundle);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction().setReorderingAllowed(true).replace(
                R.id.fragmentContainerView, fragment, null
        ).addToBackStack(FRAGMENT_TAG).commit();
    }

    @Override
    public void onSuccess(Completable completable) {
        toaster.toast("Registered successfully.");
        toLoginActivity();
    }

    @Override
    public void onFailed(String log) {
        toaster.toast(log);
    }
}