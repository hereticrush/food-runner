package com.example.food_notes.ui.view.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_notes.data.foodpost.FoodPostDataSource;
import com.example.food_notes.ui.view.model.FoodPostViewModel;

public class FoodPostModelViewFactory implements ViewModelProvider.Factory {

    private final FoodPostDataSource mDataSource;

    public FoodPostModelViewFactory(FoodPostDataSource dataSource) {
        mDataSource = dataSource;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FoodPostViewModel.class)) {
            return (T)(new FoodPostViewModel(mDataSource));
        }
        throw new IllegalArgumentException("ViewModel class not found");
    }
}
