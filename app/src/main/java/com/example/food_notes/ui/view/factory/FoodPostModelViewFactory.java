package com.example.food_notes.ui.view.factory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_notes.databinding.FragmentUserMainBinding;
import com.example.food_notes.db.FirebaseDataSource;
import com.example.food_notes.ui.view.model.FoodPostViewModel;

public class FoodPostModelViewFactory implements ViewModelProvider.Factory {

    private final FirebaseDataSource mDataSource;

    public FoodPostModelViewFactory(FirebaseDataSource dataSource) {
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
