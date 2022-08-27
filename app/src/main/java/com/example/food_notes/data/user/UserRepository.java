package com.example.food_notes.data.user;

import android.app.Application;

import com.example.food_notes.db.ApplicationDatabase;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class UserRepository implements UserDataSource {

    private final UserDao mUserDao;
    private Single<User> mUser;
    private Flowable<List<User>> mAllUsers;

    public UserRepository(Application application) {
        ApplicationDatabase database = ApplicationDatabase.getInstance(application);
        mUserDao = database.userDao();
        mAllUsers = mUserDao.getAllUsers();
    }

    @Override
    public Single<User> getUser(Long id) { return mUserDao.getUser(id); }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return mUserDao.getAllUsers();
    }

    @Override
    public Completable insertOrUpdateUser(User user) {
        return mUserDao.insertOrUpdateUser(user);
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
