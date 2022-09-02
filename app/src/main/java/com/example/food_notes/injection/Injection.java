package com.example.food_notes.injection;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.data.user.UserRepository;
import com.example.food_notes.db.ApplicationDatabase;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.factory.UserViewModelFactory;

/**
 * Dependency injection model suggests some objects
 * should be dependent to other objects from outside
 * Injection class implements this type of behaviour
 */
public class Injection {

    /**
     * Provides an UserRepository object to connect
     * the data classes with viewmodels
     * @param context
     * @return UserDataSource object
     */
    @NonNull
    public static UserDataSource provideUserDataSource(Context context) {
        ApplicationDatabase database = ApplicationDatabase.getInstance(context);
        return new UserRepository(database.userDao());
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
     * Provides an Authentication View Model object
     * to access the data repository(in this case user repository)
     * @param context
     * @return AuthenticationViewModel object
     */
    @NonNull
    public static AuthenticationViewModelFactory provideAuthViewModelFactory(Context context) {
        UserDataSource repository = provideUserDataSource(context);
        return new AuthenticationViewModelFactory(repository);
    }

}
