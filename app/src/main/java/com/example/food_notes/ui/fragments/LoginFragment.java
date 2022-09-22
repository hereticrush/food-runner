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
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.food_notes.R;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import kotlinx.coroutines.flow.StateFlow;


public class LoginFragment extends Fragment implements ApiClient{

    public static String USER_ID = "USER_ID";
    public static String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";
    private static final String CLICK_TEXT = "Click here to sign up";
    private static final String TAG = "login";
    private StateFlow<String> mUid;

    // view model factory and view model
    private AuthenticationViewModelFactory mFactory;
    private AuthenticationViewModel mAuthViewModel;

    // firebase auth
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

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
     * @param uid user.uid
     * @return an instance of LoginFragment loaded with arguments
     */
    @Nullable
    public static LoginFragment newInstance(String uid) {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the view model from factory
        mFactory = Injection.provideAuthViewModelFactory(requireActivity().getApplicationContext());
        mAuthViewModel = mFactory.create(AuthenticationViewModel.class);

        // init navigation component
        navController = NavHostFragment.findNavController(this);
        navBackStackEntry = navController.getCurrentBackStackEntry();

        // init firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // if user was logged in once and close the app without logout, next time app will immediately
        // start from userMainFragment
        FirebaseUser user = mAuth.getCurrentUser();
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

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button.setOnClickListener(v -> attemptLogin());

    }

    /**
     * Attempts login through Firebase Authentication
     */
    public void attemptLogin() {

        String email = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(requireActivity().getApplicationContext(), "Enter email and password", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onFailed(e.getLocalizedMessage());
                            toast("Invalid credentials");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            final String authenticatedUserId = authResult.getUser().getUid();
                            toast("Welcome, "+authResult.getUser().getEmail());
                            toUserMainFragment(authenticatedUserId);
                        }
                    });
        }
    }

    /*/**
     * Attempts to get User from local database.
     * If successful but user item is not found in localDB,
     * inserts user into localDB and then navigates to UserMainFragment.
     * If successful and user item is already found in localDB, navigates to UserMainFragment.
     * Also wraps the query with a CompositeDisposable container
     * @param uid FirebaseUser.uid
     */
    /*public void getUserFromLocalDB(final String uid) {

        disposable.add(mAuthViewModel.getUser(uid)
                .doOnError(throwable ->  {
                    toast(throwable.getLocalizedMessage());
                    onFailed(throwable.getMessage());
                })
                .doOnComplete(() -> {
                    whenCompleted();
                })
                .doOnSuccess(user -> {

                    mAuthViewModel.insertUserToLocalDB(user);
                    Log.d(TAG, "getUserFromLocalDB: ON_SUCCESS");
                })
                .doAfterSuccess(user -> {

                    mAuthViewModel.insertUserToLocalDB(new User(uid));
                    Log.d(TAG, "getUserFromLocalDB: AFTER_SUCCESS");
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

    }*/

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