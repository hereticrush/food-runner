package com.example.food_notes.ui.view.model;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.user.User;
import com.example.food_notes.data.user.UserDataSource;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

/**
 * UserViewModel deals with database interactions of user entity
 */
public class UserViewModel extends ViewModel {

    private final UserDataSource mDataSource;
    private User mUser;

    public UserViewModel(UserDataSource repository) {
        mDataSource = repository;
    }

    public Completable updateUsername(final String username, final String password) {
        mUser = mUser == null ? new User(username, password)
                : new User(mUser.getUser_id(), username, password);
        return mDataSource.insertUser(mUser);
    }

    /**
     * Gets all users in the database
     * @return Flowable list of user objects
     */
    public Flowable<List<User>> getAllUsers() {
        return mDataSource.getAllUsers();
    }


}
