package com.example.food_notes.ui.view.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.ui.view.model.UserViewModel;

/**
 * Factory that creates an instance of UserViewModel object
 */
public class UserViewModelFactory implements ViewModelProvider.Factory {

    private final UserDataSource mDataSource;


    public UserViewModelFactory(UserDataSource dataSource) { mDataSource = dataSource; }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom((UserViewModel.class))) {
            return (T) new UserViewModel(mDataSource);
        }
        throw new IllegalArgumentException("ViewModel class is not found");
    }
}
