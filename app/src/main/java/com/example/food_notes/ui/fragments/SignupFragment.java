package com.example.food_notes.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.food_notes.R;
import com.example.food_notes.auth.AppAuthentication;
import com.example.food_notes.databinding.FragmentSignupBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;

public class SignupFragment extends Fragment {

    private static final String TAG = "sign_up";
    private FragmentSignupBinding binding;
    private AuthenticationViewModelFactory mFactory;
    private AuthenticationViewModel mAuthViewModel;

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

        // init firebase
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
     * Navigates user to LoginFragment
     */
    private void toLogin() {
        NavBackStackEntry navBackStackEntry = navController.getPreviousBackStackEntry();
        int destination = navController.getGraph().getStartDestinationId();
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(destination, true).build();
        navController.navigate(destination, null, navOptions);
    }

    /**
     * Attempts to sign up through Firebase Authentication,
     * if it is a success, user is inserted into both localDB and Firestore
     */
    public void attemptRegistration() {

        final String email = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(requireActivity().getApplicationContext(), "Enter email and password", Toast.LENGTH_SHORT).show();
        } else {
            AppAuthentication.AUTH.createUserWithEmailAndPassword(email, password)
                    .addOnFailureListener(e -> Toast.makeText(requireContext() ,R.string.an_error_occurred, Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(requireContext().getApplicationContext(), R.string.user_registered_success, Toast.LENGTH_SHORT).show();
                        String uid = authResult.getUser().getUid();
                        addUserDocumentToFirestore(uid, email);
                        toLogin();
                    });
        }
    }

    /**
     * Adds a user document to Firestore if login was successful
     * @param uid firebaseUser uid
     * @param email firebase user_email
     */
    private void addUserDocumentToFirestore(final String uid, final String email) {
        if (uid != null && email != null) {
            mAuthViewModel.createUserDocumentForFirestore(uid, email);
        }
    }


}