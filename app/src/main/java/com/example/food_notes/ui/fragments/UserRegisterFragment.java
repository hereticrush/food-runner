package com.example.food_notes.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.food_notes.R;
import com.example.food_notes.data.user.User;
import com.example.food_notes.db.ApplicationExecutors;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.UserViewModel;
import com.example.food_notes.ui.view.ViewModelFactory;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRegisterFragment extends Fragment {

    private static final String TAG = UserRegisterFragment.class.getSimpleName();
    private ViewModelFactory mFactory;
    private UserViewModel mViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private AppCompatEditText et_username;
    private AppCompatEditText et_password;
    private AppCompatButton mButton;

    public UserRegisterFragment() {}

    public static UserRegisterFragment getInstance() { return new UserRegisterFragment(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFactory = Injection.provideViewModelFactory(getActivity());
        mViewModel = new ViewModelProvider(this, mFactory).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_register, container, false);

        mButton = view.findViewById(R.id.user_register_button);
        et_username = view.findViewById(R.id.et_register_username);
        et_password = view.findViewById(R.id.et_register_password);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        mButton.setOnClickListener(v -> {
            Boolean VALID = addUserToDatabase();
            if (!queryDatabaseForRegisteredUser(username, password) && !VALID) {
                Toast.makeText(getActivity().getApplicationContext(), "No entry", Toast.LENGTH_SHORT).show();
            } else if (queryDatabaseForRegisteredUser(username, password) && VALID) {
                toLoginActivity();
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
        bundle.putString("USERNAME", et_username.getText().toString());
        bundle.putString("PASSWORD", et_password.getText().toString());
        LoginFragment fragment = LoginFragment.getInstance(); //TODO this can get params from previous fragment and pass the data
        fragment.setArguments(bundle);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction().setReorderingAllowed(true).replace(
                R.id.fragmentContainerView, fragment, null
        ).addToBackStack("register").commit();
    }

    private boolean queryDatabaseForRegisteredUser(final String username, final String password) {

        return mDisposable.add(mViewModel.getUser(username, password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).map(
                        user -> {
                            String rUsername = user.getUsername();
                            String rPassword = user.getPassword();
                            return rUsername.matches(username) && rPassword.matches(password);
                        }
                ).subscribe());
    }

    private Boolean addUserToDatabase() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        if (!checkCredentialValidity(username, password)) {
            Toast.makeText(getActivity().getApplicationContext(), "Credentials not valid", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mDisposable.add(mViewModel.updateUsername(username, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> mButton.setEnabled(true), throwable -> Log.e(TAG, "Unable to update username", throwable)));
            return true;
        }
    }

    private boolean checkCredentialValidity(@NonNull String username, String password) {

        if (username.startsWith(" ") || password.startsWith(" ")) {
            Toast.makeText(getActivity().getApplicationContext(), "Cannot start with whitespace", Toast.LENGTH_SHORT).show();
            return false;
        } else if (username.trim().length() < 8 || password.trim().length() < 8) {
            Toast.makeText(getActivity().getApplicationContext(), "No less than 8", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(et_username.getText())) {
            Toast.makeText(getActivity().getApplicationContext(), "Username required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(et_password.getText())) {
            Toast.makeText(getActivity().getApplicationContext(), "Password req", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}