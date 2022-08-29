package com.example.food_notes.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentLoginBinding;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;
import com.example.food_notes.ui.view.util.text.CustomToastMessage;


public class LoginFragment extends Fragment implements ApiClient {

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String FRAGMENT_TAG = "login";

    private FragmentLoginBinding binding;
    private AuthenticationViewModel mAuthViewModel;
    private CustomToastMessage toaster;

    private LoginFragment(){}

    public static LoginFragment getInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this.requireActivity(), R.layout.fragment_login);
        mAuthViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        binding.setLifecycleOwner(this.requireActivity());
        binding.setAuthViewModel(mAuthViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void toUserActivity() {
        UserMainFragment fragment = UserMainFragment.getInstance();
        FragmentManager manager = getParentFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setReorderingAllowed(true).replace(
                R.id.userMainFragment, fragment, FRAGMENT_TAG
        ).addToBackStack(FRAGMENT_TAG).commit();
    }


    @Override
    public void onSuccess() {
        toaster.toast("Login is successful.");
       toUserActivity();
    }

    @Override
    public void onFailed(String log) {
        toaster.toast(log);
    }
}