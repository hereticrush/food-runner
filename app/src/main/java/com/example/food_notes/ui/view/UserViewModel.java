package com.example.food_notes.ui.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.user.User;
import com.example.food_notes.data.user.UserDataSource;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;


public class UserViewModel extends AndroidViewModel {

    private final UserDataSource mDataSource;
    private User mUser;

    public UserViewModel(UserDataSource repository, Application application) {
        super(application);
        mDataSource = repository;
    }

    //TODO make sure this function queries correctly
    public Flowable<User> getUser(final String username, final String password) {
        return mDataSource.getUser(username, password).map(
                user -> user.getUsername().matches(username) && user.getPassword().matches(password)
                ? user : null);
    }

    public Completable updateUsername(final String username, final String password) {
        mUser = mUser == null ? new User(username, password)
                : new User(mUser.getUser_id(), username, password);
        return mDataSource.insertOrUpdateUser(mUser);
    }

    public Flowable<List<User>> getAllUsers() {
        return mDataSource.getAllUsers();
    }


}
