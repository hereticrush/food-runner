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
    public Maybe<User> getUserById(int id) {
       return mUserDao.getUserById(id);
    }

    @Override
    public Maybe<User> getUser(String username, String password) {
        return mUserDao.getUserByUsernameAndPassword(username, password);
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
    public Completable deleteUserByUsername(User user) {
        return mUserDao.deleteUserByUsername(user);
    }

    @Override
    public Completable deleteAllUsers() {
        return mUserDao.deleteAllUsers();
    }
}
