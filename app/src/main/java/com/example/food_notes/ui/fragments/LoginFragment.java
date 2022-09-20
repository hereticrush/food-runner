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
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.food_notes.R;
import com.example.food_notes.data.user.User;
import com.example.food_notes.databinding.FragmentLoginBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableMaybeObserver;


public class LoginFragment extends Fragment implements ApiClient{

    public static String USER_ID = "USER_ID";
    private static final String USERNAME = "USERNAME";
    public static String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";
    private static final String CLICK_TEXT = "Click here to sign up";
    private static final String FRAGMENT_TAG = LoginFragment.class.getSimpleName();

    // view model factory and view model
    private AuthenticationViewModelFactory mFactory;
    private AuthenticationViewModel mAuthViewModel;
    private SavedStateHandle savedStateHandle;

    // firebase auth
    private FirebaseAuth mFirebaseAuth;

    //view binding
    private FragmentLoginBinding binding;

    // container for rxjava objects
    private final CompositeDisposable disposable = new CompositeDisposable();

    // ui element in the fragment
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private AppCompatButton button;

    private NavController navController;
    private NavBackStackEntry navBackStackEntry;

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
        mFactory = Injection.provideAuthViewModelFactory(requireActivity().getApplicationContext());
        mAuthViewModel = mFactory.create(AuthenticationViewModel.class);
        navController = NavHostFragment.findNavController(this);
        navBackStackEntry = navController.getCurrentBackStackEntry();

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            toUserMainFragment(user.getUid());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // initialize view binding
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        editTextUsername = binding.etLoginUsername;
        editTextPassword = binding.etLoginPassword;
        button = binding.loginButton;
        navController = NavHostFragment.findNavController(this);

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

        savedStateHandle = navController
                .getCurrentBackStackEntry().getSavedStateHandle();
        savedStateHandle.set(LOGIN_SUCCESSFUL, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button.setOnClickListener(v -> {
            final String username = editTextUsername.getText().toString();
            final String password = editTextPassword.getText().toString();
            // check input fields for user input
            attemptLogin();
            if (checkRequiredFields()) {
                // attempt login
                //loginUser(username, password);
            }
        });

    }

    public void attemptLogin() {

        String email = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(requireActivity().getApplicationContext(), "Enter email and password", Toast.LENGTH_SHORT).show();
        } else {
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onFailed(e.getLocalizedMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            toast("Welcome, "+authResult.getUser().getUid());
                            toUserMainFragment(authResult.getUser().getUid());
                        }
                    });
        }
    }

    /**
     * Subscribes an observer to viewModel in order to observe
     * the state of the database query by passing user credentials
     * and attempts an action depending on the response.
     * @param username String type username
     * @param password String type password
     */
    public void loginUser(final String username,final String password) {

        MaybeObserver<User> observer = new DisposableMaybeObserver<User>() {

            @Override
            public void onSuccess(@NonNull User user) {
                try {
                    String id = String.valueOf(user.getUser_id());
                    Log.d("SUCCESS", "uid:" + id);
                    toast("Welcome " + username);
                    savedStateHandle.set(LOGIN_SUCCESSFUL, true);
                    savedStateHandle.set(USER_ID, id);
                    toUserMainFragment(id);
                } catch (Exception e) {
                    toast(e.getLocalizedMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                toast(e.getMessage());
                onFailed(e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {
                toast("Invalid credentials");
                whenCompleted();
            }
        };
        mAuthViewModel.login(username, password)
                .subscribe(observer);
    }

    /**
     * Provides a Toast object prototype with custom message
     * @param msg String type message
     */
    private void toast(String msg) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStop() {
        disposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // when this fragment's lifetime is over, viewbinding pointer will be set to null
    }

    /**
     * Navigates user to UserMainFragment, also
     * passing username and login state data alongside
     */
    private void toUserMainFragment(final String uid) {
        Log.d("login ID", "id:"+uid);
        Bundle args = new Bundle();
        args.putString(USER_ID, uid);
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.userMainFragment, true, false)
                .build();
        navController.navigate(R.id.userMainFragment, args, navOptions);
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
        Log.d("SUCCESS", "done");
    }

    @Override
    public void onFailed(String log) {
        Log.e("FAILED", log);
    }

    @Override
    public void whenCompleted() {
        Log.d("COMPLETED", "done");
    }
}