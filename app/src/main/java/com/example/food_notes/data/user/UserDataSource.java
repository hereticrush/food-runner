package com.example.food_notes.data.user;

import com.example.food_notes.data.relations.UserWithFoodPosts;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public interface UserDataSource {

    Flowable<User> getUser(String username, String password);

    Flowable<List<User>> getAllUsers();

    Flowable<List<UserWithFoodPosts>> getUserWithPostsById(Long id);

    Completable insertOrUpdateUser(User user);

    void deleteUserByUsername(String username);

    void deleteAllUsers();
}
