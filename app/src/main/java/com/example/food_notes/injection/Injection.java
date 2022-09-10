package com.example.food_notes.injection;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.food_notes.data.foodpost.FoodPostDataSource;
import com.example.food_notes.data.foodpost.FoodPostRepository;
import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.data.user.UserRepository;
import com.example.food_notes.db.ApplicationDatabase;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.factory.FoodPostModelViewFactory;
import com.example.food_notes.ui.view.factory.UserViewModelFactory;

/**
 * Dependency injection model suggests some objects
 * should be dependent to other objects from outside
 * Injection class implements this type of behaviour
 */
public class Injection {

    /**
     * Provides an UserRepository object to connect
     * the data classes with viewmodel
     * @param context application context
     * @return UserDataSource interface
     */
    @NonNull
    public static UserDataSource provideUserDataSource(Context context) {
        ApplicationDatabase database = ApplicationDatabase.getInstance(context);
        return new UserRepository(database.userDao());
    }

    /**
     * Provides a FoodPostRepository object to connect
     * the data classes with viewmodel
     * @param context application context
     * @return FoodPostDataSource interface
     */
    @NonNull
    public static FoodPostDataSource provideFoodPostDataSource(Context context) {
        ApplicationDatabase database = ApplicationDatabase.getInstance(context);
        return new FoodPostRepository(database.foodPostDao());
    }

    /**
     * Provides a UserViewModel object to access the user repository
     * @param context
     * @return UserViewModelFactory object
     */
    @NonNull
    public static UserViewModelFactory provideUserViewModelFactory(Context context) {
        UserDataSource repository = provideUserDataSource(context);
        return new UserViewModelFactory(repository);
    }

    /**
     * Provides a factory to create AuthenticationViewModel class instance
     * to access the data repository(UserRepository)
     * @param context application context
     * @return AuthenticationViewModelFactory
     */
    @NonNull
    public static AuthenticationViewModelFactory provideAuthViewModelFactory(Context context) {
        UserDataSource repository = provideUserDataSource(context);
        return new AuthenticationViewModelFactory(repository);
    }

    /**
     * Provides a factory to create FoodPostViewModel class instance
     * to access the data layer(FoodPostRepository)
     * @param context application context
     * @return FoodPostViewModelFactory
     */
    @NonNull
    public static FoodPostModelViewFactory provideFoodPostViewModelFactory(Context context) {
        FoodPostDataSource repository = provideFoodPostDataSource(context);
        return new FoodPostModelViewFactory(repository);
    }
}
