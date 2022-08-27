package com.example.food_notes.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentLoginBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.ApiLogin;
import com.example.food_notes.ui.view.UserViewModel;
import com.example.food_notes.ui.view.UserViewModelFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;
import com.example.food_notes.ui.view.util.text.CustomToastMessage;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class LoginFragment extends Fragment implements ApiLogin {

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String TAG = "login";

    private FragmentLoginBinding binding;
    private AuthenticationViewModel mAuthViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private AppCompatButton mButton;
    private AppCompatEditText editTextUsername;
    private  AppCompatEditText editTextPassword;

    private CustomToastMessage toaster;

    private LoginFragment(){}

    public static LoginFragment getInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(getActivity(), R.layout.fragment_login);
        mAuthViewModel = new ViewModelProvider(this.getActivity()).get(AuthenticationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        binding.fragmentLoginButton.setOnClickListener(v -> {
            if (checkInputFields()) {
                 if (requestLoginIfUserExists(username, password)) {
                     Bundle args = new Bundle();
                     args.putString("LOGGED_USER", username);
                     args.putString("LOGGED_PASSWORD", password);
                     setArguments(args);
                     toUserActivity();
                } else {
                    Toast.makeText(getActivity(), "No entry, invalid user", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Please fill the required fields.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    //TODO write this func
    private Boolean checkInputFields() {
        if (TextUtils.isEmpty(binding.etLoginUsername.getText()) && TextUtils.isEmpty(binding.etLoginPassword.getText())) {
            Toast.makeText(getActivity(), "Required fields must be filled.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(binding.etLoginPassword.getText())) {
            binding.etLoginPassword.setError("Password is required.");
            return false;
        } else if (binding.etLoginUsername.getText().toString().trim().length() < 8) {
            binding.etLoginUsername.setError("Username must be at least 8 characters long.");
            return false;
        } else if (binding.etLoginPassword.getText().toString().trim().length() < 8) {
            binding.etLoginPassword.setError("Password must be at least 8 character long.");
            return false;
        } else
            return true;
    }

    private void success() {
        Toast.makeText(getActivity(), "Successfully logged in", Toast.LENGTH_SHORT).show();
    }

    private void toUserActivity() {
        UserMainFragment fragment = UserMainFragment.getInstance();
        FragmentManager manager = getParentFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setReorderingAllowed(true).replace(
                R.id.fragmentContainerView, fragment, TAG
        ).addToBackStack(TAG).commit();
    }

    @Override
    public void onReady() {
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