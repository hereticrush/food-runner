package com.example.food_notes.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentSignupBinding;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.CustomHandlers;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SignupFragment extends Fragment implements ApiClient {

    private static final String FRAGMENT_TAG = "register";
    private FragmentSignupBinding binding;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private AppCompatButton btn;

    public SignupFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        btn = binding.userRegisterButton;
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
        btn.setOnClickListener(this::toLogin);
    }

    /**
     * Navigates user to Login Fragment
     */
    private void toLogin(View view) {
        final NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        final NavController navController = navHostFragment.getNavController();
        navController.navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment());
    }

    @Override
    public void onSuccess() {
        Toast.makeText(requireActivity().getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String log) {
        Toast.makeText(requireActivity().getApplicationContext(), log, Toast.LENGTH_SHORT).show();
    }
}