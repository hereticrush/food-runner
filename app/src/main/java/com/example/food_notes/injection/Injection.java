package com.example.food_notes.injection;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.data.user.UserRepository;
import com.example.food_notes.db.ApplicationDatabase;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.factory.UserViewModelFactory;

public class Injection {

    @NonNull
    public static UserDataSource provideUserDataSource(Context context) {
        ApplicationDatabase database = ApplicationDatabase.getInstance(context);
        return new UserRepository(database.userDao());
    }

    @NonNull
    public static UserViewModelFactory provideUserViewModelFactory(Context context) {
        UserDataSource repository = provideUserDataSource(context);
        return new UserViewModelFactory(repository);
    }

    @NonNull
    public static AuthenticationViewModelFactory provideAuthViewModelFactory(Context context) {
        UserDataSource repository = provideUserDataSource(context);
        return new AuthenticationViewModelFactory(repository);
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
