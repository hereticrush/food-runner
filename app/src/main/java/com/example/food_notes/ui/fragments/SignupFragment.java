package com.example.food_notes.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentSignupBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SignupFragment extends Fragment implements ApiClient {

    private static final String FRAGMENT_TAG = "register";
    private FragmentSignupBinding binding;
    private AuthenticationViewModelFactory mFactory;
    private AuthenticationViewModel mViewModel;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private AppCompatButton btn;
    private AppCompatEditText editTextUsername;
    private AppCompatEditText editTextPassword;

    public SignupFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init view model
        mFactory = Injection.provideAuthViewModelFactory(requireContext());
        mViewModel = mFactory.create(AuthenticationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        //init view binding
        btn = binding.userRegisterButton;
        editTextUsername = binding.etRegisterUsername;
        editTextPassword = binding.etRegisterPassword;
        return binding.getRoot();
    }

    @Override
    public void onStop() {
        disposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leak
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn.setOnClickListener(v -> {
            final String username = editTextUsername.getText().toString();
            final String password = editTextPassword.getText().toString();

            if (mViewModel.validateUserInput(username, password)) {
                mViewModel.insertUser(username, password)
                        .subscribe(this::onSuccess,
                                throwable -> onFailed(throwable.getLocalizedMessage()),
                                disposable);
                Toast.makeText(requireActivity().getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity().getApplicationContext(), "Please fill the required fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Navigates user to Login Fragment
     */
    private void toLogin() {
        final NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        final NavController navController = navHostFragment.getNavController();
        navController.navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment());
    }

    @Override
    public void onSuccess() {
        toLogin();
    }

    @Override
    public void onFailed(String log) {
        requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity().getApplicationContext(), log, Toast.LENGTH_SHORT).show());
        Log.e("FAILED", log);
    }
}