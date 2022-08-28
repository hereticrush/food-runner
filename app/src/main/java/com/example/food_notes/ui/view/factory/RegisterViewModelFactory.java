package com.example.food_notes.ui.view.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.ui.view.model.RegisterViewModel;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {

    private final UserDataSource mDataSource;

    public RegisterViewModelFactory(UserDataSource dataSource) {
        mDataSource = dataSource;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(mDataSource);
        }
        throw new IllegalArgumentException("ViewModel class cannot be instantiated");
    }
}
