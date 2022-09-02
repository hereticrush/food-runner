package com.example.food_notes.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentLoginBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.activities.MainActivity;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.Observable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class LoginFragment extends Fragment implements ApiClient {

    private static final String USERNAME = "USERNAME";
    private static final String CLICK_TEXT = "Click here to sign up";
    private static final String FRAGMENT_TAG = LoginFragment.class.getSimpleName();

    private AuthenticationViewModelFactory mFactory;
    private AuthenticationViewModel mAuthViewModel;
    private FragmentLoginBinding binding;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private AppCompatButton button;
    private BottomNavigationView bottomNavigationView;

    public LoginFragment(){}

    @Nullable
    public static LoginFragment newInstance(String username) {
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFactory = Injection.provideAuthViewModelFactory(requireActivity().getApplication());
        mAuthViewModel = mFactory.create(AuthenticationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        editTextUsername = binding.etLoginUsername;
        editTextPassword = binding.etLoginPassword;
        final NavController navController = NavHostFragment.findNavController(this);

        SpannableString spannableString = new SpannableString(CLICK_TEXT);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                navController.navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment2());
            }
        };
        spannableString.setSpan(clickableSpan, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvLoginSuggestion.setText(spannableString);
        binding.tvLoginSuggestion.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvLoginSuggestion.setHighlightColor(Color.TRANSPARENT);
        button = binding.loginButton;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button.setOnClickListener(v -> {
            if (isInputValid())
                attemptLogin();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        disposable.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void toUserActivity() {
        requireActivity().getSupportFragmentManager().beginTransaction()
        .setReorderingAllowed(true).replace(
                R.id.userMainFragment, new UserMainFragment(), FRAGMENT_TAG
        ).commit();
    }


    private void attemptLogin() {

        /*disposable.add(mAuthViewModel.getUser(username).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe((user, throwable) -> {
                    String LOGGED_USER = user.getUsername();
                    Log.e(FRAGMENT_TAG, "Error occurred", throwable);
                }));*/
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        System.out.println(username + " " + password);
        disposable.add(mAuthViewModel.insertUser(username, password)
                .doOnComplete(this::toUserActivity).subscribe(this::onSuccess));
        System.out.println("done");

    }

    @Override
    public void onSuccess() {
        Toast.makeText(requireActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String log) {
        Toast.makeText(requireActivity().getApplicationContext(), log, Toast.LENGTH_SHORT).show();
    }

    private boolean isInputValid() {
        if (TextUtils.isEmpty(editTextUsername.getText()) &&
        TextUtils.isEmpty(editTextPassword.getText())) {
            editTextUsername.setError("");
            editTextPassword.setError("");
            return false;
        }
        if (TextUtils.isEmpty(editTextUsername.getText())) {
            editTextUsername.setError("Please enter a username");
            return false;
        }
        if (TextUtils.isEmpty(editTextPassword.getText())) {
            editTextPassword.setError("Please enter a password");
            return false;
        }
        return true;
    }
}