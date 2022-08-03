package com.example.food_notes.db;

import android.content.Context;

import com.example.food_notes.data.foodpost.FoodPostDataSource;
import com.example.food_notes.data.user.UserDataSource;

public interface DataSourceProvider {
    UserDataSource provideUserDataSource(final Context context);

    FoodPostDataSource provideFoodPostDataSource(final Context context);
}
