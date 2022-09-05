package com.example.food_notes.ui.view.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;

/**
 * Factory that creates an instance of AuthenticationViewModel object
 */
public class AuthenticationViewModelFactory implements ViewModelProvider.Factory {

    // connected to userRepository
    private final UserDataSource mDataSource;

    // constructor
    public AuthenticationViewModelFactory(UserDataSource dataSource) {
        mDataSource = dataSource;
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
