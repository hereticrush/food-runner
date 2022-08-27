package com.example.food_notes.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentUserRegisterBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.UserViewModel;
import com.example.food_notes.ui.view.UserViewModelFactory;
import com.example.food_notes.ui.view.util.regex.RegexValidation;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRegisterFragment extends Fragment {

    private UserViewModel mViewModel;
    private FragmentUserRegisterBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final RegexValidation validation = new RegexValidation();
    private AppCompatEditText editTextUsername;
    private AppCompatEditText editTextPassword;
    private AppCompatButton mButton;

    public UserRegisterFragment() {}

    public static UserRegisterFragment getInstance() { return new UserRegisterFragment(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserViewModelFactory mFactory = Injection.provideViewModelFactory(getActivity());
        mViewModel = new ViewModelProvider(this, mFactory).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserRegisterBinding.inflate(inflater, container, false);
        editTextUsername = binding.etRegisterUsername;
        editTextPassword = binding.etRegisterPassword;
        mButton = binding.userRegisterButton;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valid = checkCredentialValidity(editTextUsername.getText().toString().trim(),
                        editTextPassword.getText().toString().trim());
                if (valid) {
                    addUserToDatabase();
                }
                else Toast.makeText(getActivity(), "No entry", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    private void toLoginActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("USERNAME", Objects.requireNonNull(editTextUsername.getText()).toString().trim());
        bundle.putString("PASSWORD", Objects.requireNonNull(editTextPassword.getText()).toString().trim());
        LoginFragment fragment = LoginFragment.getInstance(); //this can get params from previous fragment and pass the data
        fragment.setArguments(bundle);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction().setReorderingAllowed(true).replace(
                R.id.fragmentContainerView, fragment, null
        ).addToBackStack("register").commit();
    }

    private void addUserToDatabase() {
        String username = Objects.requireNonNull(editTextUsername.getText()).toString().trim();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString().trim();
        mDisposable.add(mViewModel.updateUsername(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getActivity(), "Successfully added user.", Toast.LENGTH_SHORT).show();
                        toLoginActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @NonNull
    private Boolean checkCredentialValidity(String username, String password) {
        if (!(validation.validateUsername(username) || validation.validatePassword(password))) {
            Toast.makeText(getActivity(), "Please fill the required fields", Toast.LENGTH_SHORT).show();
            System.out.println("1");
            return false;
        } else if (!validation.validatePassword(password)) {
            editTextPassword.setError("Password required");
            System.out.println("2");
            editTextUsername.setError(null);
            return false;
        } else if (!validation.validateUsername(username)) {
            editTextUsername.setError("Username required");
            System.out.println("3");
            editTextPassword.setError(null);
            return false;
        }
        System.out.println("assuming true");
        return true;
    }
}