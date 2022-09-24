package com.example.food_notes.ui.view.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_notes.auth.AppAuthentication;
import com.example.food_notes.db.FirebaseDataSource;
import com.example.food_notes.db.FirebaseRepository;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Factory that creates an instance of AuthenticationViewModel object
 */
public class AuthenticationViewModelFactory implements ViewModelProvider.Factory {

    // connected to userRepository
    private final FirebaseDataSource mDataSource;
    private final FirebaseAuth mAuth;

    // constructor
    public AuthenticationViewModelFactory(FirebaseDataSource dataSource) {
        mDataSource = dataSource;
        mAuth = AppAuthentication.AUTH;
    }


    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthenticationViewModel.class)) {
            return (T) new AuthenticationViewModel(mDataSource);
        }
        throw new IllegalArgumentException("ViewModel cannot be instantiated");
    }
}
