package com.example.food_notes.ui.view.factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;

public class AuthenticationViewModelFactory implements ViewModelProvider.Factory {

    private final UserDataSource mDataSource;
    Application application;

    public AuthenticationViewModelFactory(UserDataSource dataSource) {
        mDataSource = dataSource;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthenticationViewModel.class)) {
            return (T) new AuthenticationViewModel(mDataSource, application);
        }
        throw new IllegalArgumentException("ViewModel cannot be instantiated");
    }
}
