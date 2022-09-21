package com.example.food_notes.data.user;

import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public class UserRepository implements UserDataSource {

    private final UserDao mUserDao;
    private final Flowable<List<User>> mAllUsers;

    public UserRepository(UserDao userDao) {
        mUserDao = userDao;
        mAllUsers = mUserDao.getAllUsers();
    }


    @Override
    public Maybe<User> getUserById(String id) {
        return mUserDao.getUserById(id);
    }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return mAllUsers;
    }

    @Override
    public Completable insertUser(User user) {
        return mUserDao.insertUser(user);
    }

    @Override
    public Completable deleteUser(User user) {
        return mUserDao.deleteUser(user);
    }

    @Override
    public Completable deleteAllUsers() {
        return mUserDao.deleteAllUsers();
    }
}
