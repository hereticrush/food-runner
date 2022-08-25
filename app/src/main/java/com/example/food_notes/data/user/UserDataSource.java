package com.example.food_notes.data.user;

import com.example.food_notes.data.relations.UserWithFoodPosts;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface UserDataSource {

    Single<User> getUser(String username, String password);

    Flowable<List<User>> getAllUsers();

    Flowable<List<UserWithFoodPosts>> getUserWithPostsById(Long id);

    Completable insertOrUpdateUser(User user);

    void deleteUserByUsername(final String username);

    void deleteAllUsers();
}
