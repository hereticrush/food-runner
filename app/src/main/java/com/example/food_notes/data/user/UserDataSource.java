package com.example.food_notes.data.user;

import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public interface UserDataSource {

    Maybe<User> getUserById(int id);

    Maybe<User> getUser(String username, String password);

    Flowable<List<User>> getAllUsers();

    Completable insertUser(User user);

    Completable deleteUserByUsername(User user);

    Completable deleteAllUsers();
}
