package com.example.food_notes.ui.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_notes.data.foodpost.FoodPostDataSource;

public class FoodPostModelViewFactory implements ViewModelProvider.Factory {

    private final FoodPostDataSource mDataSource;
    Application application;

    public FoodPostModelViewFactory(FoodPostDataSource dataSource) {
        mDataSource = dataSource;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FoodPostViewModel.class)) {
            return (T)(new FoodPostViewModel(mDataSource, application));
        }
        throw new IllegalArgumentException("ViewModel class not found");
    }
}
