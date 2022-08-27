package com.example.food_notes.injection;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.food_notes.data.foodpost.FoodPostDataSource;
import com.example.food_notes.data.foodpost.FoodPostRepository;
import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.data.user.UserRepository;
import com.example.food_notes.db.ApplicationDatabase;
import com.example.food_notes.ui.view.FoodPostModelViewFactory;
import com.example.food_notes.ui.view.UserViewModelFactory;

public class Injection {

    @NonNull
    public static UserDataSource provideUserDataSource(Application application) {
        ApplicationDatabase database = ApplicationDatabase.getInstance(application);
        return new UserRepository(application);
    }

    @NonNull
    public static UserViewModelFactory provideViewModelFactory(Application application) {
        UserDataSource repository = provideUserDataSource(application);
        return new UserViewModelFactory(repository);
    }

    /*@NonNull
    public static FoodPostDataSource provideFoodPostDataSource(Application application) {
        ApplicationDatabase database = ApplicationDatabase.getInstance(application);
        return new FoodPostRepository(database.foodPostDao());
    }

    @NonNull
    public static FoodPostModelViewFactory provideFoodPostViewModelFactory(Context context) {
        FoodPostDataSource repository = provideFoodPostDataSource(context);
        return new FoodPostModelViewFactory(repository);
    }*/
}
