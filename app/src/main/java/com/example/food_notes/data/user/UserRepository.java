package com.example.food_notes.data.user;

import com.example.food_notes.data.relations.UserWithFoodPosts;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class UserRepository implements UserDataSource {

    private final UserDao mUserDao;

    public UserRepository(UserDao userDao) {
        mUserDao = userDao;
    }

    @Override
    public Single<User> getUser(final String username, final String password) { return mUserDao.getUsernameAndPassword(username, password); }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return mUserDao.getAllUsersByUsername();
    }

    @Override
    public Completable insertOrUpdateUser(User user) {
        return mUserDao.insertUser(user);
    }

    @Override
    public void deleteAllUsers() {
        mUserDao.deleteAllUsers();
    }

    @Override
    public void deleteUserByUsername(final String username) {
        mUserDao.deleteUserByUsername(username);
    }

    @Override
    public Flowable<List<UserWithFoodPosts>> getUserWithPostsById(final Long id) {
        return mUserDao.getUserWithPosts(id);
    }

}
