package com.example.food_notes.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.food_notes.data.user.User;
import com.example.food_notes.databinding.FragmentSignupBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SignupFragment extends Fragment implements ApiClient {

    private static final String TAG = "sign_up";
    private FragmentSignupBinding binding;
    private AuthenticationViewModelFactory mFactory;
    private AuthenticationViewModel mAuthViewModel;

    private FirebaseAuth mFirebaseAuth;

    private AppCompatButton btn;
    private AppCompatEditText editTextUsername;
    private AppCompatEditText editTextPassword;

    private NavController navController;

    public SignupFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init view model
        mFactory = Injection.provideAuthViewModelFactory(requireContext());
        mAuthViewModel = mFactory.create(AuthenticationViewModel.class);

        // init firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        // get navigation controller
        navController = NavHostFragment.findNavController(this);
        //init view binding
        btn = binding.userRegisterButton;
        editTextUsername = binding.etRegisterUsername;
        editTextPassword = binding.etRegisterPassword;
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leak
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //click on button
        btn.setOnClickListener(v -> attemptRegistration());
    }

    /**
     * Navigates user to Login Fragment
     */
    private void toLogin() {
        NavBackStackEntry navBackStackEntry = navController.getPreviousBackStackEntry();
        int destination = navController.getGraph().getStartDestinationId();
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(destination, true).build();
        navController.navigate(destination, null, navOptions);
    }

    @Override
    public void onSuccess() {
        requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity().getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show());
        toLogin();
    }

    @Override
    public void onFailed(String log) {
        requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity().getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show());
        Log.e("FAILED", log);
    }

    @Override
    public void whenCompleted() {
        Log.d("COMPLETED", "done");
    }

    /**
     * Attempts to sign up through Firebase Authentication,
     * if it is a success, user is also inserted into localDB
     */
    public void attemptRegistration() {

        final String email = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(requireActivity().getApplicationContext(), "Enter email and password", Toast.LENGTH_SHORT).show();
        } else {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnFailureListener(e -> onFailed(e.getLocalizedMessage()))
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();
                        mAuthViewModel.insertUserToLocalDB(new User(uid, email, password));
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity().getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show());
                        toLogin();
                        Log.d(TAG, "attemptRegistration: SUCCESS");
                    });
        }
    }

}