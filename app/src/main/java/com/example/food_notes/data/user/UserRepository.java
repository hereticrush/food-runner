package com.example.food_notes.data.user;

import android.app.Application;

import com.example.food_notes.db.ApplicationDatabase;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class UserRepository implements UserDataSource {

    private final UserDao mUserDao;
    private final Flowable<List<User>> mAllUsers;

    public UserRepository(UserDao userDao) {
        mUserDao = userDao;
        mAllUsers = mUserDao.getAllUsers();
    }

    @Override
    public Single<User> getUserById(int id) {
        return mUserDao.getUserById(id);
    }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return mAllUsers;
    }

    @Override
    public Completable insertUser(User user) {
        return Completable.wrap(mUserDao.insertUser(user));
    }

    @Override
    public Completable deleteUserByUsername(User user) {
        return mUserDao.deleteUserByUsername(user);
    }

    @Override
    public Completable deleteAllUsers() {
        return mUserDao.deleteAllUsers();
    }
}
