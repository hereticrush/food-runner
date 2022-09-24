package com.example.food_notes.injection;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.food_notes.db.FirebaseDataSource;
import com.example.food_notes.db.FirebaseRepository;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.factory.FoodPostModelViewFactory;

/**
 * Dependency injection model suggests some objects
 * should be dependent to other objects from outside
 * Injection class implements this type of behaviour
 */
public class Injection {

    /**
     * Provides a factory to create AuthenticationViewModel class instance
     * to access the data repository(UserRepository)
     * @param context application context
     * @return AuthenticationViewModelFactory
     */
    @NonNull
    public static AuthenticationViewModelFactory provideAuthViewModelFactory(Context context) {
        FirebaseDataSource repository = new FirebaseRepository(context);
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
        FirebaseDataSource repository = new FirebaseRepository(context);
        return new FoodPostModelViewFactory(repository);
    }
}
