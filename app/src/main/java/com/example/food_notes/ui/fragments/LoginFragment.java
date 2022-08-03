package com.example.food_notes.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.food_notes.R;
import com.example.food_notes.data.user.User;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.UserViewModel;
import com.example.food_notes.ui.view.ViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class LoginFragment extends Fragment {

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String TAG = "login";

    private UserViewModel mViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private AppCompatEditText et_username;
    private AppCompatEditText et_password;
    private AppCompatButton mButton;

    private LoginFragment(){}

    public static LoginFragment getInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelFactory mFactory = Injection.provideViewModelFactory(getActivity());
        mViewModel = new ViewModelProvider(this, mFactory).get(UserViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mButton = view.findViewById(R.id.fragment_login_button);
        et_username = view.findViewById(R.id.et_login_username);
        et_password = view.findViewById(R.id.et_login_password);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String username = Objects.requireNonNull(et_username.getText()).toString();
        String password = Objects.requireNonNull(et_password.getText()).toString();
        mButton.setOnClickListener(v -> {
            if (checkInputFields()) {
                 if (queryDatabaseForRegisteredUser()) {
                     Bundle args = new Bundle();
                     args.putString("LOGGED_USER", username);
                     args.putString("LOGGED_PASSWORD", password);
                     setArguments(args);
                     try {
                         requireActivity().runOnUiThread(() -> {
                             Toast.makeText(getActivity(), "Logging in...", Toast.LENGTH_SHORT).show();
                         });
                         Thread.sleep(1000);
                         toUserActivity();
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No entry, invalid user", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this.getActivity().getApplicationContext(), "Please fill the required fields.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }
    //TODO write this func
    private Boolean checkInputFields() {
        if (TextUtils.isEmpty(et_username.getText()) && TextUtils.isEmpty(et_password.getText())) {
            Toast.makeText(getActivity().getApplicationContext(), "Required fields must be filled.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(et_password.getText())) {
            et_password.setError("Password is required.");
            return false;
        } else if (et_username.getText().toString().trim().length() < 8) {
            et_username.setError("Username must be at least 8 characters long.");
            return false;
        } else if (et_password.getText().toString().trim().length() < 8) {
            et_password.setError("Password must be at least 8 character long.");
            return false;
        } else
            return true;
    }

    //TODO check this func something is fishy here
    private Boolean queryDatabaseForRegisteredUser() {
        return mDisposable.add(mViewModel.getUser(et_username.getText().toString(), et_password.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    private void toUserActivity() {
        UserMainFragment fragment = UserMainFragment.getInstance();
        FragmentManager manager = getParentFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setReorderingAllowed(true).replace(
                R.id.fragmentContainerView, fragment, TAG
        ).addToBackStack(TAG).commit();
    }

}