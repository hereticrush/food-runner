package com.example.food_notes.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentLoginBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class LoginFragment extends Fragment implements ApiClient{

    private static final String USERNAME = "USERNAME";
    private static final String CLICK_TEXT = "Click here to sign up";
    private static final String FRAGMENT_TAG = LoginFragment.class.getSimpleName();

    // view model factory and view model
    private AuthenticationViewModelFactory mFactory;
    private AuthenticationViewModel mAuthViewModel;

    //view binding
    private FragmentLoginBinding binding;

    // container for rxjava objects
    private final CompositeDisposable disposable = new CompositeDisposable();

    // ui element in the fragment
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private AppCompatButton button;


    private BottomNavigationView bottomNavigationView;

    /**
     * Fragment constructor with no arguments
     */
    public LoginFragment(){}

    /**
     * If username data that need to be passed to next fragment,
     * this function can be used to construct the fragment, added with bundle
     * @param username user.username
     * @return an instance of LoginFragment loaded with arguments
     */
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

        // initialize the view model from factory
        mFactory = Injection.provideAuthViewModelFactory(requireActivity().getApplication());
        mAuthViewModel = mFactory.create(AuthenticationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // initialize view binding
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        editTextUsername = binding.etLoginUsername;
        editTextPassword = binding.etLoginPassword;
        button = binding.loginButton;
        final NavController navController = NavHostFragment.findNavController(this);

        // creates a partially clickable text that navigates user to SignupFragment
        SpannableString spannableString = new SpannableString(CLICK_TEXT);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                navController.navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment2());
            }
        };

        // styling clickable text
        spannableString.setSpan(clickableSpan, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvLoginSuggestion.setText(spannableString);
        binding.tvLoginSuggestion.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvLoginSuggestion.setHighlightColor(Color.TRANSPARENT);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button.setOnClickListener(v -> {
            final String username = editTextUsername.getText().toString();
            final String password = editTextPassword.getText().toString();
            // check input fields for user input
            if (checkRequiredFields()) {
                // query database for user item
                disposable.add(mAuthViewModel.getUser(username, password)
                        .subscribe(
                                action -> onSuccess(),
                                throwable -> Log.e("ERROR", throwable.getLocalizedMessage())
                        ));
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
        binding = null; // when this fragment's lifetime is over, viewbinding pointer will be set to null
    }

    /**
     * Navigates user to UserMainFragment
     */
    private void toUserMainFragment() {
        final NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        final NavController navController = navHostFragment.getNavController();
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToUserMainFragment());
    }

    /**
     * Validates if the current user input(username, password) is according to
     * the rules, and makes sure that the input fields are not empty
     */
    private boolean checkRequiredFields() {
        if (TextUtils.isEmpty(editTextUsername.getText()) &&
        TextUtils.isEmpty(editTextPassword.getText())) {
            editTextUsername.setError("Please enter a username");
            editTextPassword.setError("Please enter a password");
            return false;
        }
        if (TextUtils.isEmpty(editTextUsername.getText())) {
            editTextUsername.setError("Please enter a username");
            return false;
        }
        if (TextUtils.isEmpty(editTextPassword.getText())) {
            editTextPassword.setError("Please enter a password");
            return false;
        } else if (editTextUsername.getText().toString().length() < 8 || editTextPassword.getText().toString().length() < 8) {
            editTextUsername.setError("At least 8 characters long");
            editTextPassword.setError("At least 8 characters long");
            return false;
        }
        return true;
    }

    /**
     * If query response is successful,
     * this function shows a Toast message and
     * navigates valid user to next fragment
     */
    @Override
    public void onSuccess() {
        requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity().getApplicationContext(), "Welcome " + editTextUsername.getText().toString(), Toast.LENGTH_SHORT).show());
        toUserMainFragment();
    }

    @Override
    public void onFailed(String log) {
        requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity().getApplicationContext(), log, Toast.LENGTH_SHORT).show());
        Log.e("FAILED", log);
    }
}