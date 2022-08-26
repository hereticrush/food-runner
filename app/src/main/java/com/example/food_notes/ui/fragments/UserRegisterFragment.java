package com.example.food_notes.ui.fragments;

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
import com.example.food_notes.databinding.FragmentUserRegisterBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.UserViewModel;
import com.example.food_notes.ui.view.ViewModelFactory;

import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRegisterFragment extends Fragment {

    private static final String TAG = UserRegisterFragment.class.getSimpleName();
    private UserViewModel mViewModel;
    private FragmentUserRegisterBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private AppCompatEditText et_username;
    private AppCompatEditText et_password;
    private AppCompatButton mButton;

    public UserRegisterFragment() {}

    public static UserRegisterFragment getInstance() { return new UserRegisterFragment(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelFactory mFactory = Injection.provideViewModelFactory(getActivity());
        mViewModel = new ViewModelProvider(this, mFactory).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserRegisterBinding.inflate(inflater, container, false);
        et_username = binding.etRegisterUsername;
        et_password = binding.etRegisterPassword;
        mButton = binding.userRegisterButton;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String username = et_username.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        mButton.setOnClickListener(v -> {
            if (checkCredentialValidity(username, password)) {
                addUserToDatabase();
            } else {
                Log.d("REGEX", "Do not match");
                Toast.makeText(getActivity(), "No entry", Toast.LENGTH_SHORT).show();
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
        LoginFragment fragment = LoginFragment.getInstance(); //this can get params from previous fragment and pass the data
        fragment.setArguments(bundle);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction().setReorderingAllowed(true).replace(
                R.id.fragmentContainerView, fragment, null
        ).addToBackStack("register").commit();
    }

    private void addUserToDatabase() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
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

    private boolean checkCredentialValidity(final String username,final String password) {

        Pattern pattern = Pattern.compile("(^[[\\p{Alnum}!\\-@$_][^\\s]]{8,24}$)");
        Matcher matcherA = pattern.matcher(username);
        Matcher matcherB = pattern.matcher(password);
        if (matcherA.matches()) {
            return matcherB.matches();
        }
        return false;
    }
}