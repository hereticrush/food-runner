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
import com.example.food_notes.auth.AppAuthentication;
import com.example.food_notes.databinding.FragmentLoginBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class LoginFragment extends Fragment {

    public static String USER_ID = "USER_ID";
    private static final String CLICK_TEXT = "Click here to sign up";
    private static final String TAG = "login";

    // view model factory and view model
    private AuthenticationViewModelFactory mFactory;
    private AuthenticationViewModel mAuthViewModel;

    // firebase auth
    private FirebaseAuth auth;

    //view binding
    private FragmentLoginBinding binding;

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
        auth = mAuthViewModel.getAuth();

        // if user was logged in once and close the app without logout, next time app will immediately
        // start from userMainFragment
        if (auth.getCurrentUser() != null) {
            toUserMainFragment(auth.getCurrentUser().getUid());
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

        // clicking on login button
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
            auth.signInWithEmailAndPassword(email, password)
                    .addOnFailureListener(e -> {
                        toast("Invalid credentials");
                    }).addOnSuccessListener(authResult -> {
                        final String authenticatedUserId = authResult.getUser().getUid();
                        toast("Welcome, "+authResult.getUser().getEmail());
                        toUserMainFragment(authenticatedUserId);
                    });
        }
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


}