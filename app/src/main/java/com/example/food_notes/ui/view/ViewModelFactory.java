package com.example.food_notes.ui.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.data.user.UserRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final UserDataSource mDataSource;
    Application application;

    public ViewModelFactory(UserDataSource dataSource) { mDataSource = dataSource; }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom((UserViewModel.class))) {
            return (T) new UserViewModel(mDataSource, application);
        }
        throw new IllegalArgumentException("ViewModel class is not found");
    }
}
